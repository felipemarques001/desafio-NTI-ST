package com.felipemarques.desafioNTIST.configs;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                description = "Documentação do desafio To-do-list feito pelo NTI-ST",
                title = "To-do-list",
                version = "1.0.0"
        ),
        servers = {
                @Server(description = "Servidor para desenvolvimento", url = "http://localhost:9090")
        }
)
public class SwaggerConfig { }
