package ovh.mythmc.social.api.context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.players.SocialPlayer;

@Data
@Builder
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@With
public class SocialMessageContext implements SocialContext {

    private final Date date;

    private final SocialPlayer sender;

    private final ChatChannel chatChannel;

    private final String rawMessage;

    private final Integer replyId;

    public String date() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Social.get().getConfig().getSettings().getDateFormat());
        return dateFormat.format(date);
    }

    public boolean isReply() {
        if (Objects.equals(replyId, null))
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }

    public Integer id() {
        return Social.get().getChatManager().getHistory().getIdByContext(this);
    }
    
}
