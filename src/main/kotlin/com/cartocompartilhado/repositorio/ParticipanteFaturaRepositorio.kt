package com.cartocompartilhado.repositorio

import com.cartocompartilhado.modelo.ParticipanteFatura
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Camada de Acesso a Dados (Repository) para a tabela de associação ParticipanteFatura.
 * 
 * Requisitos acadêmicos atendidos:
 * - Consulta de relacionamentos N-para-N entre faturas e usuários.
 * - Suporte a busca por código de acesso do participante para o login seguro de convidados.
 */
@Repository
interface ParticipanteFaturaRepositorio : JpaRepository<ParticipanteFatura, Long> {

    /**
     * Retorna a lista de todos os participantes cadastrados em uma fatura.
     * @param faturaId Identificador numérico da fatura.
     */
    fun findByFaturaId(faturaId: Long): List<ParticipanteFatura>

    /**
     * Verifica o vínculo específico entre uma fatura e um usuário.
     * @param faturaId Identificador da fatura.
     * @param usuarioId Identificador do usuário.
     */
    fun findByFaturaIdAndUsuarioId(faturaId: Long, usuarioId: Long): Optional<ParticipanteFatura>

    /**
     * Localiza o participante pelo seu código de acesso pessoal para login de convidado.
     * @param codigoAcesso Código de acesso (ex: "EOC-1812").
     */
    fun findByCodigoAcessoFaturaIgnoreCase(codigoAcesso: String): Optional<ParticipanteFatura>
}
