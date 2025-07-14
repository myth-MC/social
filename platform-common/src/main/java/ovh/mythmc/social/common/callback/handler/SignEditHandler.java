package ovh.mythmc.social.common.callback.handler;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.common.callback.game.SignEditCallback;

public final class SignEditHandler implements SocialCallbackHandler {

    @Override
    public void register() {
        SignEditCallback.INSTANCE.registerHandler("social:sign-formatter", ctx -> {
            final List<Component> newLines = new ArrayList<>();

            ctx.lines().forEach(unparsedLine -> {
                final var context = SocialParserContext.builder(ctx.user(), unparsedLine).build();
                final var line = Social.get().getTextProcessor().parsePlayerInput(context);

                newLines.add(line);
            });

            ctx.lines(newLines);
        });
    }

    @Override
    public void unregister() {
        SignEditCallback.INSTANCE.unregisterHandlers("social:sign-formatter");
    }
    
}
