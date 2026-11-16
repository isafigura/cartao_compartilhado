package com.cartocompartilhado.controladora

import com.cartocompartilhado.dto.RespostaPadrao
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Interceptador Global de Exceções da API (@RestControllerAdvice).
 * 
 * Requisitos acadêmicos atendidos:
 * - Tratamento centralizado e elegante de erros e exceções.
 * - Captura erros de validação (@Valid) e formata mensagens legíveis em português para o usuário.
 * - Evita vazamento de stack traces internos do servidor para o cliente.
 */
@RestControllerAdvice
class TratadorGlobalDeErros {

    /**
     * Trata erros de validação dos campos anotados com Bean Validation (ex: @NotBlank, @NotNull, @DecimalMin).
     * Retorna HTTP 400 (Bad Request) com a lista de campos inválidos e suas respectivas mensagens.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun tratarErrosDeValidacao(ex: MethodArgumentNotValidException): ResponseEntity<RespostaPadrao<Nothing>> {
        val erros = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            RespostaPadrao(
                sucesso = false,
                mensagem = "Erro de validação nos campos da requisição.",
                erros = erros
            )
        )
    }

    /**
     * Trata exceções de regras de negócio (RuntimeException) lançadas pelas camadas de serviço.
     * Retorna HTTP 400 (Bad Request) com a mensagem explicativa.
     */
    @ExceptionHandler(RuntimeException::class)
    fun tratarErrosDeNegocio(ex: RuntimeException): ResponseEntity<RespostaPadrao<Nothing>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            RespostaPadrao(
                sucesso = false,
                mensagem = ex.message ?: "Ocorreu um erro ao processar a requisição."
            )
        )
    }

    /**
     * Captura qualquer outra exceção genérica não tratada previamente.
     * Retorna HTTP 500 (Internal Server Error) de maneira controlada.
     */
    @ExceptionHandler(Exception::class)
    fun tratarErrosInesperados(ex: Exception): ResponseEntity<RespostaPadrao<Nothing>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            RespostaPadrao(
                sucesso = false,
                mensagem = "Erro interno no servidor: ${ex.message}"
            )
        )
    }
}
