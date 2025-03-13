package ovh.mythmc.social.common.command.parser;

import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.command.exception.UnknownMessageException;

import java.util.ArrayList;
import java.util.List;

public final class RegisteredMessageParser implements ArgumentParser<AbstractSocialUser, SocialRegisteredMessageContext>, BlockingSuggestionProvider.Strings<AbstractSocialUser>, ParserDescriptor<AbstractSocialUser, SocialRegisteredMessageContext> {

    public static RegisteredMessageParser registeredMessageParser() {
        return new RegisteredMessageParser();
    }

    RegisteredMessageParser() {
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull SocialRegisteredMessageContext> parse(@NonNull CommandContext<@NonNull AbstractSocialUser> commandContext, @NonNull CommandInput commandInput) {
        final String input = commandInput.readInput();
        int id;

        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return ArgumentParseResult.failure(new UnknownMessageException(input, this, commandContext));
        }

        final SocialRegisteredMessageContext message = Social.get().getChatManager().getHistory().getById(id);

        if (message == null)
            return ArgumentParseResult.failure(new UnknownMessageException(input, this, commandContext));

        return ArgumentParseResult.success(message);
    }

    @Override
    public @NonNull ArgumentParser<AbstractSocialUser, SocialRegisteredMessageContext> parser() {
        return this;
    }

    @Override
    public @NonNull TypeToken<SocialRegisteredMessageContext> valueType() {
        return TypeToken.get(SocialRegisteredMessageContext.class);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<AbstractSocialUser> commandContext, @NonNull CommandInput input) {
        final List<String> suggestions = new ArrayList<>();
        for (int i = 0; i < Social.get().getChatManager().getHistory().get().size(); i++) {
            suggestions.add(String.valueOf(i));
        }

        return suggestions;
    }

}
