package com.seekernaut.seekernaut.client.ollama.feign.api.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ErrorDto {
        private List<String> mensagens;

        public List<String> getMensagens() {
            if (this.mensagens == null) {
                this.mensagens = new ArrayList<>();
            }

            return this.mensagens;
        }
    }