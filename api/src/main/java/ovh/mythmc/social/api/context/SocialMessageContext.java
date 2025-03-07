package ovh.mythmc.social.api.context;

import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class SocialMessageContext implements SocialContext {

    private final AbstractSocialUser sender;

    private final ChatChannel channel;

    private final Set<Audience> viewers;

    private final String rawMessage;

    private final Integer replyId;

    private final @Nullable SignedMessage signedMessage;

    @Deprecated(forRemoval = true)
    public boolean isSigned() {
        return signedMessage != null;
    }

    public Optional<SignedMessage> signedMessage() {
        return Optional.ofNullable(signedMessage);
    }

    public boolean isReply() {
        if (replyId == null)
            return false;

        return Social.get().getChatManager().getHistory().getById(replyId) != null;
    }
    
}
