package ovh.mythmc.social.common.command.parser;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import io.leangen.geantyref.TypeToken;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.text.parser.SocialIdentifiedParser;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.command.exception.UnknownIdentifiedParserException;

public final class IdentifiedParserParser<T extends SocialIdentifiedParser> implements ArgumentParser<AbstractSocialUser, T>, BlockingSuggestionProvider.Strings<AbstractSocialUser>, ParserDescriptor<AbstractSocialUser, T> {

    public static <T extends SocialIdentifiedParser> IdentifiedParserParser<T> of(Class<T> type) {
        return new IdentifiedParserParser<T>(type);
    }

    private final Class<T> type;

    private IdentifiedParserParser(Class<T> type) {
        this.type = type;
    }

    @Override
    public @NonNull ArgumentParser<AbstractSocialUser, T> parser() {
        return this;
    }

    @Override
    public @NonNull TypeToken<T> valueType() {
        return TypeToken.get(type);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(
            @NonNull CommandContext<AbstractSocialUser> commandContext, @NonNull CommandInput input) {

        return Social.get().getTextProcessor().getContextualParsersByType(type).stream()
            .map(SocialIdentifiedParser::identifier)
            .toList();
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull T> parse(
            @NonNull CommandContext<@NonNull AbstractSocialUser> commandContext, @NonNull CommandInput commandInput) {

        final String input = commandInput.readString();
        final var optionalParser = Social.get().getTextProcessor().getIdentifiedParser(type, input);

        return optionalParser.map(ArgumentParseResult::success).orElseGet(() -> ArgumentParseResult.failure(new UnknownIdentifiedParserException(input, this, commandContext)));
    }
    
}
