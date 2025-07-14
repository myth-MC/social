package ovh.mythmc.social.common.callback.handler;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.common.callback.game.AnvilRenameCallback;

public final class AnvilRenameHandler implements SocialCallbackHandler {

    @Override
    public void register() {
        AnvilRenameCallback.INSTANCE.registerHandler("social:anvil-formatter", ctx -> {
            final var context = SocialParserContext.builder(ctx.user(), ctx.name()).build();
            final Component name = Social.get().getTextProcessor().parsePlayerInput(context);

            ctx.name(name);
        });
    }

    @Override
    public void unregister() {
        AnvilRenameCallback.INSTANCE.unregisterHandlers("social:anvil-formatter");
    }
    
}
