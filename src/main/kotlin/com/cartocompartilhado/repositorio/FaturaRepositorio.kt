package com.cartocompartilhado.repositorio

import com.cartocompartilhado.modelo.Fatura
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Camada de Acesso a Dados (Repository) para a entidade Fatura.
 * 
 * Requisitos acadêmicos atendidos:
 * - Operações de persistência e recuperação de faturas no PostgreSQL.
 * - Suporte a busca pelo código único da fatura (ex: "ISA-3016").
 */
@Repository
interface FaturaRepositorio : JpaRepository<Fatura, Long> {

    /**
     * Localiza uma fatura através de seu código textual legível, desconsiderando maiúsculas/minúsculas.
     * @param codigoFatura Código da fatura informado pelo usuário no login ou link de compartilhamento.
     * @return Optional com a Fatura se existente.
     */
    fun findByCodigoFaturaIgnoreCase(codigoFatura: String): Optional<Fatura>
}
