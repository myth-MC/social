package ovh.mythmc.social.api.context;

import java.util.Objects;

import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.chat.SignedMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class SocialMessageContext implements SocialContext {

    private final SocialUser sender;

    private final ChatChannel chatChannel;

    private final String rawMessage;

    private final Integer replyId;

    private final @Nullable SignedMessage signedMessage;

    public boolean isReply() {
        if (Objects.equals(replyId, null))
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }
    
}
