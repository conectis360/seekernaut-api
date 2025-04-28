package com.seekernaut.seekernaut.exception.handler;

import com.seekernaut.seekernaut.components.Messages;
import com.seekernaut.seekernaut.exception.BusinessException;
import com.seekernaut.seekernaut.exception.CommunicationException;
import com.seekernaut.seekernaut.exception.EntityNotFoundException;
import com.seekernaut.seekernaut.exception.ValidationException;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
public class CommonsExceptionHandler extends ResponseEntityExceptionHandler {

    public static final int CODIGO_HTTP_BUSINESS_EXCEPTION = 460;
    public static final String MESSAGE_PROPERTIES_DESCRIPTION_INITIAL = "message.";

    @Autowired
    MessageSource messageSource;

    @Autowired
    private Messages messages;

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
        ex.printStackTrace();
        Erro erro = new Erro();
        erro.getMensagens().add(messages.get("erro.ocorreu-erro"));
        return this.handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({FeignException.class})
    public ResponseEntity<Object> handleFeignException(FeignException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(ex.getMessage());
        return this.handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(Optional.ofNullable(ex).map(EntityNotFoundException::getMessage).orElse(messages.get("recurso.nao-encontrado")));
        return handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        Erro erro = new Erro();
        List<String> messageList = Optional.ofNullable(ex.getMessages()).orElse(new ArrayList<>());
        for (String message : messageList) {
            message = Optional.ofNullable(message).orElse(ex.toString());
            if (message.contains(MESSAGE_PROPERTIES_DESCRIPTION_INITIAL)) {
                message = messages.get(message);
            }
            erro.getMensagens().add(message);
        }
        return ResponseEntity.status(CODIGO_HTTP_BUSINESS_EXCEPTION).body(erro);
    }

    @ExceptionHandler({CommunicationException.class})
    public ResponseEntity<Object> handleCommunicationException(CommunicationException ex, WebRequest request) {
        Erro erro = new Erro();
        List<String> messageList = Optional.ofNullable(ex.getMessages()).orElse(new ArrayList<>());
        for (String message : messageList) {
            message = Optional.ofNullable(message).orElse(ex.toString());
            if (message.contains(MESSAGE_PROPERTIES_DESCRIPTION_INITIAL)) {
                message = messages.get(message);
            }
            erro.getMensagens().add(message);
        }
        return ResponseEntity.status(CODIGO_HTTP_BUSINESS_EXCEPTION).body(erro);
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
        Erro erro = new Erro();
        List<String> messageList = Optional.ofNullable(ex.getMessages()).orElse(new ArrayList<>());
        for (String message : messageList) {
            message = Optional.ofNullable(message).orElse(ex.toString());
            if (message.contains(MESSAGE_PROPERTIES_DESCRIPTION_INITIAL)) {
                message = messages.get(message);
            }
            erro.getMensagens().add(message);
        }
        return ResponseEntity.status(CODIGO_HTTP_BUSINESS_EXCEPTION).body(erro);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleException(AccessDeniedException ex, WebRequest request) {
        Erro erro = new Erro();
        if (StringUtils.isEmpty(ex.getMessage())) {
            erro.getMensagens().add(messages.get("acesso.negado"));
        } else {
            erro.getMensagens().add(ex.getMessage());
        }
        return this.handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(messages.get("acesso.negado"));
        return this.handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(erro);
    }

    private Erro createListErrors(BindingResult bidingResult) {
        Erro erro = new Erro();
        Iterator<? extends FieldError> var3 = bidingResult.getFieldErrors().iterator();

        while (var3.hasNext()) {
            FieldError fieldEror = var3.next();
            String mensagem = this.messageSource.getMessage(fieldEror, LocaleContextHolder.getLocale());
            erro.getMensagens().add(mensagem);
        }

        return erro;
    }


    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<Object> handleException(AuthenticationException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(ex.getMessage());
        return this.handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<Object> handleException(HttpClientErrorException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(ex.getMessage());
        return this.handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({AccessException.class})
    public ResponseEntity<Object> handleException(AccessException ex, WebRequest request) {
        Erro erro = new Erro();
        erro.getMensagens().add(ex.getMessage());
        return this.handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    private Erro createListErrors(Set<ConstraintViolation<?>> constraintViolation) {
        Erro erro = new Erro();
        Iterator<? extends ConstraintViolation> var3 = constraintViolation.iterator();

        while (var3.hasNext()) {
            ConstraintViolation<?> constrainViolation = var3.next();
            String mensagem = constrainViolation.getPropertyPath().toString().split("\\.")[1] + " " + constrainViolation.getMessage();
            erro.getMensagens().add(mensagem);
        }

        return erro;
    }


    public class Erro {
        private List<String> mensagens;

        public Erro() {
        }

        public List<String> getMensagens() {
            if (this.mensagens == null) {
                this.mensagens = new ArrayList<>();
            }

            return this.mensagens;
        }

        public void setMensagens(List<String> mensagens) {
            this.mensagens = mensagens;
        }
    }
}
