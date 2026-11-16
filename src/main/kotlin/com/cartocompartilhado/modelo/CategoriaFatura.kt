package com.cartocompartilhado.modelo

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Entidade que define as categorias de gastos customizáveis de cada Fatura.
 * 
 * Requisito acadêmico atendido:
 * - Entidade de domínio normalizada que permite personalização por fatura
 *   (ex: "Alimentação", "Mercado", "Assinaturas").
 */
@Entity
@Table(name = "categorias_fatura", uniqueConstraints = [
    UniqueConstraint(name = "uk_fatura_categoria", columnNames = ["fatura_id", "nome_categoria"])
])
class CategoriaFatura(
    /**
     * Identificador primário autoincrementado (BIGSERIAL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    /**
     * Fatura dona desta categoria.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fatura_id", nullable = false)
    var fatura: Fatura? = null,

    /**
     * Nome textual da categoria.
     */
    @Column(name = "nome_categoria", nullable = false, length = 50)
    var nomeCategoria: String = "",

    /**
     * Data de cadastro da categoria.
     */
    @Column(name = "criado_em")
    var criadoEm: LocalDateTime = LocalDateTime.now()
) {
    // Construtor sem argumentos para o JPA
    constructor() : this(null, null, "")
}
