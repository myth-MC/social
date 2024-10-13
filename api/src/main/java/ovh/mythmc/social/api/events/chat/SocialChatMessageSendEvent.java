package ovh.mythmc.social.api.events.chat;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;

@Getter
@Setter
public class SocialChatMessageSendEvent extends SocialChatMessagePrepareEvent implements Cancellable {

    private static final @NotNull HandlerList handlers = new HandlerList();

    public SocialChatMessageSendEvent(SocialPlayer sender, ChatChannel chatChannel, String message, Integer replyId) {
        super(sender, chatChannel, message, replyId);
    }

    public Component getParsedRawMessage() {
        return Social.get().getTextProcessor().parsePlayerInput(getSender(), getRawMessage());
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
