package com.cartocompartilhado.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotBlank

/**
 * DTO para atualização do status de pagamento de uma despesa.
 */
data class RequisicaoStatusPagamento(
    @field:NotBlank(message = "O nome do usuário é obrigatório")
    val usuario: String,

    @JsonAlias("paid")
    val estaPago: Boolean? = null,

    @JsonAlias("notified", "notifiedPaid")
    val notificouPagamentoPix: Boolean? = null
)

