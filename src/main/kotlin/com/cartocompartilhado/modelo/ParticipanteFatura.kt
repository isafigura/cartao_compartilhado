package com.cartocompartilhado.modelo

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Entidade de junção que modela a relação N-para-N entre Faturas e Usuários.
 * 
 * Requisito acadêmico atendido:
 * - Tabela intermediária de relacionamento N-para-N rica em atributos:
 *   armazena qual é o papel do usuário na fatura (administrador ou convidado)
 *   e o código de acesso exclusivo para aquela fatura específica.
 */
@Entity
@Table(name = "participantes_fatura", uniqueConstraints = [
    UniqueConstraint(name = "uk_participante_fatura", columnNames = ["fatura_id", "usuario_id"])
])
class ParticipanteFatura(
    /**
     * Identificador primário autoincrementado (BIGSERIAL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    /**
     * Referência à fatura vinculada.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fatura_id", nullable = false)
    var fatura: Fatura? = null,

    /**
     * Referência ao usuário participante.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    var usuario: Usuario? = null,

    /**
     * Papel desempenhado pelo usuário nesta fatura: "administrador" ou "convidado".
     */
    @Column(name = "papel_na_fatura", nullable = false, length = 20)
    var papelNaFatura: String = "convidado",

    /**
     * Código de acesso utilizado por este usuário para acessar os dados da fatura.
     */
    @Column(name = "codigo_acesso_fatura", length = 50)
    var codigoAcessoFatura: String? = null,

    /**
     * Indica se a participação está ativa.
     */
    @Column(name = "ativo", nullable = false)
    var ativo: Boolean = true,

    /**
     * Data em que o participante ingressou na fatura.
     */
    @Column(name = "criado_em")
    var criadoEm: LocalDateTime = LocalDateTime.now()
) {
    // Construtor sem argumentos para o JPA
    constructor() : this(null, null, null)
}
