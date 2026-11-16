package com.cartocompartilhado.modelo

/**
 * Enum que representa as estratégias de rateio de uma despesa.
 * 
 * Requisito acadêmico atendido:
 * - Uso de enumeração (enum) para padronizar os tipos de divisão de despesas.
 */
enum class TipoDivisao(val descricao: String) {
    /**
     * O valor da compra é dividido igualmente entre os participantes selecionados.
     */
    DIVISAO_IGUAL("Divisão Igual"),

    /**
     * O gasto é atribuído 100% ao titular do cartão.
     */
    SOMENTE_TITULAR("Somente Titular"),

    /**
     * Cada participante paga uma quantia ou porcentagem arbitrária definida manualmente.
     */
    DIVISAO_PERSONALIZADA("Divisão Personalizada"),

    /**
     * O gasto é atribuído 100% a um convidado específico.
     */
    ATRIBUIDO_INTEGRALMENTE("Atribuído Integralmente a Convidado")
}
