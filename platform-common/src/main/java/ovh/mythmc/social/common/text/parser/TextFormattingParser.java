package ovh.mythmc.social.common.text.parser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.filter.SocialFilterLike;
import ovh.mythmc.social.api.text.injection.defaults.SocialInjectionTagParser;

import java.util.ArrayList;
import java.util.List;

public final class TextFormattingParser implements SocialFilterLike {

    @Override
    public Component parse(SocialParserContext context) {
        if (!context.user().checkPermission("social.text-formatting"))
            return context.message();

        final List<TagResolver> tagResolvers = new ArrayList<>();

        context.injectedValues().forEach(injectedValue -> {
            if (injectedValue.parser() instanceof SocialInjectionTagParser && injectedValue.value() instanceof TagResolver tagResolver) {
                tagResolvers.add(tagResolver);
            }
        });

        return miniMessage(
            context.message(),
            TagResolver.builder()
                .resolvers(tagResolvers)
                .build());
    }

    private static Component miniMessage(@NotNull Component component, @NotNull TagResolver tagResolver) {
        final MiniMessage miniMessage = MiniMessage.builder()
            .tags(TagResolver.builder()
                .resolvers(
                    StandardTags.color(),
                    StandardTags.decorations(),
                    tagResolver
                )
                .build())
            .build();

        final String serialized = miniMessage.serialize(component);
        return miniMessage.deserialize(serialized
            .replace("\\<", "<") // Possibly the worst way to achieve this (not even supported by MiniMessage's team, so plz don't do it)
        );
    }

}
