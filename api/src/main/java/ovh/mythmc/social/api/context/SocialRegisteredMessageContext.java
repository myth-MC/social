package ovh.mythmc.social.api.context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Getter
@Accessors(fluent = true)
public class SocialRegisteredMessageContext extends SocialMessageContext {

    private final Integer id;

    private final Component message;

    private final long timestamp;

    public SocialRegisteredMessageContext(
        int id,
        long timestamp,
        AbstractSocialUser sender,
        ChatChannel channel,
        Set<Audience> viewers,
        Component message,
        String rawMessage,
        Integer replyId,
        SignedMessage signedMessage) {

        super(sender, channel, viewers, rawMessage, replyId, signedMessage);

        this.timestamp = timestamp;
        this.id = id;
        this.message = message;
    }

    public String date() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Social.get().getConfig().getGeneral().getDateFormat());
        return dateFormat.format(new Date(timestamp));
    }

}
