package ovh.mythmc.social.common.command.parser;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.aggregate.AggregateParser;
import org.incendo.cloud.parser.aggregate.AggregateResultMapper;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;

import io.leangen.geantyref.TypeToken;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.api.util.registry.RegistryKey;
import ovh.mythmc.social.common.command.exception.UnknownReactionException;

public final class ReactionParser implements AggregateParser<SocialUser, Reaction> {

    public static ReactionParser reactionParser() {
        return new ReactionParser();
    }

    ReactionParser() {
    }

    @Override
    public @NonNull TypeToken<Reaction> valueType() {
        return TypeToken.get(Reaction.class);
    }

    @Override
    public @NonNull List<@NonNull CommandComponent<SocialUser>> components() {
        final List<@NonNull CommandComponent<SocialUser>> components = new ArrayList<>();

        components.add(CommandComponent.<SocialUser, String>builder()
                .name("category")
                .parser(StringParser.stringParser())
                .suggestionProvider(SuggestionProvider
                        .blockingStrings((ctx, input) -> Social.registries().reactions().namespaceComponents().stream()
                                .filter(namespace -> !namespace.equalsIgnoreCase("hidden"))
                                .toList()))
                .build());

        components.add(CommandComponent.<SocialUser, String>builder()
                .name("identifier")
                .parser(StringParser.stringParser())
                .suggestionProvider(SuggestionProvider.blockingStrings((ctx, input) -> {
                    final String category = ctx.get("category");
                    return Social.registries().reactions().valuesByNamespaceComponent(category).stream()
                            .map(Reaction::name)
                            .toList();
                }))
                .build());

        return components;
    }

    @Override
    public @NonNull AggregateResultMapper<SocialUser, Reaction> mapper() {
        return (commandContext, aggregateCommandContext) -> {
            final String category = aggregateCommandContext.get("category");
            final String identifier = aggregateCommandContext.get("identifier");

            final Reaction reaction = Social.registries().reactions()
                    .value(RegistryKey.namespaced(category, identifier)).orElse(null);
            if (reaction == null)
                return ArgumentParseResult
                        .failureFuture(new UnknownReactionException(category, identifier, this, commandContext));

            return ArgumentParseResult.successFuture(reaction);
        };
    }

}
