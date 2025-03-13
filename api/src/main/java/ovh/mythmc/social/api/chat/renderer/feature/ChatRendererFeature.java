package ovh.mythmc.social.api.chat.renderer.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.renderer.SocialChatRendererUtil;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.text.injection.value.SocialInjectedValue;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.util.CompanionModUtils;

import java.util.function.Predicate;

public class ChatRendererFeature {

    public static ChatFeatureBuilder builder(@NotNull Handler handler) {
        return new ChatFeatureBuilder(handler);
    }

    public static ChatRendererFeature replies(int injectionIndex) {
        return ChatRendererFeature.builder((target, format, message, parser) -> {
            if (message.isReply())
                format.injectValue(SocialInjectedValue.literal(SocialChatRendererUtil.getReplyIcon(target, message)), injectionIndex);
        })
        .decorator((context, component) -> {
            final int idToReply = context.isReply() ? context.replyId() : context.id();
            return component.applyFallbackStyle(Style.style(ClickEvent.suggestCommand("(re:#" + idToReply + ") ")));
        })
        .build();
    }

    public static ChatRendererFeature companion() {
        return ChatRendererFeature.builder((target, format, message, parser) -> {
            if (target.companion().isPresent())
                format.append(CompanionModUtils.asChannelable(Component.empty(), message.channel()), 0);
        })
        .build();
    }

    private final Handler handler;

    private final Decorator decorator;

    private final Predicate<SocialRegisteredMessageContext> condition;

    private ChatRendererFeature(@NotNull Handler handler, @NotNull Decorator decorator, @NotNull Predicate<SocialRegisteredMessageContext> condition) {
        this.handler = handler;
        this.decorator = decorator;
        this.condition = condition;
    }

    public Handler handler() {
        return this.handler;
    }

    public Decorator decorator() {
        return this.decorator;
    }

    public boolean isApplicable(@NotNull SocialRegisteredMessageContext context) {
        return condition.test(context);
    }

    public abstract static class Builder<T extends Builder<T, R>, R extends ChatRendererFeature> {

        protected final Handler handler;

        protected Predicate<SocialRegisteredMessageContext> condition;

        protected Decorator decorator;

        protected Builder(@NotNull Handler handler) {
            this.handler = handler;
            this.condition = context -> true;
            this.decorator = (context, component) -> component;
        }

        protected abstract T get();

        public T condition(@NotNull Predicate<SocialRegisteredMessageContext> condition) {
            get().condition = condition;
            return get();
        }

        public T decorator(@NotNull Decorator decorator) {
            get().decorator = decorator;
            return get();
        }

        public abstract R build();

    }

    public static final class ChatFeatureBuilder extends Builder<ChatFeatureBuilder, ChatRendererFeature> {

        private ChatFeatureBuilder(@NotNull Handler handler) {
            super(handler);
        }

        @Override
        protected ChatFeatureBuilder get() {
            return this;
        }

        @Override
        public ChatRendererFeature build() {
            return new ChatRendererFeature(handler, decorator, condition);
        }

    }

    @FunctionalInterface
    public interface Handler {

        void handle(@NotNull AbstractSocialUser target, @NotNull ChatChannel.FormatBuilder format, @NotNull SocialRegisteredMessageContext message, @NotNull SocialParserContext parser);

    }

    @FunctionalInterface
    public interface Decorator {

        @NotNull Component decorate(@NotNull SocialRegisteredMessageContext context, @NotNull Component component);

    }

}
