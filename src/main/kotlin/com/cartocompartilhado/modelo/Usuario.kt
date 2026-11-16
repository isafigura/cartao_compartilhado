package com.cartocompartilhado.modelo

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Entidade base que representa um Usuário no sistema.
 * 
 * Requisito acadêmico atendido:
 * - Herança orientada a objetos com JPA: Classe mãe (Usuario) e filhas (Administrador, Convidado).
 * - Estratégia de herança: SINGLE_TABLE (armazena todos os tipos de usuário na mesma tabela 'usuarios',
 *   diferenciando-os pela coluna discriminadora 'tipo_usuario').
 */
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
open class Usuario(
    /**
     * Identificador único do usuário no banco de dados.
     * Gerado de forma autoincremental através da estratégia IDENTITY (BIGSERIAL no PostgreSQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    open var id: Long? = null,

    /**
     * Nome completo do usuário. Campo obrigatório.
     */
    @Column(name = "nome", nullable = false, length = 100)
    open var nome: String = "",

    /**
     * E-mail para contato ou notificações (opcional).
     */
    @Column(name = "email", length = 150)
    open var email: String? = null,

    /**
     * Telefone celular do participante (opcional).
     */
    @Column(name = "telefone", length = 20)
    open var telefone: String? = null,

    /**
     * Chave PIX cadastrada para recebimento ou envio de reembolsos.
     */
    @Column(name = "chave_pix_padrao", length = 150)
    open var chavePixPadrao: String? = null,

    /**
     * Flag que indica se o usuário está ativo no sistema.
     */
    @Column(name = "ativo", nullable = false)
    open var ativo: Boolean = true,

    /**
     * Data e hora do cadastro do usuário.
     */
    @Column(name = "criado_em")
    open var criadoEm: LocalDateTime = LocalDateTime.now()
) {
    // Construtor secundário padrão sem argumentos (exigido pelo JPA/Hibernate)
    constructor() : this(null, "")
}
