package ovh.mythmc.social.api.context;

import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@RequiredArgsConstructor
public class SocialRendererContext implements SocialContext {

    private final AbstractSocialUser<? extends Object> sender;

    private final ChatChannel channel;

    private final Set<Audience> viewers;

    private final Component prefix;

    private final String plainMessage;

    private final Component message;

    private final Integer replyId;

    private final int messageId;
    
}
