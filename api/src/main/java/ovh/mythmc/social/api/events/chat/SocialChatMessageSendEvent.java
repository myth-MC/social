package ovh.mythmc.social.api.events.chat;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;

@Getter
@Setter
public class SocialChatMessageSendEvent extends SocialChatMessagePrepareEvent {

    private static final @NotNull HandlerList handlers = new HandlerList();

    public SocialChatMessageSendEvent(SocialPlayer sender, ChatChannel chatChannel, String message, Integer replyId) {
        super(sender, chatChannel, message, replyId);
    }

    // TODO: delete this!!
    // make ChatHistory store contexts instead of events?
    @Deprecated
    @ScheduledForRemoval
    public Component getParsedRawMessage() {
        return Component.text(getRawMessage());
        /*
        SocialParserContext context = SocialParserContext.builder()
            .socialPlayer(getSender())
            .message(Component.text(getRawMessage()))
            .build();

        return Social.get().getTextProcessor().parsePlayerInput(context); */
    }

    public Integer getId() {
        return Social.get().getChatManager().getHistory().getIdByEvent(this);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
