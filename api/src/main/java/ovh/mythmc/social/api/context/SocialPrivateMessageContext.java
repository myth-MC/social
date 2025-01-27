package ovh.mythmc.social.api.context;

import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.chat.SignedMessage;
import ovh.mythmc.social.api.users.SocialUser;

@Getter
@Accessors(fluent = true)
public class SocialPrivateMessageContext extends SocialMessageContext {
    
    private final Set<SocialUser> recipients;

    public SocialPrivateMessageContext(
        SocialUser sender,
        Set<SocialUser> recipients,
        String rawMessage,
        SignedMessage signedMessage) {

        super(sender, sender.getMainChannel(), rawMessage, null, signedMessage);

        this.recipients = recipients;
    }

}
