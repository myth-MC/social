package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.context.SocialMessageContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ChatHistory {

    private final Map<Integer, SocialRegisteredMessageContext> messages = new HashMap<>();

    public SocialRegisteredMessageContext register(final @NotNull SocialMessageContext messageContext, final @NotNull Component finalMessage) {
        int id = messages.size();
        
        SocialRegisteredMessageContext historyContext = new SocialRegisteredMessageContext(
            id, 
            System.currentTimeMillis(),
            messageContext.sender(), 
            messageContext.channel(), 
            messageContext.viewers(),
            finalMessage, 
            messageContext.rawMessage(), 
            messageContext.replyId(),
            messageContext.signedMessage().orElse(null));

        messages.put(id, historyContext);
        return historyContext;
    }

    public SocialRegisteredMessageContext getById(final @NotNull Integer id) {
        return messages.get(id);
    }

    public boolean canDelete(SocialMessageContext context) {
        return context.signedMessage().isPresent() && context.signedMessage().get().canDelete();
    }

    public void delete(SocialRegisteredMessageContext context) {
        Social.get().getUserService().get().forEach(user -> user.deleteMessage(context.signedMessage().get()));

        SocialRegisteredMessageContext newContext = new SocialRegisteredMessageContext(
            context.id(), 
            context.timestamp(),
            context.sender(), 
            context.channel(), 
            context.viewers(),
            Component.text("N/A", NamedTextColor.RED), 
            context.rawMessage(), 
            context.replyId(), 
            context.signedMessage().orElse(null));

        messages.put(context.id(), newContext);
    }

    public List<SocialRegisteredMessageContext> get() {
        return messages.values().stream().toList();
    }

    public List<SocialRegisteredMessageContext> getByChannel(final @NotNull ChatChannel chatChannel) {
        return messages.values().stream()
                .filter(message -> message.channel().equals(chatChannel))
                .toList();
    }

    public List<SocialRegisteredMessageContext> getByUser(final @NotNull SocialUser user) {
        return messages.values().stream()
                .filter(message -> message.sender().uuid().equals(user.uuid()))
                .toList();
    }

    public List<SocialRegisteredMessageContext> getByText(final @NotNull String text) {
        return messages.values().stream()
                .filter(message -> message.rawMessage().contains(text))
                .toList();
    }

    public List<SocialRegisteredMessageContext> getThread(final @NotNull SocialRegisteredMessageContext messageContext, int limit) {
        return messages.values().stream()
                .filter(value ->
                        (value.isReply() && Objects.equals(value.replyId(), messageContext.id())) ||
                        (value.isReply() && Objects.equals(value.replyId(), messageContext.replyId())) ||
                        value.id().equals(messageContext.id()) ||
                        value.id().equals(messageContext.replyId())
                )
                .limit(limit)
                .toList();
    }

    @Deprecated(forRemoval = true, since = "v0.3")
    public boolean isThread(final @NotNull SocialRegisteredMessageContext messageContext) {
        return getThread(messageContext, 3).size() > 2;
    }

}
