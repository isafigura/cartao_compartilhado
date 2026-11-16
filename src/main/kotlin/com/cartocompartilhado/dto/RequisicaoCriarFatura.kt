package com.cartocompartilhado.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotBlank

/**
 * DTO para requisição de criação de uma nova Fatura.
 * Utiliza Bean Validation para garantir preenchimento de campos obrigatórios.
 */
data class RequisicaoCriarFatura(
    /** Código de identificação desejado para a fatura */
    @JsonAlias("code", "codigoFatura")
    @field:NotBlank(message = "O código da fatura é obrigatório")
    val codigo: String,

    /** Nome do administrador titular */
    @JsonAlias("adminName", "nome")
    @field:NotBlank(message = "O nome do administrador é obrigatório")
    val nomeAdministrador: String,

    /** Senha de administração da fatura */
    @JsonAlias("adminPassword", "password", "senha")
    @field:NotBlank(message = "A senha do administrador é obrigatória")
    val senhaAdministrador: String,

    /** Chave PIX do titular para recebimento dos rateios */
    @JsonAlias("pixKey", "chave_pix")
    @field:NotBlank(message = "A chave PIX é obrigatória")
    val chavePix: String,

    /** Nome descritivo do cartão */
    @JsonAlias("cardName")
    val nomeCartao: String? = "Cartão de Crédito",

    /** Dia de fechamento mensal */
    @JsonAlias("closingDay")
    val diaFechamento: Int = 15,

    /** Dia de vencimento da fatura */
    @JsonAlias("dueDay")
    val diaVencimento: Int = 22,

    /** Convidado opcional cadastrado no momento da criação da fatura */
    @JsonAlias("guestName")
    val nomeConvidadoInicial: String? = null
)

