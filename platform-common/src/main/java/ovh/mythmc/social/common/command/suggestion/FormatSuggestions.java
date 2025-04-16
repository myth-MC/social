package ovh.mythmc.social.common.command.suggestion;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.text.parser.SocialContextualKeyword;
import ovh.mythmc.social.api.user.AbstractSocialUser;

import java.util.ArrayList;
import java.util.Collection;

public final class FormatSuggestions implements BlockingSuggestionProvider.Strings<AbstractSocialUser> {

    public static FormatSuggestions formatSuggestions() {
        return new FormatSuggestions();
    }

    FormatSuggestions() {
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<AbstractSocialUser> commandContext, @NonNull CommandInput input) {
        final Collection<String> suggestions = new ArrayList<>();

        Social.get().getTextProcessor().getContextualParsersByType(SocialContextualKeyword.class)
            .forEach(keyword -> suggestions.add("[" + keyword.keyword() + "]"));

        Social.registries().emojis().registry().entrySet().stream()
            .filter(entry -> !entry.getKey().namespace().equalsIgnoreCase("hidden"))
            .forEach(entry -> suggestions.add(":" + entry.getValue().name() + ":"));

        return suggestions;
    }
}
