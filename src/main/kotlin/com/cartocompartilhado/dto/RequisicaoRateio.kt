package com.cartocompartilhado.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

/**
 * DTO representando a cota de rateio individual de um participante numa despesa.
 */
data class RequisicaoRateio(
    @JsonAlias("user")
    @field:NotBlank(message = "O nome do usuário no rateio é obrigatório")
    val usuario: String,

    @JsonAlias("amount")
    @field:NotNull(message = "O valor do rateio é obrigatório")
    @field:DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    val valor: BigDecimal,

    @JsonAlias("percentage")
    @field:NotNull(message = "A porcentagem é obrigatória")
    val porcentagem: BigDecimal,

    @JsonAlias("paid")
    val estaPago: Boolean = false,

    @JsonAlias("notifiedPaid")
    val notificouPagamentoPix: Boolean = false
)

