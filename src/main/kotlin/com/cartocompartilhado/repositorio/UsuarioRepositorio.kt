package com.cartocompartilhado.repositorio

import com.cartocompartilhado.modelo.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Camada de Acesso a Dados (Repository) para a entidade Usuario.
 * 
 * Requisitos acadêmicos atendidos:
 * - Padrão Spring Data JPA Repository com operações CRUD prontas.
 * - ID numérico do tipo Long correspondente ao BIGSERIAL do PostgreSQL.
 * - Consultas derivadas por convenção de nomenclatura (Derived Query Methods).
 */
@Repository
interface UsuarioRepositorio : JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo nome exato, ignorando diferenças entre maiúsculas e minúsculas.
     * @param nome Nome a ser buscado.
     * @return Optional contendo o usuário caso encontrado.
     */
    fun findByNomeIgnoreCase(nome: String): Optional<Usuario>

    /**
     * Busca um usuário pelo endereço de e-mail cadastrado.
     * @param email E-mail a ser localizado.
     * @return Optional contendo o usuário caso encontrado.
     */
    fun findByEmail(email: String): Optional<Usuario>
}
