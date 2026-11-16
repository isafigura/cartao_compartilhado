package com.cartocompartilhado.controladora

import com.cartocompartilhado.dto.*
import com.cartocompartilhado.servico.FaturaServico
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Camada Controladora REST para Faturas e Autenticação.
 * 
 * Requisitos acadêmicos atendidos:
 * - Padrão MVC / REST Controller do Spring Web.
 * - Endpoints com nomes semânticos em português (ex: /faturas, /autenticacao/administrador).
 * - Suporte a múltiplos mapeamentos para manter compatibilidade com o front-end Flutter.
 * - Validação automática das requisições via @Valid (Bean Validation).
 * - Códigos de status HTTP padronizados (200 OK, 201 Created, 400 Bad Request, etc.).
 * - Habilitação de Cross-Origin Resource Sharing (@CrossOrigin) para chamadas Web/Flutter.
 */
@RestController
@RequestMapping
@CrossOrigin(origins = ["*"])
class FaturaControladora(
    private val faturaServico: FaturaServico
) {

    /**
     * Endpoint de verificação de disponibilidade (Healthcheck).
     * GET /saude
     */
    @GetMapping("/saude", "/api/health")
    fun verificarSaude(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "UP", "mensagem" to "Serviço Cartão Compartilhado funcionando normalmente."))
    }

    /**
     * Retorna a lista de todas as faturas registradas.
     * GET /faturas
     */
    @GetMapping("/faturas", "/api/groups")
    fun listarFaturas(): ResponseEntity<List<Map<String, Any>>> {
        return ResponseEntity.ok(faturaServico.listarTodas())
    }

    /**
     * Busca os detalhes de uma fatura específica pelo seu ID numérico.
     * GET /faturas/{id}
     */
    @GetMapping("/faturas/{id}", "/api/groups/{id}")
    fun buscarFaturaPorId(@PathVariable id: Long): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(faturaServico.buscarPorId(id))
    }

    /**
     * Busca uma fatura pelo seu código legível (ex: "ISA-3016").
     * GET /faturas/codigo/{codigo}
     */
    @GetMapping("/faturas/codigo/{codigo}")
    fun buscarFaturaPorCodigo(@PathVariable codigo: String): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(faturaServico.buscarPorCodigo(codigo))
    }

    /**
     * Cria uma nova fatura com administrador, categorias padrão e participantes.
     * POST /faturas
     * @param req Objeto validado com as regras de Bean Validation.
     * @return Retorna HTTP 201 (Created) com os dados da fatura gerada.
     */
    @PostMapping("/faturas", "/api/groups")
    fun criarFatura(@Valid @RequestBody req: RequisicaoCriarFatura): ResponseEntity<Map<String, Any>> {
        val criada = faturaServico.criarFatura(req)
        val resposta = mapOf(
            "success" to true,
            "sucesso" to true,
            "data" to criada,
            "dados" to criada
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta)
    }

    /**
     * Atualiza dados de configuração da fatura (dia de vencimento, PIX, etc.).
     * PATCH /faturas/{id}
     */
    @PatchMapping("/faturas/{id}", "/api/groups/{id}")
    fun atualizarFatura(
        @PathVariable id: Long,
        @RequestBody req: RequisicaoAtualizarFatura
    ): ResponseEntity<Map<String, Any>> {
        val atualizada = faturaServico.atualizarFatura(id, req)
        val resposta = mapOf(
            "success" to true,
            "sucesso" to true,
            "data" to atualizada,
            "dados" to atualizada
        )
        return ResponseEntity.ok(resposta)
    }

    /**
     * Adiciona um novo participante convidado à fatura.
     * POST /faturas/{id}/convidados
     */
    @PostMapping("/faturas/{id}/convidados", "/api/groups/{id}/guests")
    fun adicionarConvidado(
        @PathVariable id: Long,
        @RequestBody dados: Map<String, String>
    ): ResponseEntity<Map<String, Any>> {
        val nome = dados["guestName"] ?: dados["nome"] ?: throw RuntimeException("Nome do convidado é obrigatório")
        val codigo = dados["customCode"] ?: dados["codigo"]
        val atualizada = faturaServico.adicionarConvidado(id, nome, codigo)
        val resposta = mapOf(
            "success" to true,
            "sucesso" to true,
            "data" to atualizada,
            "dados" to atualizada
        )
        return ResponseEntity.ok(resposta)
    }

    /**
     * Remove um participante da fatura.
     * DELETE /faturas/{id}/convidados/{nomeConvidado}
     */
    @DeleteMapping("/faturas/{id}/convidados/{nomeConvidado}", "/api/groups/{id}/guests/{nomeConvidado}")
    fun removerConvidado(
        @PathVariable id: Long,
        @PathVariable nomeConvidado: String
    ): ResponseEntity<Map<String, Any>> {
        val atualizada = faturaServico.removerConvidado(id, nomeConvidado)
        val resposta = mapOf(
            "success" to true,
            "sucesso" to true,
            "data" to atualizada,
            "dados" to atualizada
        )
        return ResponseEntity.ok(resposta)
    }

    /**
     * Realiza a autenticação do Administrador por código de fatura e senha.
     * POST /autenticacao/administrador
     */
    @PostMapping("/autenticacao/administrador", "/api/auth/admin")
    fun loginAdmin(@Valid @RequestBody req: RequisicaoLoginAdmin): ResponseEntity<Map<String, Any>> {
        val dados = faturaServico.autenticarAdmin(req)
        val resposta = mapOf(
            "success" to true,
            "sucesso" to true,
            "data" to dados,
            "dados" to dados
        )
        return ResponseEntity.ok(resposta)
    }

    /**
     * Realiza a autenticação do Convidado por código de acesso pessoal.
     * POST /autenticacao/convidado
     */
    @PostMapping("/autenticacao/convidado", "/api/auth/guest")
    fun loginConvidado(@Valid @RequestBody req: RequisicaoLoginConvidado): ResponseEntity<Map<String, Any>> {
        val dados = faturaServico.autenticarConvidado(req)
        val resposta = mapOf(
            "success" to true,
            "sucesso" to true,
            "data" to dados,
            "dados" to dados
        )
        return ResponseEntity.ok(resposta)
    }
}
