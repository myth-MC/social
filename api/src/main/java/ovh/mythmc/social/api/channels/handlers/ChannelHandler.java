package ovh.mythmc.social.api.channels.handlers;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.context.SocialMessageContext;

public interface ChannelHandler {

    void send(@NotNull SocialMessageContext context);
    
}
