package com.cartocompartilhado.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotBlank

/**
 * DTO para login de Administrador com código da fatura e senha.
 */
data class RequisicaoLoginAdmin(
    @JsonAlias("groupCode", "codigo")
    @field:NotBlank(message = "O código da fatura é obrigatório")
    val codigoFatura: String,

    @JsonAlias("password")
    @field:NotBlank(message = "A senha é obrigatória")
    val senha: String
)

