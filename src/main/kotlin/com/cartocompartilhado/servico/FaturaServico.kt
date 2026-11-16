package com.cartocompartilhado.servico

import com.cartocompartilhado.dto.*
import com.cartocompartilhado.modelo.*
import com.cartocompartilhado.repositorio.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

/**
 * Camada de Serviço (Regras de Negócio) para Gerenciamento de Faturas e Participantes.
 * 
 * Requisitos acadêmicos atendidos:
 * - Separação da lógica de negócio da camada de controle (Controller).
 * - Transações atômicas com @Transactional para integridade relacional.
 * - Geração de códigos amigáveis de acesso pessoal para convidados.
 * - Autenticação e autorização por perfil (Administrador vs Convidado).
 */
@Service
class FaturaServico(
    private val faturaRepositorio: FaturaRepositorio,
    private val usuarioRepositorio: UsuarioRepositorio,
    private val participanteRepositorio: ParticipanteFaturaRepositorio,
    private val categoriaRepositorio: CategoriaFaturaRepositorio
) {

    /**
     * Gera um código de acesso pessoal único para um convidado baseado nas primeiras letras do seu nome
     * seguido de um número aleatório (exemplo: "EOC-1812" para Eoclides).
     */
    fun gerarCodigoConvidado(nome: String): String {
        val limpo = nome.trim().uppercase().replace(Regex("[^A-Z]"), "")
        val prefixo = if (limpo.length >= 3) limpo.substring(0, 3) else (limpo + "XXX").substring(0, 3)
        val numero = Random.nextInt(1000, 9999)
        return "$prefixo-$numero"
    }

    /**
     * Localiza um usuário pelo nome ou o cria no banco caso ainda não exista.
     * Instancia Administrador ou Convidado conforme o parâmetro 'tipo' (polimorfismo/herança).
     */
    fun buscarOuCriarUsuario(nome: String, tipo: TipoUsuario, codigoPessoal: String? = null): Usuario {
        val nomeLimpo = nome.trim()
        val existente = usuarioRepositorio.findByNomeIgnoreCase(nomeLimpo)
        if (existente.isPresent) {
            return existente.get()
        }

        // Instanciação da subclasse apropriada (Herança JPA)
        val novoUsuario = if (tipo == TipoUsuario.ADMINISTRADOR) {
            Administrador(nome = nomeLimpo, senhaHash = "123")
        } else {
            Convidado(nome = nomeLimpo, codigoAcessoPessoal = codigoPessoal)
        }
        return usuarioRepositorio.save(novoUsuario)
    }

    /**
     * Retorna a lista de todas as faturas registradas no sistema.
     */
    fun listarTodas(): List<Map<String, Any>> {
        val faturas = faturaRepositorio.findAll()
        return faturas.map { mapearParaMapa(it) }
    }

    /**
     * Busca uma fatura específica pelo seu ID numérico gerado pelo PostgreSQL.
     */
    fun buscarPorId(id: Long): Map<String, Any> {
        val fatura = faturaRepositorio.findById(id)
            .orElseThrow { RuntimeException("Fatura com ID '$id' não encontrada.") }
        return mapearParaMapa(fatura)
    }

    /**
     * Busca uma fatura pelo seu código legível (ex: "ISA-3016").
     */
    fun buscarPorCodigo(codigo: String): Map<String, Any> {
        val fatura = faturaRepositorio.findByCodigoFaturaIgnoreCase(codigo.trim())
            .orElseThrow { RuntimeException("Fatura com código '$codigo' não encontrada.") }
        return mapearParaMapa(fatura)
    }

    /**
     * Cria uma nova fatura no banco de dados.
     * Executada em bloco transacional (@Transactional):
     * 1. Valida unicidade do código da fatura.
     * 2. Cria ou atualiza o usuário administrador com sua senha.
     * 3. Salva a fatura e obtém o ID autoincremental gerado.
     * 4. Vincula o administrador como participante.
     * 5. Popula as categorias padrão (Alimentação, Transporte, Lazer, etc.).
     * 6. Registra o convidado inicial (se informado).
     */
    @Transactional
    fun criarFatura(requisicao: RequisicaoCriarFatura): Map<String, Any> {
        val codigoLimpo = requisicao.codigo.trim().uppercase()
        if (faturaRepositorio.findByCodigoFaturaIgnoreCase(codigoLimpo).isPresent) {
            throw RuntimeException("Já existe uma fatura cadastrada com o código '$codigoLimpo'.")
        }

        // Localiza ou cadastra o Administrador
        val admin = buscarOuCriarUsuario(requisicao.nomeAdministrador, TipoUsuario.ADMINISTRADOR, codigoLimpo)
        if (admin is Administrador) {
            admin.senhaHash = requisicao.senhaAdministrador.trim()
            usuarioRepositorio.save(admin)
        }

        // Cria a fatura com ID null para o PostgreSQL gerar autoincrement
        val novaFatura = Fatura(
            id = null,
            codigoFatura = codigoLimpo,
            administrador = admin,
            nomeCartao = requisicao.nomeCartao ?: "Cartão de Crédito",
            chavePixRecebimento = requisicao.chavePix.trim(),
            diaFechamento = requisicao.diaFechamento,
            diaVencimento = requisicao.diaVencimento
        )
        val faturaSalva = faturaRepositorio.save(novaFatura)
        val faturaId = faturaSalva.id ?: throw RuntimeException("Erro ao gerar ID autoincremental para a fatura.")

        // Vincula o administrador na tabela de relacionamento ParticipanteFatura
        val participanteAdmin = ParticipanteFatura(
            fatura = faturaSalva,
            usuario = admin,
            papelNaFatura = "administrador",
            codigoAcessoFatura = codigoLimpo
        )
        participanteRepositorio.save(participanteAdmin)

        // Inicializa as categorias padrão na fatura
        val categoriasPadrao = listOf("Alimentação", "Transporte", "Lazer", "Mercado", "Serviços", "Farmácia", "Assinaturas", "Outros")
        for (cat in categoriasPadrao) {
            categoriaRepositorio.save(CategoriaFatura(fatura = faturaSalva, nomeCategoria = cat))
        }

        // Adiciona convidado inicial caso fornecido
        if (!requisicao.nomeConvidadoInicial.isNullOrBlank()) {
            adicionarConvidado(faturaId, requisicao.nomeConvidadoInicial, null)
        }

        return buscarPorId(faturaId)
    }

    /**
     * Atualiza dados de configuração da fatura (chave PIX, dia de fechamento, dia de vencimento, senha, etc.).
     */
    @Transactional
    fun atualizarFatura(id: Long, req: RequisicaoAtualizarFatura): Map<String, Any> {
        val fatura = faturaRepositorio.findById(id)
            .orElseThrow { RuntimeException("Fatura não encontrada.") }

        req.chavePix?.let { fatura.chavePixRecebimento = it.trim() }
        req.diaFechamento?.let { fatura.diaFechamento = it }
        req.diaVencimento?.let { fatura.diaVencimento = it }

        if (!req.senhaAdministrador.isNullOrBlank()) {
            val admin = fatura.administrador
            if (admin is Administrador) {
                admin.senhaHash = req.senhaAdministrador.trim()
                usuarioRepositorio.save(admin)
            }
        }

        if (req.categorias != null) {
            val existentes = categoriaRepositorio.findByFaturaId(id).map { it.nomeCategoria }
            for (novaCat in req.categorias) {
                if (!existentes.contains(novaCat.trim())) {
                    categoriaRepositorio.save(CategoriaFatura(fatura = fatura, nomeCategoria = novaCat.trim()))
                }
            }
        }

        if (req.convidados != null) {
            for (nomeConvidado in req.convidados) {
                val codigoCustom = req.codigosConvidados?.get(nomeConvidado)
                adicionarConvidado(id, nomeConvidado, codigoCustom)
            }
        }

        faturaRepositorio.save(fatura)
        return buscarPorId(id)
    }

    /**
     * Adiciona um novo convidado à fatura, gerando seu código de acesso pessoal para login.
     */
    @Transactional
    fun adicionarConvidado(faturaId: Long, nomeConvidado: String, codigoCustom: String?): Map<String, Any> {
        val fatura = faturaRepositorio.findById(faturaId)
            .orElseThrow { RuntimeException("Fatura não encontrada.") }

        val nomeLimpo = nomeConvidado.trim()
        val codigo = if (!codigoCustom.isNullOrBlank()) codigoCustom.trim().uppercase() else gerarCodigoConvidado(nomeLimpo)
        val convidado = buscarOuCriarUsuario(nomeLimpo, TipoUsuario.CONVIDADO, codigo)
        val convidadoId = convidado.id ?: throw RuntimeException("ID do convidado inválido.")

        val existente = participanteRepositorio.findByFaturaIdAndUsuarioId(faturaId, convidadoId)
        if (existente.isPresent) {
            val p = existente.get()
            p.codigoAcessoFatura = codigo
            p.ativo = true
            participanteRepositorio.save(p)
        } else {
            participanteRepositorio.save(
                ParticipanteFatura(
                    fatura = fatura,
                    usuario = convidado,
                    papelNaFatura = "convidado",
                    codigoAcessoFatura = codigo
                )
            )
        }
        return buscarPorId(faturaId)
    }

    /**
     * Remove um convidado da fatura (desvinculando-o na tabela de relacionamento).
     */
    @Transactional
    fun removerConvidado(faturaId: Long, nomeConvidado: String): Map<String, Any> {
        val convidadoOpt = usuarioRepositorio.findByNomeIgnoreCase(nomeConvidado.trim())
        if (convidadoOpt.isPresent) {
            val convidadoId = convidadoOpt.get().id
            if (convidadoId != null) {
                val participanteOpt = participanteRepositorio.findByFaturaIdAndUsuarioId(faturaId, convidadoId)
                participanteOpt.ifPresent { participanteRepositorio.delete(it) }
            }
        }
        return buscarPorId(faturaId)
    }

    /**
     * Realiza o login do Administrador verificando o código da fatura e a senha.
     */
    fun autenticarAdmin(req: RequisicaoLoginAdmin): Map<String, Any> {
        val fatura = faturaRepositorio.findByCodigoFaturaIgnoreCase(req.codigoFatura.trim())
            .orElseThrow { RuntimeException("Fatura com código '${req.codigoFatura}' não encontrada.") }

        val admin = fatura.administrador
        val senhaCadastrada = if (admin is Administrador) admin.senhaHash else "123"

        if (senhaCadastrada != req.senha.trim()) {
            throw RuntimeException("Senha de administrador incorreta.")
        }

        return mapOf(
            "sucesso" to true,
            "papel" to "admin",
            "nomeUsuario" to (admin?.nome ?: "Admin"),
            "fatura" to mapearParaMapa(fatura)
        )
    }

    /**
     * Realiza o login do Convidado utilizando apenas seu código de acesso pessoal (ex: "EOC-1812").
     */
    fun autenticarConvidado(req: RequisicaoLoginConvidado): Map<String, Any> {
        val codigoLimpo = req.codigoAcesso.trim().uppercase()
        val participante = participanteRepositorio.findByCodigoAcessoFaturaIgnoreCase(codigoLimpo)
            .orElseThrow { RuntimeException("Nenhum convidado encontrado com o código de acesso '$codigoLimpo'.") }

        val fatura = participante.fatura ?: throw RuntimeException("Fatura associada não encontrada.")
        val usuario = participante.usuario ?: throw RuntimeException("Usuário não encontrado.")

        return mapOf(
            "sucesso" to true,
            "papel" to "guest",
            "nomeUsuario" to usuario.nome,
            "fatura" to mapearParaMapa(fatura)
        )
    }

    /**
     * Converte a entidade Fatura em um mapa JSON completo, com suporte a chaves em português
     * e compatibilidade retroativa para as chaves em inglês consumidas pelo Flutter.
     */
    private fun mapearParaMapa(fatura: Fatura): Map<String, Any> {
        val faturaId = fatura.id ?: 0L
        val participantes = participanteRepositorio.findByFaturaId(faturaId)
        val categorias = categoriaRepositorio.findByFaturaId(faturaId).map { it.nomeCategoria }

        val usuariosConvidados = mutableListOf<String>()
        val codigosConvidados = mutableMapOf<String, String>()

        for (p in participantes) {
            val u = p.usuario ?: continue
            if (p.papelNaFatura == "convidado") {
                usuariosConvidados.add(u.nome)
                p.codigoAcessoFatura?.let { codigosConvidados[u.nome] = it }
            }
        }

        val adminNome = fatura.administrador?.nome ?: "Admin"
        val adminSenha = if (fatura.administrador is Administrador) (fatura.administrador as Administrador).senhaHash else "123"

        return mapOf(
            "id" to faturaId,
            "code" to fatura.codigoFatura,
            "codigo" to fatura.codigoFatura,
            "adminName" to adminNome,
            "nomeAdministrador" to adminNome,
            "adminPassword" to adminSenha,
            "pixKey" to fatura.chavePixRecebimento,
            "chavePix" to fatura.chavePixRecebimento,
            "cardName" to fatura.nomeCartao,
            "nomeCartao" to fatura.nomeCartao,
            "closingDay" to fatura.diaFechamento,
            "diaFechamento" to fatura.diaFechamento,
            "dueDay" to fatura.diaVencimento,
            "diaVencimento" to fatura.diaVencimento,
            "categories" to categorias,
            "categorias" to categorias,
            "users" to usuariosConvidados,
            "convidados" to usuariosConvidados,
            "guestCodes" to codigosConvidados,
            "codigosConvidados" to codigosConvidados
        )
    }
}
