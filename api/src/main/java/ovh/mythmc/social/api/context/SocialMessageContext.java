package ovh.mythmc.social.api.context;

import java.util.Set;

import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.user.SocialUser;

@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class SocialMessageContext implements SocialContext {

    private final SocialUser sender;

    private final ChatChannel channel;

    private final Set<Audience> viewers;

    private final String rawMessage;

    private final Integer replyId;

    private final @Nullable SignedMessage signedMessage;

    public boolean isSigned() {
        return signedMessage != null;
    }

    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }
    
}
