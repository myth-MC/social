package ovh.mythmc.social.api.chat.renderer;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.context.SocialRendererContext;

public interface SocialChatRenderer {

    @Nullable SocialRendererContext render(@NotNull SocialMessageContext context);

}
