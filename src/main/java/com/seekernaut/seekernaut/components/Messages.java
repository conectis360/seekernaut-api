package com.seekernaut.seekernaut.components;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Messages {

    private final MessageSource messageSource;

    private MessageSourceAccessor accessor;

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource, LocaleContextHolder.getLocale());
    }

    public String get(String code, String... args) {
        return accessor.getMessage(code, args);
    }

    public String get(String code) {
        return accessor.getMessage(code);
    }
}