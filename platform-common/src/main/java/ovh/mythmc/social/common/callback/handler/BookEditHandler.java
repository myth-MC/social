package ovh.mythmc.social.common.callback.handler;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.common.callback.game.BookEditCallback;

public final class BookEditHandler implements SocialCallbackHandler {

    @Override
    public void register() {
        BookEditCallback.INSTANCE.registerHandler("social:book-formatter", ctx -> {
            final List<Component> newPages = new ArrayList<>();

            ctx.pages().forEach(unparsedPage -> {
                final var context = SocialParserContext.builder(ctx.user(), unparsedPage).build();
                final var page = Social.get().getTextProcessor().parsePlayerInput(context);

                newPages.add(page);
            });

            ctx.pages(newPages);
        });
    }

    @Override
    public void unregister() {
        BookEditCallback.INSTANCE.unregisterHandlers("social:book-formatter");
    }
    
}
