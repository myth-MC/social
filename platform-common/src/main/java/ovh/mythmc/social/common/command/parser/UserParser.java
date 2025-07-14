package ovh.mythmc.social.common.command.parser;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import io.leangen.geantyref.TypeToken;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.command.exception.UnknownUserException;

public final class UserParser implements ArgumentParser<AbstractSocialUser, AbstractSocialUser>, BlockingSuggestionProvider.Strings<AbstractSocialUser>, ParserDescriptor<AbstractSocialUser, AbstractSocialUser> {

    private UserParser() { }

    public static UserParser userParser() {
        return new UserParser();
    }

    @Override
    public @NonNull ArgumentParser<AbstractSocialUser, AbstractSocialUser> parser() {
        return this;
    }

    @Override
    public @NonNull TypeToken<AbstractSocialUser> valueType() {
        return TypeToken.get(AbstractSocialUser.class);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(
            @NonNull CommandContext<AbstractSocialUser> commandContext, @NonNull CommandInput input) {

        return Social.get().getUserService().get().stream()
            .map(AbstractSocialUser::name)
            .toList();
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull AbstractSocialUser> parse(
            @NonNull CommandContext<@NonNull AbstractSocialUser> commandContext, @NonNull CommandInput commandInput) {

        final String input = commandInput.readString();
        final Optional<AbstractSocialUser> optionalUser = Social.get().getUserService().getByName(input);

        return optionalUser.map(ArgumentParseResult::success).orElseGet(() -> ArgumentParseResult.failure(new UnknownUserException(input, this, commandContext)));
    }

}
