package com.hula.ai.llm.locallm.ollama.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChatCompletionMessage implements Serializable {
    @NotNull
    private String role;
    private String content;

    public static Builder builder() {
        return new Builder();
    }

    private ChatCompletionMessage(Builder builder) {
        setRole(builder.role);
        setContent(builder.content);
    }

    @Getter
    @AllArgsConstructor
    public enum Role {
        USER("user"),
        ASSISTANT("assistant");
        private String name;
    }

    public static final class Builder {
        private @NotNull String role;
        private String content;

        public Builder() {
        }

        public Builder role(@NotNull String role) {
            this.role = role;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public ChatCompletionMessage build() {
            return new ChatCompletionMessage(this);
        }
    }

}
