package com.cartocompartilhado.modelo

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

/**
 * Entidade filha que representa um Administrador no sistema.
 * 
 * Requisito acadêmico atendido:
 * - Especialização (Herança de Usuario).
 * - O Administrador possui privilégios de criar faturas, definir categorias,
 *   gerenciar participantes e possui uma senha de acesso.
 * - Anotação @DiscriminatorValue("administrador") grava o valor 'administrador' na coluna 'tipo_usuario'.
 */
@Entity
@DiscriminatorValue("administrador")
class Administrador(
    id: Long? = null,
    nome: String = "",
    email: String? = null,
    telefone: String? = null,
    chavePixPadrao: String? = null,

    /**
     * Senha (ou hash de senha) que permite ao administrador gerenciar a fatura e seus membros.
     */
    @Column(name = "senha_hash", length = 255)
    var senhaHash: String = ""
) : Usuario(id, nome, email, telefone, chavePixPadrao) {
    // Construtor sem argumentos para o JPA
    constructor() : this(null, "", null, null, null, "")
}
