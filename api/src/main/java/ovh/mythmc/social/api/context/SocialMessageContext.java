package ovh.mythmc.social.api.context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.handlers.MessageHandlerOptions;
import ovh.mythmc.social.api.handlers.RegisteredMessageHandler;
import ovh.mythmc.social.api.users.SocialUser;

@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@RequiredArgsConstructor
@With
public class SocialMessageContext implements SocialContext {

    private final Date date;

    private final SocialUser sender;

    private final ChatChannel chatChannel;

    private final String rawMessage;

    private final Component component;

    private final Integer replyId;

    private final Collection<RegisteredMessageHandler<? extends MessageHandlerOptions>> handlers = new ArrayList<>();

    public SocialMessageContext(SocialUser sender, ChatChannel channel, String rawMessage, Component component, Integer replyId) {
        this.date = null;
        this.sender = sender;
        this.chatChannel = channel;
        this.rawMessage = rawMessage;
        this.component = component;
        this.replyId = replyId;
    }

    public @Nullable Integer id() {
        return Social.get().getChatManager().getHistory().getIdByContext(this);
    }

    public String date() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Social.get().getConfig().getSettings().getDateFormat());
        return dateFormat.format(date);
    }

    public boolean isReply() {
        if (Objects.equals(replyId, null))
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }


    
}
