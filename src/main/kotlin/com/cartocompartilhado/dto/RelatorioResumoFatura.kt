package com.cartocompartilhado.dto

import java.math.BigDecimal

/**
 * DTO para o Relatório de Fechamento de Fatura.
 * Consolida somatórios contábeis de gastos, pagamentos e valores pendentes.
 */
data class RelatorioResumoFatura(
    val idFatura: Long,
    val codigoFatura: String,
    val nomeCartao: String,
    val totalGasto: BigDecimal,
    val totalPago: BigDecimal,
    val totalPendente: BigDecimal,
    val quantidadeDespesas: Int,
    val resumoPorUsuario: Map<String, BigDecimal>
)

