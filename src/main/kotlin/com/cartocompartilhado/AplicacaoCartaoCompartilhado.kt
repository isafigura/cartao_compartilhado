package com.cartocompartilhado

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Ponto de entrada (Entrypoint) da aplicação Spring Boot.
 * 
 * A anotação @SpringBootApplication ativa:
 * - @Configuration: define a classe como fonte de definições de beans.
 * - @EnableAutoConfiguration: configura automaticamente o Spring conforme as bibliotecas no pom.xml.
 * - @ComponentScan: varre recursivamente o pacote 'com.cartocompartilhado' em busca de componentes,
 *   controladoras (@RestController), serviços (@Service) e repositórios (@Repository).
 */
@SpringBootApplication
class AplicacaoCartaoCompartilhado

/**
 * Função principal (main) em Kotlin que inicializa o servidor web embutido (Tomcat na porta 8080).
 */
fun main(args: Array<String>) {
    runApplication<AplicacaoCartaoCompartilhado>(*args)
}
