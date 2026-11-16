package com.cartocompartilhado.modelo

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Entidade que armazena a divisão (rateio) de uma Despesa para um Usuário específico.
 * 
 * Requisitos acadêmicos atendidos:
 * - Controle granular financeiro: registra a cota-parte de cada pessoa na compra,
 *   a porcentagem, se ela já realizou o pagamento e se confirmou envio via PIX.
 * - Relacionamentos JPA:
 *   - @ManyToOne com Despesa.
 *   - @ManyToOne com Usuario.
 */
@Entity
@Table(name = "rateio_despesas", uniqueConstraints = [
    UniqueConstraint(name = "uk_rateio_despesa_usuario", columnNames = ["despesa_id", "usuario_id"])
])
class RateioDespesa(
    /**
     * Identificador primário autoincrementado (BIGSERIAL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    /**
     * Despesa de origem deste rateio.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "despesa_id", nullable = false)
    var despesa: Despesa? = null,

    /**
     * Usuário que deve pagar esta parcela.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    var usuario: Usuario? = null,

    /**
     * Quantia monetária atribuída a este usuário na despesa.
     */
    @Column(name = "valor_atribuido", nullable = false, precision = 12, scale = 2)
    var valorAtribuido: BigDecimal = BigDecimal.ZERO,

    /**
     * Percentual correspondente da despesa (ex: 50.00%).
     */
    @Column(name = "porcentagem", nullable = false, precision = 5, scale = 2)
    var porcentagem: BigDecimal = BigDecimal.ZERO,

    /**
     * Status indicando se o usuário já quitou este valor com o administrador.
     */
    @Column(name = "esta_pago", nullable = false)
    var estaPago: Boolean = false,

    /**
     * Notificação do usuário informando que realizou a transferência PIX.
     */
    @Column(name = "notificou_pagamento_pix", nullable = false)
    var notificouPagamentoPix: Boolean = false,

    /**
     * Timestamp do momento em que o pagamento foi registrado como quitado.
     */
    @Column(name = "pago_em")
    var pagoEm: LocalDateTime? = null
) {
    // Construtor sem argumentos para o JPA
    constructor() : this(null, null, null)
}
