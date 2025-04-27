package com.seekernaut.seekernaut.exception;

import com.seekernaut.seekernaut.components.Messages;
import com.seekernaut.seekernaut.exception.exceptions.BusinessException;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * {@code @ControllerAdvice} que centraliza o tratamento de exceções em toda a aplicação.
 * Estende {@link ResponseEntityExceptionHandler} para fornecer tratamento padrão para exceções do Spring MVC.
 * Utiliza {@link MessageSource} para internacionalização de mensagens de erro e um componente {@link Messages} customizado.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class CommonsExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Código HTTP customizado para exceções de regra de negócio.
     */
    public static final int CODIGO_HTTP_BUSINESS_EXCEPTION = 460;

    /**
     * Bean injetado para acessar mensagens de erro internacionalizadas.
     */
    private final MessageSource messageSource;

    /**
     * Bean injetado para acessar mensagens customizadas da aplicação.
     */
    private final Messages messages;

    /**
     * Trata exceções genéricas não tratadas especificamente.
     * Retorna um {@link ResponseEntity} com status {@link HttpStatus#INTERNAL_SERVER_ERROR} e uma mensagem genérica de erro.
     *
     * @param ex      A exceção {@link RuntimeException} ocorrida.
     * @param request O {@link WebRequest} da requisição.
     * @return Um {@link ResponseEntity} contendo o objeto de erro e o status HTTP.
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleGenericException(RuntimeException ex, WebRequest request) {
        ex.printStackTrace(); // Log da exceção para diagnóstico
        Erro erro = new Erro();
        erro.getMensagens().add(messages.get("erro.ocorreu-erro"));
        return handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Trata {@link EmptyResultDataAccessException}, que ocorre quando uma consulta ao banco de dados não retorna resultados esperados.
     * Retorna um {@link ResponseEntity} com status {@link HttpStatus#NOT_FOUND} e uma mensagem indicando que o recurso não foi encontrado.
     *
     * @param ex      A exceção {@link EmptyResultDataAccessException} ocorrida.
     * @param request O {@link WebRequest} da requisição.
     * @return Um {@link ResponseEntity} contendo o objeto de erro e o status HTTP.
     */
    @ExceptionHandler({EmptyResultDataAccessException.class})
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(messages.get("recurso.nao-encontrado"));
        return handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Trata {@link BusinessException}, exceções customizadas para regras de negócio da aplicação.
     * Retorna um {@link ResponseEntity} com um status HTTP customizado ({@link #CODIGO_HTTP_BUSINESS_EXCEPTION}) e a mensagem da exceção.
     *
     * @param ex      A exceção {@link BusinessException} ocorrida.
     * @param request O {@link WebRequest} da requisição.
     * @return Um {@link ResponseEntity} contendo o objeto de erro e o status HTTP customizado.
     */
    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(ex.getMessage());
        return ResponseEntity.status(CODIGO_HTTP_BUSINESS_EXCEPTION).body(erro);
    }

    /**
     * Trata {@link JDBCConnectionException}, que ocorre quando há um problema de conexão com o banco de dados.
     * Retorna um {@link ResponseEntity} com status {@link HttpStatus#INTERNAL_SERVER_ERROR} e uma mensagem indicando a falha na conexão.
     *
     * @param ex      A exceção {@link JDBCConnectionException} ocorrida.
     * @param request O {@link WebRequest} da requisição.
     * @return Um {@link ResponseEntity} contendo o objeto de erro e o status HTTP.
     */
    @ExceptionHandler({JDBCConnectionException.class})
    public ResponseEntity<Object> handleJDBCConnectionException(JDBCConnectionException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(messages.get("database.sem-conexao"));
        return handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Trata {@link AccessDeniedException}, que ocorre quando um usuário não tem permissão para acessar um recurso.
     * Retorna um {@link ResponseEntity} com status {@link HttpStatus#UNAUTHORIZED} e uma mensagem de acesso negado.
     *
     * @param ex      A exceção {@link AccessDeniedException} ocorrida.
     * @param request O {@link WebRequest} da requisição.
     * @return Um {@link ResponseEntity} contendo o objeto de erro e o status HTTP.
     */
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(messages.get("acesso.negado"));
        return handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * Trata {@link ResponseStatusException}, que permite retornar respostas com status HTTP específicos definidos na exceção.
     * Retorna um {@link ResponseEntity} com o status HTTP e a razão definidos na exceção.
     *
     * @param ex      A exceção {@link ResponseStatusException} ocorrida.
     * @param request O {@link WebRequest} da requisição.
     * @return Um {@link ResponseEntity} contendo o objeto de erro e o status HTTP da exceção.
     */
    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(erro);
    }

    /**
     * Cria uma lista de mensagens de erro a partir do {@link BindingResult} da validação.
     * Utiliza o {@link MessageSource} para obter mensagens internacionalizadas para cada erro de campo.
     *
     * @param bindingResult O resultado da validação dos argumentos do método.
     * @return Um objeto {@link Erro} contendo a lista de mensagens de erro.
     */
    private Erro createListErrors(BindingResult bindingResult) {
        Erro erro = new Erro();
        Iterator<FieldError> fieldErrorIterator = bindingResult.getFieldErrors().iterator();
        while (fieldErrorIterator.hasNext()) {
            FieldError fieldError = fieldErrorIterator.next();
            String mensagem = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
            erro.getMensagens().add(mensagem);
        }
        return erro;
    }

    /**
     * Cria uma lista de mensagens de erro a partir do {@link Set} de {@link ConstraintViolation}.
     * Extrai o nome do campo e a mensagem da constraint para formatar a mensagem de erro.
     *
     * @param constraintViolations Um conjunto de violações de constraint.
     * @return Um objeto {@link Erro} contendo a lista de mensagens de erro de constraint.
     */
    private Erro createListErrors(Set<ConstraintViolation<?>> constraintViolations) {
        Erro erro = new Erro();
        Iterator<? extends ConstraintViolation<?>> constraintViolationIterator = constraintViolations.iterator();
        while (constraintViolationIterator.hasNext()) {
            ConstraintViolation<?> constraintViolation = constraintViolationIterator.next();
            // Assume que a propriedade violada está no formato "entidade.campo"
            String[] pathParts = constraintViolation.getPropertyPath().toString().split("\\.");
            String fieldName = pathParts.length > 1 ? pathParts[1] : constraintViolation.getPropertyPath().toString();
            String mensagem = fieldName + " " + constraintViolation.getMessage();
            erro.getMensagens().add(mensagem);
        }
        return erro;
    }

    /**
     * Classe interna estática para representar a estrutura do objeto de erro retornado na resposta.
     * Contém uma lista de mensagens de erro.
     */
    public static class Erro {
        private List<String> mensagens;

        /**
         * Construtor padrão da classe {@code Erro}.
         */
        public Erro() {
        }

        /**
         * Retorna a lista de mensagens de erro. Se a lista for nula, uma nova lista vazia é criada.
         *
         * @return A lista de mensagens de erro.
         */
        public List<String> getMensagens() {
            if (this.mensagens == null) {
                this.mensagens = new ArrayList<>();
            }
            return this.mensagens;
        }

        /**
         * Define a lista de mensagens de erro.
         *
         * @param mensagens A lista de mensagens de erro a ser definida.
         */
        public void setMensagens(List<String> mensagens) {
            this.mensagens = mensagens;
        }
    }
}