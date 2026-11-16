package com.cartocompartilhado.repositorio

import com.cartocompartilhado.modelo.CategoriaFatura
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Camada de Acesso a Dados (Repository) para as categorias de cada fatura.
 * 
 * Requisitos acadêmicos atendidos:
 * - Operações de listagem e remoção de categorias configuradas por fatura.
 */
@Repository
interface CategoriaFaturaRepositorio : JpaRepository<CategoriaFatura, Long> {

    /**
     * Lista todas as categorias cadastradas para uma fatura específica.
     * @param faturaId Identificador da fatura.
     */
    fun findByFaturaId(faturaId: Long): List<CategoriaFatura>

    /**
     * Remove uma categoria específica associada a uma fatura.
     * @param faturaId Identificador da fatura.
     * @param nomeCategoria Nome da categoria a ser excluída.
     */
    fun deleteByFaturaIdAndNomeCategoria(faturaId: Long, nomeCategoria: String)
}
