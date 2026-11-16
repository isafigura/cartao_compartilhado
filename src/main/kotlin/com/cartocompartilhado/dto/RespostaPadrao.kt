package com.cartocompartilhado.dto

/**
 * DTO genérico que padroniza o envelope de resposta da API para operações que retornam dados, mensagens e lista de erros.
 */
data class RespostaPadrao<T>(
    val sucesso: Boolean,
    val dados: T? = null,
    val mensagem: String? = null,
    val erros: List<String>? = null
)

