package com.xg7plugins.api.utils;

import com.xg7plugins.api.XG7PluginManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class Conversation {


    private final String question;
    private String prefix;
    private final ResponseType type;
    private BiConsumer<ConversationContext, ?> onFinish;
    private @Nullable Consumer<ConversationAbandonedEvent> onCancel;
    private @Nullable Consumer<Exception> onError;

    public static Conversation init(ResponseType type, String question) {
        return new Conversation(question, type);
    }

    public Conversation prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }
    public Conversation onFinish(BiConsumer<ConversationContext, ?> onFinish) {
        this.onFinish = onFinish;
        return this;
    }
    public Conversation onCancel(Consumer<ConversationAbandonedEvent> onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public Conversation onError(Consumer<Exception> onError) {
        this.onError = onError;
        return this;
    }


    public void ask(Player player) {
        if (onFinish == null) throw new RuntimeException("Conversations needs a finish action!");

        new ConversationFactory(XG7PluginManager.getPlugin())
                .withLocalEcho(false)
                .withTimeout(120)
                .withModality(true)
                .withEscapeSequence("cancel")
                .withPrefix(new ConversationPrefix() {
                    @NotNull
                    @Override
                    public String getPrefix(@NotNull ConversationContext conversationContext) {

                        if (prefix == null) return XG7PluginManager.getPluginPrefix() == null ? "" : XG7PluginManager.getPluginPrefix();
                        return prefix;
                    }
                })
                .withFirstPrompt(new Prompt() {
                    @NotNull
                    @Override
                    public String getPromptText(@NotNull ConversationContext conversationContext) {
                        return question;
                    }

                    @Override
                    public boolean blocksForInput(@NotNull ConversationContext conversationContext) {
                        return true;
                    }

                    @Nullable
                    @Override
                    public Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {

                        try {
                            onFinish.accept(conversationContext, type.parse(s));
                        } catch (Exception e) {
                            if (onError != null) onError.accept(e);
                        }


                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .addConversationAbandonedListener(
                        listener -> {
                            if (listener.gracefulExit()) return;
                            if (onCancel != null) onCancel.accept(listener);
                        }
                )
                .buildConversation(player)
                .begin();
    }


    public enum ResponseType {
        INT(Integer::parseInt),
        STRING(value -> value),
        BOOLEAN(Boolean::parseBoolean),
        DOUBLE(Double::parseDouble),
        FLOAT(Float::parseFloat),
        LONG(Long::parseLong),
        BYTE(Byte::parseByte);

        @FunctionalInterface
        interface Parser<T> {
            T parse(String value);
        }

        private final Parser<?> method;

        ResponseType(Parser<?> method) {
            this.method = method;
        }
        public <T> T parse(String value) {
            return (T) this.method.parse(value);
        }
    }

}
