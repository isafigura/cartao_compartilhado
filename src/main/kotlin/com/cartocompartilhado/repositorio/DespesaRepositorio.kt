package com.cartocompartilhado.repositorio

import com.cartocompartilhado.modelo.Despesa
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * Camada de Acesso a Dados (Repository) para a entidade Despesa.
 * 
 * Requisitos acadêmicos atendidos:
 * - Operações CRUD completas herdadas de JpaRepository.
 * - Consulta personalizada JPQL com @Query e parâmetros opcionais para filtros dinâmicos de despesas
 *   por período (data início / fim) e por categoria.
 */
@Repository
interface DespesaRepositorio : JpaRepository<Despesa, Long> {

    /**
     * Lista todas as despesas pertencentes a uma determinada fatura,
     * ordenadas cronologicamente da mais recente para a mais antiga.
     * @param faturaId Identificador numérico da fatura.
     * @return Lista de despesas encontradas.
     */
    fun findByFaturaIdOrderByDataCompraDescCriadoEmDesc(faturaId: Long): List<Despesa>

    /**
     * Filtro avançado e flexível de despesas em JPQL.
     * Permite ao usuário filtrar simultaneamente por fatura, categoria específica e intervalo de datas.
     * Caso os parâmetros de categoria ou datas sejam nulos, a cláusula correspondente é ignorada.
     */
    @Query("SELECT d FROM Despesa d WHERE d.fatura.id = :faturaId AND (:categoria IS NULL OR d.categoria = :categoria) AND (:dataInicio IS NULL OR d.dataCompra >= :dataInicio) AND (:dataFim IS NULL OR d.dataCompra <= :dataFim) ORDER BY d.dataCompra DESC")
    fun filtrarDespesas(faturaId: Long, categoria: String?, dataInicio: LocalDate?, dataFim: LocalDate?): List<Despesa>
}
