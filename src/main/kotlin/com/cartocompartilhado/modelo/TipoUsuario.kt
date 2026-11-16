package com.cartocompartilhado.modelo

/**
 * Enum que define os perfis de acesso e autorização no sistema.
 * 
 * Requisito acadêmico atendido:
 * - Uso de enumeração (enum) para tipagem forte e regras de negócio.
 */
enum class TipoUsuario(val descricao: String) {
    /**
     * Administrador: possui poderes de edição total da fatura e convidados.
     */
    ADMINISTRADOR("Administrador da Fatura"),

    /**
     * Convidado: participante com acesso aos seus gastos e dados de pagamento via PIX.
     */
    CONVIDADO("Convidado Autorizado")
}
