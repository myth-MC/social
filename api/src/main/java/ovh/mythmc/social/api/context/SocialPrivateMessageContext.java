package ovh.mythmc.social.api.context;

import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import ovh.mythmc.social.api.user.SocialUser;

@Getter
@Accessors(fluent = true)
public class SocialPrivateMessageContext extends SocialMessageContext {
    
    private final Set<SocialUser> recipients;

    public SocialPrivateMessageContext(
        SocialUser sender,
        Set<SocialUser> recipients,
        Set<Audience> viewers,
        String rawMessage,
        SignedMessage signedMessage) {

        super(sender, sender.getMainChannel(), viewers, rawMessage, 0, signedMessage);

        this.recipients = recipients;
    }

}
