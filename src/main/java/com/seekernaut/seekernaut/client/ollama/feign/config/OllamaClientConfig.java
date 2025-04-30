package com.seekernaut.seekernaut.client.ollama.feign.config;

import com.seekernaut.seekernaut.client.ollama.feign.api.dto.ErrorDto;
import com.seekernaut.seekernaut.components.Messages;
import com.seekernaut.seekernaut.exception.CommunicationException;
import com.seekernaut.seekernaut.utils.ConverterUtils;
import feign.*;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class OllamaClientConfig {

    @Value("BASIC")
    private String loggerLevel;

    private final Messages messages;

    @Bean
    public Feign.Builder builderOllamaClient() {
        return Feign.builder()
                .errorDecoder(this::errorDecoder)
                .logLevel(Logger.Level.valueOf(loggerLevel))
                .retryer(Retryer.NEVER_RETRY);
    }

    @Bean
    public RequestInterceptor requestInterceptorOllamaClient() {
        return requestTemplate -> {
            final String AUTHORIZATION_HEADER = "Authorization";
            final String AUTHORIZATION_PREFIX = "Bearer";
        };
    }

    private Exception errorDecoder(String methodKey, Response response) {
        List<String> erros = new ArrayList<>();
        try {
            String body = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
            ErrorDto error = ConverterUtils.jsonToObject(body, ErrorDto.class);
            erros.addAll(error.getMensagens());
        } catch (IOException e) {
            log.error("read conflict response body exception. {}", e.toString());
            erros.add(messages.get("client.erro-comunicacao-sistema-x", "ollama-api"));
        }

        return new CommunicationException(erros);
    }
}
