package com.seekernaut.seekernaut.client.ollama.webclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient ollamaWebClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMinutes(5)) // Timeout de resposta do Netty
                .tcpConfiguration(tcpClient ->
                        tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // Timeout de conexão (em milissegundos)
                                .doOnConnected(conn -> {
                                    conn.addHandlerLast(new ReadTimeoutHandler(300)); // Timeout de leitura (inatividade) em segundos (5 minutos = 300 segundos)
                                    conn.addHandlerLast(new WriteTimeoutHandler(300)); // Timeout de escrita (inatividade) em segundos (5 minutos = 300 segundos)
                                }));

        return builder.clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("http://localhost:11434/api")
                .build();
    }
}
