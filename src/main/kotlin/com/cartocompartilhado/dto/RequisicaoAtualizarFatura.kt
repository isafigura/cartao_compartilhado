package com.cartocompartilhado.dto

import com.fasterxml.jackson.annotation.JsonAlias

/**
 * DTO para atualização parcial de dados da fatura (chave PIX, datas de fechamento/vencimento, categorias e convidados).
 */
data class RequisicaoAtualizarFatura(
    @JsonAlias("pixKey")
    val chavePix: String? = null,

    @JsonAlias("adminPassword", "password")
    val senhaAdministrador: String? = null,

    @JsonAlias("closingDay")
    val diaFechamento: Int? = null,

    @JsonAlias("dueDay")
    val diaVencimento: Int? = null,

    @JsonAlias("categories")
    val categorias: List<String>? = null,

    @JsonAlias("users")
    val convidados: List<String>? = null,

    @JsonAlias("guestCodes")
    val codigosConvidados: Map<String, String>? = null
)

