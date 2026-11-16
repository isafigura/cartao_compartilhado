package com.cartocompartilhado.modelo

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entidade que representa uma Despesa / Compra realizada no cartão.
 * 
 * Requisitos acadêmicos atendidos:
 * - Entidade com operações de CRUD completas (Criar, Ler, Atualizar, Excluir).
 * - Relacionamentos JPA:
 *   - @ManyToOne com Fatura (a fatura à qual a compra pertence).
 *   - @ManyToOne com Usuario (quem realizou ou cadastrou a compra).
 *   - @OneToMany com RateioDespesa (como o valor desta compra foi dividido entre os membros).
 */
@Entity
@Table(name = "despesas")
class Despesa(
    /**
     * Identificador primário autoincremental (BIGSERIAL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    /**
     * Fatura associada à compra.
     * @JsonIgnore evita ciclo de serialização Fatura -> Despesa -> Fatura.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fatura_id", nullable = false)
    var fatura: Fatura? = null,

    /**
     * Usuário responsável pelo lançamento da despesa.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criado_por_usuario_id", nullable = false)
    var criadoPor: Usuario? = null,

    /**
     * Título ou descrição do gasto (ex: "Supermercado Semanal").
     */
    @Column(name = "descricao", nullable = false, length = 200)
    var descricao: String = "",

    /**
     * Valor monetário total da compra.
     * Utiliza BigDecimal para precisão contábil e evitar erros de ponto flutuante.
     */
    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    var valorTotal: BigDecimal = BigDecimal.ZERO,

    /**
     * Categoria do gasto (ex: "Mercado", "Alimentação", "Transporte").
     */
    @Column(name = "categoria", nullable = false, length = 50)
    var categoria: String = "Outros",

    /**
     * Data em que a compra ocorreu.
     */
    @Column(name = "data_compra", nullable = false)
    var dataCompra: LocalDate = LocalDate.now(),

    /**
     * Modo como a despesa foi distribuída ('split_equal', 'custom', etc.).
     */
    @Column(name = "tipo_divisao", nullable = false, length = 30)
    var tipoDivisao: String = "split_equal",

    /**
     * Timestamp de cadastro no banco de dados.
     */
    @Column(name = "criado_em")
    var criadoEm: LocalDateTime = LocalDateTime.now(),

    /**
     * Rateios individuais (quantia devida por cada participante desta despesa).
     */
    @OneToMany(mappedBy = "despesa", cascade = [CascadeType.ALL], orphanRemoval = true)
    var rateios: MutableList<RateioDespesa> = mutableListOf()
) {
    // Construtor sem argumentos para o JPA
    constructor() : this(null, null, null, "")
}
