package com.apirestlibrary.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.apirestlibrary.libraryapi.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(appInfo());
    }

    private ApiInfo appInfo(){
        return new ApiInfoBuilder()
                .title("Library API")
                .description("Projeto para controle de estoque e emprestimos de livros")
                .version("1.0.0")
                .contact(contact())
                .build();
    }

    private Contact contact(){
        return new Contact(
                "Gabriel Nogueira da Silva",
                "http://github.com/baco999",
                "gabriel.nogueira21@hotmail.com"
        );
    }
}
