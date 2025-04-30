package com.seekernaut.seekernaut.domain.ollama.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seekernaut.seekernaut.api.ollama.dto.ModelListDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateRequestDto;
import com.seekernaut.seekernaut.api.ollama.dto.OllamaGenerateResponseDto;
import com.seekernaut.seekernaut.client.ollama.feign.api.OllamaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>Serviço responsável pela comunicação com a API do Ollama.</p>
 * <p>Esta classe oferece métodos para listar modelos disponíveis e gerar completações de texto
 * utilizando a API do Ollama, lidando com respostas em stream.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaService {

    /**
     * Cliente Feign para interagir com a API do Ollama.
     * Utilizado para operações que não exigem tratamento de stream complexo.
     */
    private final OllamaClient ollamaClient;

    /**
     * Cliente Web reativo para interagir com a API do Ollama.
     * Utilizado para chamadas que podem retornar streams de dados, como a geração de completações.
     */
    private final WebClient ollamaWebClient;

    /**
     * ObjectMapper do Jackson para desserializar as respostas JSON da API do Ollama.
     */
    private final ObjectMapper objectMapper;

    /**
     * <p>Retorna a lista de modelos disponíveis no servidor Ollama.</p>
     * <p>Utiliza o cliente Feign para realizar a chamada síncrona à API.</p>
     *
     * @return {@link ModelListDto} contendo a lista de modelos.
     */
    public ModelListDto listModels() {
        return ollamaClient.listModels();
    }

    /**
     * Utilizado para armazenar temporariamente a parte de uma linha JSON incompleta
     * que pode ser recebida em um `DataBuffer` e completada no próximo.
     * Garante que tentaremos desserializar apenas linhas JSON completas.
     */
    private final AtomicReference<String> incompleteLine = new AtomicReference<>("");

    /**
     * <p>Gera uma completação de texto utilizando a API do Ollama, suportando respostas em stream.</p>
     * <p>Utiliza o {@link WebClient} para realizar uma requisição POST assíncrona ao endpoint `/generate`
     * da API do Ollama e processa a resposta como um fluxo de eventos JSON.</p>
     *
     * @param request {@link OllamaGenerateRequestDto} contendo os parâmetros da requisição de geração.
     * @return {@link Flux} de {@link OllamaGenerateResponseDto}, onde cada item representa um evento
     * da stream de resposta do Ollama.
     */
    public Flux<OllamaGenerateResponseDto> generateCompletion(OllamaGenerateRequestDto request) {
        return ollamaWebClient.post()
                .uri("/generate")
                .contentType(MediaType.APPLICATION_JSON) // Define o tipo de conteúdo da requisição como JSON
                .accept(MediaType.TEXT_EVENT_STREAM) // Define o tipo de mídia esperado na resposta como um fluxo de eventos de texto
                .body(Mono.just(request), OllamaGenerateRequestDto.class) // Define o corpo da requisição como o objeto DTO convertido para um Mono
                .retrieve() // Realiza a requisição HTTP e obtém a resposta como um objeto de resposta do WebClient
                .bodyToFlux(DataBuffer.class) // Extrai o corpo da resposta como um Flux de DataBuffer (pedaços de dados binários)
                .flatMap(dataBuffer -> {
                    // Lê os bytes do DataBuffer
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    // Converte os bytes para uma String utilizando a codificação UTF-8
                    String content = new String(bytes, StandardCharsets.UTF_8);
                    // Concatena o conteúdo recebido com qualquer parte incompleta da linha JSON da iteração anterior
                    String currentBuffer = incompleteLine.get() + content;
                    // Divide o buffer atual por quebras de linha ('\n'), pois cada evento JSON do Ollama é enviado em uma nova linha
                    String[] lines = currentBuffer.split("\\n");
                    // Limpa o buffer de linha incompleta para a próxima iteração
                    incompleteLine.set("");

                    // Verifica se a última linha não está vazia e não termina com '}', o que sugere que é um JSON incompleto
                    if (lines.length > 0 && !lines[lines.length - 1].trim().isEmpty() && !lines[lines.length - 1].trim().endsWith("}")) {
                        // Armazena a linha incompleta para ser completada no próximo DataBuffer
                        incompleteLine.set(lines[lines.length - 1]);
                        // Remove a linha incompleta do array de linhas a serem processadas nesta iteração
                        lines = Arrays.copyOf(lines, lines.length - 1);
                    }

                    // Cria um Flux a partir do array de linhas JSON completas
                    return Flux.fromArray(lines)
                            .filter(line -> !line.trim().isEmpty()) // Filtra linhas vazias
                            .map(line -> {
                                try {
                                    // Tenta desserializar a linha JSON para um objeto OllamaGenerateResponseDto
                                    return objectMapper.readValue(line, OllamaGenerateResponseDto.class);
                                } catch (IOException e) {
                                    // Em caso de erro na desserialização, loga o erro e retorna null
                                    System.err.println("Erro ao desserializar linha da stream: " + e.getMessage());
                                    return null;
                                }
                            })
                            .filter(dto -> dto != null); // Filtra os objetos DTO que foram desserializados com sucesso (não nulos)
                })
                .timeout(Duration.ofMinutes(5)); // Define um timeout para a stream completa de 5 minutos
    }
}