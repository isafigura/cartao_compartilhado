package com.cartocompartilhado.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotBlank

/**
 * DTO para login de Convidado utilizando apenas seu código de acesso pessoal.
 */
data class RequisicaoLoginConvidado(
    @JsonAlias("guestCode", "codigo")
    @field:NotBlank(message = "O código do convidado é obrigatório")
    val codigoAcesso: String
)

