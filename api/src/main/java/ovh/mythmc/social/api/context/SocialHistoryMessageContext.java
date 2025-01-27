package ovh.mythmc.social.api.context;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@Getter
@Accessors(fluent = true)
public class SocialHistoryMessageContext extends SocialMessageContext {

    private final Integer id;

    private final Component message;

    private final long timestamp;

    public SocialHistoryMessageContext(
        int id,
        long timestamp,
        SocialUser sender,
        ChatChannel channel,
        Component message,
        String rawMessage,
        Integer replyId,
        SignedMessage signedMessage) {

        super(sender, channel, rawMessage, replyId, signedMessage);

        this.timestamp = timestamp;
        this.id = id;
        this.message = message;
    }

    public String date() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Social.get().getConfig().getGeneral().getDateFormat());
        return dateFormat.format(new Date(timestamp));
    }

}
