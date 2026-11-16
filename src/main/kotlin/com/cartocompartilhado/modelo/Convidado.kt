package com.cartocompartilhado.modelo

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

/**
 * Entidade filha que representa um Convidado (participante regular da fatura).
 * 
 * Requisito acadêmico atendido:
 * - Especialização (Herança de Usuario).
 * - O Convidado não necessita de senha complexa: ele acessa a fatura usando um código de acesso pessoal
 *   (por exemplo, "EOC-1812") para visualizar seus gastos individuais e a chave PIX do administrador.
 */
@Entity
@DiscriminatorValue("convidado")
class Convidado(
    id: Long? = null,
    nome: String = "",
    email: String? = null,
    telefone: String? = null,
    chavePixPadrao: String? = null,

    /**
     * Código de acesso rápido/pessoal gerado para o convidado entrar na aplicação.
     */
    @Column(name = "codigo_acesso_pessoal", length = 50)
    var codigoAcessoPessoal: String? = null
) : Usuario(id, nome, email, telefone, chavePixPadrao) {
    // Construtor sem argumentos para o JPA
    constructor() : this(null, "", null, null, null, null)
}
