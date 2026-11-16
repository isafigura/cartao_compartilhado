package com.cartocompartilhado.modelo

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Entidade que representa a Fatura do Cartão de Crédito Compartilhado.
 * 
 * Requisitos acadêmicos atendidos:
 * - Entidade central agregadora de despesas e participantes.
 * - Relacionamentos JPA:
 *   - @ManyToOne com Usuario (administrador responsável pela fatura).
 *   - @OneToMany com CategoriaFatura (categorias permitidas nessa fatura).
 *   - @OneToMany com ParticipanteFatura (relação N-para-N entre Fatura e Usuários).
 *   - @OneToMany com Despesa (todas as compras lançadas nesta fatura).
 */
@Entity
@Table(name = "faturas")
class Fatura(
    /**
     * Identificador primário autoincrementado (BIGSERIAL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    /**
     * Código legível da fatura compartilhado entre os membros (ex: "ISA-3016").
     */
    @Column(name = "codigo_fatura", nullable = false, unique = true, length = 50)
    var codigoFatura: String = "",

    /**
     * Usuário administrador proprietário do cartão.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_administrador_id", nullable = false)
    var administrador: Usuario? = null,

    /**
     * Nome amigável do cartão (ex: "Cartão Nubank Isabela").
     */
    @Column(name = "nome_cartao", length = 100)
    var nomeCartao: String = "Cartão de Crédito",

    /**
     * Chave PIX do administrador para onde os convidados transferem os pagamentos.
     */
    @Column(name = "chave_pix_recebimento", nullable = false, length = 150)
    var chavePixRecebimento: String = "",

    /**
     * Dia do mês em que a fatura fecha.
     */
    @Column(name = "dia_fechamento")
    var diaFechamento: Int = 15,

    /**
     * Dia do mês em que a fatura vence.
     */
    @Column(name = "dia_vencimento")
    var diaVencimento: Int = 22,

    /**
     * Data e hora de criação da fatura.
     */
    @Column(name = "criado_em")
    var criadoEm: LocalDateTime = LocalDateTime.now(),

    /**
     * Lista de categorias associadas à fatura.
     */
    @OneToMany(mappedBy = "fatura", cascade = [CascadeType.ALL], orphanRemoval = true)
    var categorias: MutableList<CategoriaFatura> = mutableListOf(),

    /**
     * Lista de participantes autorizados a visualizar ou lançar despesas nesta fatura.
     */
    @OneToMany(mappedBy = "fatura", cascade = [CascadeType.ALL], orphanRemoval = true)
    var participantes: MutableList<ParticipanteFatura> = mutableListOf(),

    /**
     * Lista de despesas lançadas na fatura.
     * @JsonIgnore evita loops recursivos de serialização infinita em respostas JSON.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "fatura", cascade = [CascadeType.ALL], orphanRemoval = true)
    var despesas: MutableList<Despesa> = mutableListOf()
) {
    // Construtor sem argumentos para o JPA
    constructor() : this(null, "", null)
}
