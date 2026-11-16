package com.cartocompartilhado.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

/**
 * DTO para cadastro ou edição de uma despesa.
 * Valida os dados de entrada através do Bean Validation.
 */
data class RequisicaoCriarDespesa(
    /** Identificador numérico da despesa (caso seja atualização) */
    val id: Long? = null,

    /** Descrição do que foi comprado */
    @JsonAlias("title")
    @field:NotBlank(message = "A descrição do gasto é obrigatória")
    val descricao: String,

    /** Valor monetário total */
    @JsonAlias("amount")
    @field:NotNull(message = "O valor total é obrigatório")
    @field:DecimalMin(value = "0.01", message = "O valor da compra deve ser positivo")
    val valorTotal: BigDecimal,

    /** Categoria da compra */
    @field:NotBlank(message = "A categoria é obrigatória")
    val categoria: String,

    /** Data da compra (formato YYYY-MM-DD) */
    @JsonAlias("date")
    @field:NotBlank(message = "A data da compra é obrigatória (formato YYYY-MM-DD)")
    val dataCompra: String,

    /** Estratégia de divisão ('split_equal', 'custom', etc.) */
    @JsonAlias("assignmentType")
    val tipoDivisao: String = "split_equal",

    /** Lista com a divisão dos valores entre os participantes */
    @JsonAlias("assignments")
    val rateios: List<RequisicaoRateio> = emptyList()
)

