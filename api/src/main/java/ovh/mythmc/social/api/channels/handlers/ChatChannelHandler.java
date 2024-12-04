package ovh.mythmc.social.api.channels.handlers;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ovh.mythmc.social.api.context.SocialMessageContext;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ChatChannelHandler implements ChannelHandler {

    @Override
    public void send(@NotNull SocialMessageContext context) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'send'");
    }
    
}
