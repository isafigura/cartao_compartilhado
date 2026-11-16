package com.cartocompartilhado.repositorio

import com.cartocompartilhado.modelo.RateioDespesa
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Camada de Acesso a Dados (Repository) para a entidade RateioDespesa.
 * 
 * Requisitos acadêmicos atendidos:
 * - Controle de status financeiro por usuário e despesa.
 * - Suporte à consulta de rateio por nome de usuário para atualização de pagamento PIX.
 */
@Repository
interface RateioDespesaRepositorio : JpaRepository<RateioDespesa, Long> {

    /**
     * Localiza a parcela de rateio de uma despesa para um determinado usuário pelo seu nome.
     * @param despesaId Identificador numérico da despesa.
     * @param nomeUsuario Nome do participante.
     */
    fun findByDespesaIdAndUsuarioNomeIgnoreCase(despesaId: Long, nomeUsuario: String): Optional<RateioDespesa>

    /**
     * Lista todas as parcelas atribuídas a um usuário em diferentes despesas.
     * @param usuarioId Identificador do usuário.
     */
    fun findByUsuarioId(usuarioId: Long): List<RateioDespesa>
}
