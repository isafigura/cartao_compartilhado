package com.cartocompartilhado.controladora

import com.cartocompartilhado.dto.*
import com.cartocompartilhado.servico.DespesaServico
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * Camada Controladora REST para Gerenciamento de Despesas.
 * 
 * Requisitos acadêmicos atendidos:
 * - Implementação dos métodos do CRUD completo:
 *   - [C] Criar: POST /despesas e POST /faturas/{faturaId}/despesas
 *   - [R] Ler: GET /despesas, GET /despesas/{id} e GET /faturas/{faturaId}/despesas
 *   - [U] Atualizar: POST /despesas (com ID existente) e POST /despesas/{id}/pagamento
 *   - [D] Deletar: DELETE /despesas/{id}
 * - Recursos avançados:
 *   - Filtros de consulta por data e categoria (GET /faturas/{faturaId}/despesas/filtro)
 *   - Geração de Relatório de Fechamento de Fatura (GET /faturas/{faturaId}/relatorio)
 */
@RestController
@RequestMapping
@CrossOrigin(origins = ["*"])
class DespesaControladora(
    private val despesaServico: DespesaServico
) {

    /**
     * Lista todas as despesas cadastradas no sistema.
     * GET /despesas
     */
    @GetMapping("/despesas", "/api/expenses")
    fun listarTodasDespesas(): ResponseEntity<List<Map<String, Any>>> {
        return ResponseEntity.ok(despesaServico.listarTodas())
    }

    /**
     * Lista todas as despesas pertencentes a uma fatura específica.
     * GET /faturas/{faturaId}/despesas
     */
    @GetMapping("/faturas/{faturaId}/despesas", "/api/groups/{faturaId}/expenses")
    fun listarDespesasPorFatura(@PathVariable faturaId: Long): ResponseEntity<List<Map<String, Any>>> {
        return ResponseEntity.ok(despesaServico.listarPorFatura(faturaId))
    }

    /**
     * Busca uma despesa específica pelo seu identificador primário.
     * GET /despesas/{id}
     */
    @GetMapping("/despesas/{id}", "/api/expenses/{id}")
    fun buscarDespesaPorId(@PathVariable id: Long): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(despesaServico.buscarPorId(id))
    }

    /**
     * Cria ou atualiza uma despesa.
     * POST /despesas
     * @return Retorna HTTP 201 (Created).
     */
    @PostMapping("/despesas", "/api/expenses")
    fun criarOuAtualizarDespesa(
        @Valid @RequestBody req: RequisicaoCriarDespesa
    ): ResponseEntity<Map<String, Any>> {
        val salva = despesaServico.salvarDespesa(req)
        val resposta = mapOf(
            "success" to true,
            "sucesso" to true,
            "data" to salva,
            "dados" to salva
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta)
    }

    /**
     * Cria uma despesa vinculada diretamente ao ID de uma fatura informada na URL.
     * POST /faturas/{faturaId}/despesas
     */
    @PostMapping("/faturas/{faturaId}/despesas", "/api/groups/{faturaId}/expenses")
    fun criarDespesaParaFatura(
        @PathVariable faturaId: Long,
        @Valid @RequestBody req: RequisicaoCriarDespesa
    ): ResponseEntity<Map<String, Any>> {
        val salva = despesaServico.salvarDespesa(req, faturaId)
        val resposta = mapOf(
            "success" to true,
            "sucesso" to true,
            "data" to salva,
            "dados" to salva
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta)
    }

    /**
     * Exclui uma despesa do banco de dados (Operação DELETE do CRUD).
     * DELETE /despesas/{id}
     */
    @DeleteMapping("/despesas/{id}", "/api/expenses/{id}")
    fun excluirDespesa(@PathVariable id: Long): ResponseEntity<Map<String, Any>> {
        despesaServico.excluirDespesa(id)
        val resposta = mapOf(
            "success" to true,
            "sucesso" to true,
            "excluido" to true,
            "data" to mapOf("deleted" to true)
        )
        return ResponseEntity.ok(resposta)
    }

    /**
     * Atualiza o status de pagamento de um participante em uma despesa.
     * POST /despesas/{id}/pagamento
     */
    @PostMapping("/despesas/{id}/pagamento", "/api/expenses/{id}/payment")
    fun atualizarStatusPagamento(
        @PathVariable id: Long,
        @Valid @RequestBody req: RequisicaoStatusPagamento
    ): ResponseEntity<Map<String, Any>> {
        val atualizada = despesaServico.atualizarStatusPagamento(id, req)
        val resposta = mapOf(
            "success" to true,
            "sucesso" to true,
            "data" to atualizada,
            "dados" to atualizada
        )
        return ResponseEntity.ok(resposta)
    }

    /**
     * Recurso adicional: Filtro personalizado de despesas por fatura, categoria e período.
     * GET /faturas/{faturaId}/despesas/filtro?categoria=Mercado&dataInicio=2026-11-01&dataFim=2026-11-30
     */
    @GetMapping("/faturas/{faturaId}/despesas/filtro")
    fun filtrarDespesas(
        @PathVariable faturaId: Long,
        @RequestParam(required = false) categoria: String?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dataInicio: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dataFim: LocalDate?
    ): ResponseEntity<List<Map<String, Any>>> {
        return ResponseEntity.ok(despesaServico.filtrarDespesas(faturaId, categoria, dataInicio, dataFim))
    }

    /**
     * Recurso adicional: Geração do Relatório Consolidado de Fechamento da Fatura.
     * GET /faturas/{faturaId}/relatorio
     */
    @GetMapping("/faturas/{faturaId}/relatorio")
    fun gerarRelatorioFatura(@PathVariable faturaId: Long): ResponseEntity<RelatorioResumoFatura> {
        return ResponseEntity.ok(despesaServico.gerarRelatorioFatura(faturaId))
    }
}
