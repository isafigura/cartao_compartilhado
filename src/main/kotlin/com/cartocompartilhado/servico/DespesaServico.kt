package com.cartocompartilhado.servico

import com.cartocompartilhado.dto.*
import com.cartocompartilhado.modelo.*
import com.cartocompartilhado.repositorio.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Camada de Serviço (Regras de Negócio) para Gerenciamento de Despesas e Rateios.
 * 
 * Requisitos acadêmicos atendidos:
 * - Operações de CRUD completas da entidade Despesa (Criar, Consultar, Atualizar e Deletar).
 * - Algoritmo de cálculo e persistência dos rateios por participante.
 * - Filtros personalizados com consulta JPQL.
 * - Relatório consolidado de fechamento com totais pagos e pendentes.
 */
@Service
class DespesaServico(
    private val despesaRepositorio: DespesaRepositorio,
    private val faturaRepositorio: FaturaRepositorio,
    private val usuarioRepositorio: UsuarioRepositorio,
    private val rateioRepositorio: RateioDespesaRepositorio
) {

    /**
     * Lista todas as despesas cadastradas no sistema.
     */
    fun listarTodas(): List<Map<String, Any>> {
        val despesas = despesaRepositorio.findAll()
        return despesas.map { mapearParaMapa(it) }
    }

    /**
     * Lista todas as despesas vinculadas a uma fatura específica.
     * @param faturaId ID numérico da fatura.
     */
    fun listarPorFatura(faturaId: Long): List<Map<String, Any>> {
        val despesas = despesaRepositorio.findByFaturaIdOrderByDataCompraDescCriadoEmDesc(faturaId)
        return despesas.map { mapearParaMapa(it) }
    }

    /**
     * Busca uma despesa específica pelo seu ID.
     * @param id ID numérico da despesa.
     */
    fun buscarPorId(id: Long): Map<String, Any> {
        val despesa = despesaRepositorio.findById(id)
            .orElseThrow { RuntimeException("Despesa com ID '$id' não encontrada.") }
        return mapearParaMapa(despesa)
    }

    /**
     * Cria uma nova despesa ou atualiza uma existente.
     * Operação transacional (@Transactional):
     * 1. Vincula à fatura especificada (ou primeira fatura cadastrada).
     * 2. Persiste a entidade Despesa com ID autoincremental (caso nova).
     * 3. Atualiza os rateios para cada participante envolvido na compra.
     */
    @Transactional
    fun salvarDespesa(req: RequisicaoCriarDespesa, faturaIdPadrao: Long? = null): Map<String, Any> {
        val faturas = faturaRepositorio.findAll()
        val fatura = if (faturaIdPadrao != null) {
            faturaRepositorio.findById(faturaIdPadrao).orElse(faturas.firstOrNull())
        } else {
            faturas.firstOrNull()
        } ?: throw RuntimeException("Nenhuma fatura encontrada para associar a despesa.")

        val criador = fatura.administrador ?: usuarioRepositorio.findAll().firstOrNull()
        ?: throw RuntimeException("Nenhum usuário disponível como criador.")

        val dataParsed = try {
            LocalDate.parse(req.dataCompra)
        } catch (e: Exception) {
            LocalDate.now()
        }

        // Se a requisição contiver ID existente, trata-se de uma atualização; caso contrário, nova inserção
        val despesa = if (req.id != null) {
            val existente = despesaRepositorio.findById(req.id).orElse(null)
            if (existente != null) {
                existente.fatura = fatura
                existente.descricao = req.descricao
                existente.valorTotal = req.valorTotal
                existente.categoria = req.categoria
                existente.dataCompra = dataParsed
                existente.tipoDivisao = req.tipoDivisao
                existente
            } else {
                Despesa(
                    id = null,
                    fatura = fatura,
                    criadoPor = criador,
                    descricao = req.descricao,
                    valorTotal = req.valorTotal,
                    categoria = req.categoria,
                    dataCompra = dataParsed,
                    tipoDivisao = req.tipoDivisao
                )
            }
        } else {
            Despesa(
                id = null,
                fatura = fatura,
                criadoPor = criador,
                descricao = req.descricao,
                valorTotal = req.valorTotal,
                categoria = req.categoria,
                dataCompra = dataParsed,
                tipoDivisao = req.tipoDivisao
            )
        }

        val despesaSalva = despesaRepositorio.save(despesa)

        // Limpa rateios anteriores se for atualização
        despesaSalva.rateios.clear()

        // Registra cada participante e sua cota no rateio
        for (r in req.rateios) {
            val usuarioNome = r.usuario.trim()
            val usuario = usuarioRepositorio.findByNomeIgnoreCase(usuarioNome).orElseGet {
                val novoConvidado = Convidado(
                    nome = usuarioNome
                )
                usuarioRepositorio.save(novoConvidado)
            }

            val rateio = RateioDespesa(
                despesa = despesaSalva,
                usuario = usuario,
                valorAtribuido = r.valor,
                porcentagem = r.porcentagem,
                estaPago = r.estaPago,
                notificouPagamentoPix = r.notificouPagamentoPix,
                pagoEm = if (r.estaPago) LocalDateTime.now() else null
            )
            rateioRepositorio.save(rateio)
            despesaSalva.rateios.add(rateio)
        }

        return mapearParaMapa(despesaSalva)
    }

    /**
     * Exclui uma despesa e automaticamente remove seus rateios por cascade.
     * Atende ao 'D' (Delete) do CRUD.
     */
    @Transactional
    fun excluirDespesa(id: Long) {
        if (!despesaRepositorio.existsById(id)) {
            throw RuntimeException("Despesa com ID '$id' não existe.")
        }
        despesaRepositorio.deleteById(id)
    }

    /**
     * Atualiza o status de quitação de uma despesa para um determinado usuário.
     * Permite marcar como pago ou registrar que o usuário notificou o envio do PIX.
     */
    @Transactional
    fun atualizarStatusPagamento(despesaId: Long, req: RequisicaoStatusPagamento): Map<String, Any> {
        val rateio = rateioRepositorio.findByDespesaIdAndUsuarioNomeIgnoreCase(despesaId, req.usuario.trim())
            .orElseThrow { RuntimeException("Rateio não encontrado para o usuário '${req.usuario}' nesta despesa.") }

        req.estaPago?.let {
            rateio.estaPago = it
            if (it) {
                rateio.pagoEm = LocalDateTime.now()
                rateio.notificouPagamentoPix = false
            } else {
                rateio.pagoEm = null
            }
        }
        req.notificouPagamentoPix?.let {
            rateio.notificouPagamentoPix = it
        }

        rateioRepositorio.save(rateio)
        return buscarPorId(despesaId)
    }

    /**
     * Recurso adicional: Filtros personalizados de despesas por categoria e intervalo de datas.
     */
    fun filtrarDespesas(faturaId: Long, categoria: String?, dataInicio: LocalDate?, dataFim: LocalDate?): List<Map<String, Any>> {
        val despesas = despesaRepositorio.filtrarDespesas(faturaId, categoria, dataInicio, dataFim)
        return despesas.map { mapearParaMapa(it) }
    }

    /**
     * Recurso adicional: Relatório consolidado de fechamento de fatura.
     * Realiza os somatórios contábeis de:
     * - Total geral gasto no período.
     * - Total já pago pelos membros.
     * - Total ainda pendente de acerto.
     * - Detalhamento da dívida por participante.
     */
    fun gerarRelatorioFatura(faturaId: Long): RelatorioResumoFatura {
        val fatura = faturaRepositorio.findById(faturaId)
            .orElseThrow { RuntimeException("Fatura não encontrada para gerar relatório.") }

        val despesas = despesaRepositorio.findByFaturaIdOrderByDataCompraDescCriadoEmDesc(faturaId)

        var totalGasto = BigDecimal.ZERO
        var totalPago = BigDecimal.ZERO
        var totalPendente = BigDecimal.ZERO
        val resumoPorUsuario = mutableMapOf<String, BigDecimal>()

        for (d in despesas) {
            totalGasto = totalGasto.add(d.valorTotal)
            for (r in d.rateios) {
                val nomeUser = r.usuario?.nome ?: "Desconhecido"
                resumoPorUsuario[nomeUser] = resumoPorUsuario.getOrDefault(nomeUser, BigDecimal.ZERO).add(r.valorAtribuido)

                if (r.estaPago) {
                    totalPago = totalPago.add(r.valorAtribuido)
                } else {
                    totalPendente = totalPendente.add(r.valorAtribuido)
                }
            }
        }

        return RelatorioResumoFatura(
            idFatura = fatura.id ?: 0L,
            codigoFatura = fatura.codigoFatura,
            nomeCartao = fatura.nomeCartao,
            totalGasto = totalGasto,
            totalPago = totalPago,
            totalPendente = totalPendente,
            quantidadeDespesas = despesas.size,
            resumoPorUsuario = resumoPorUsuario
        )
    }

    /**
     * Converte a entidade Despesa e seus rateios em um mapa JSON estruturado.
     */
    private fun mapearParaMapa(despesa: Despesa): Map<String, Any> {
        val rateiosMapeados = despesa.rateios.map {
            mapOf(
                "id" to (it.id ?: 0L),
                "user" to (it.usuario?.nome ?: ""),
                "usuario" to (it.usuario?.nome ?: ""),
                "amount" to it.valorAtribuido,
                "valor" to it.valorAtribuido,
                "percentage" to it.porcentagem,
                "porcentagem" to it.porcentagem,
                "paid" to it.estaPago,
                "estaPago" to it.estaPago,
                "notifiedPaid" to it.notificouPagamentoPix,
                "notificouPagamentoPix" to it.notificouPagamentoPix
            )
        }

        val despesaId = despesa.id ?: 0L
        val faturaId = despesa.fatura?.id ?: 0L

        return mapOf(
            "id" to despesaId,
            "groupId" to faturaId,
            "idFatura" to faturaId,
            "title" to despesa.descricao,
            "descricao" to despesa.descricao,
            "amount" to despesa.valorTotal,
            "valorTotal" to despesa.valorTotal,
            "category" to despesa.categoria,
            "categoria" to despesa.categoria,
            "date" to despesa.dataCompra.toString(),
            "dataCompra" to despesa.dataCompra.toString(),
            "assignmentType" to despesa.tipoDivisao,
            "tipoDivisao" to despesa.tipoDivisao,
            "assignments" to rateiosMapeados,
            "rateios" to rateiosMapeados
        )
    }
}
