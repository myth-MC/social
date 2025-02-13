package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.users.SocialUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ChatHistory {

    private final Map<Integer, SocialRegisteredMessageContext> messages = new HashMap<>();

    public SocialRegisteredMessageContext register(final @NotNull SocialMessageContext messageContext, final @NotNull Component finalMessage) {
        int id = messages.size();
        
        SocialRegisteredMessageContext historyContext = new SocialRegisteredMessageContext(
            id, 
            System.currentTimeMillis(),
            messageContext.sender(), 
            messageContext.chatChannel(), 
            messageContext.viewers(),
            finalMessage, 
            messageContext.rawMessage(), 
            messageContext.replyId(),
            messageContext.signedMessage());

        messages.put(id, historyContext);
        return historyContext;
    }

    public SocialRegisteredMessageContext getById(final @NotNull Integer id) {
        return messages.get(id);
    }

    public boolean canDelete(SocialMessageContext context) {
        return context.isSigned() ? context.signedMessage().canDelete() : false;
    }

    public void delete(SocialRegisteredMessageContext context) {
        Social.get().getUserManager().get().forEach(user -> user.deleteMessage(context.signedMessage()));

        SocialRegisteredMessageContext newContext = new SocialRegisteredMessageContext(
            context.id(), 
            context.timestamp(),
            context.sender(), 
            context.chatChannel(), 
            context.viewers(),
            Component.text("N/A", NamedTextColor.RED), 
            context.rawMessage(), 
            context.replyId(), 
            context.signedMessage());

        messages.put(context.id(), newContext);
    }

    public List<SocialRegisteredMessageContext> get() {
        return messages.values().stream().toList();
    }

    public List<SocialRegisteredMessageContext> getByChannel(final @NotNull ChatChannel chatChannel) {
        return messages.values().stream()
                .filter(message -> message.chatChannel().equals(chatChannel))
                .toList();
    }

    public List<SocialRegisteredMessageContext> getByUser(final @NotNull SocialUser user) {
        return messages.values().stream()
                .filter(message -> message.sender().getUuid().equals(user.getUuid()))
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
                        (value.isReply() && value.replyId() == messageContext.id()) ||
                        (value.isReply() && value.replyId() == messageContext.replyId()) ||
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
