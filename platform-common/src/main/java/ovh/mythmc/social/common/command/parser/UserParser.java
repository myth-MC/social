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
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.common.command.exception.SubjectIsSenderException;
import ovh.mythmc.social.common.command.exception.UnknownUserException;

public final class UserParser implements ArgumentParser<SocialUser, SocialUser>,
        BlockingSuggestionProvider.Strings<SocialUser>, ParserDescriptor<SocialUser, SocialUser> {

    private final boolean excludeSender;

    private UserParser(boolean excludeSender) {
        this.excludeSender = excludeSender;
    }

    public static UserParser userParser() {
        return new UserParser(false);
    }

    public static UserParser excludeSender() {
        return new UserParser(true);
    }

    @Override
    public @NonNull ArgumentParser<SocialUser, SocialUser> parser() {
        return this;
    }

    @Override
    public @NonNull TypeToken<SocialUser> valueType() {
        return TypeToken.get(SocialUser.class);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(
            @NonNull CommandContext<SocialUser> commandContext, @NonNull CommandInput input) {

        return Social.get().getUserService().get().stream()
                .map(SocialUser::username)
                .toList();
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull SocialUser> parse(
            @NonNull CommandContext<@NonNull SocialUser> commandContext, @NonNull CommandInput commandInput) {

        final String input = commandInput.readString();
        final Optional<SocialUser> optionalUser = Social.get().getUserService().get().stream()
                .filter(user -> user.username().equals(input))
                .findAny();

        final SocialUser sender = commandContext.sender();
        if (excludeSender && optionalUser.isPresent() && optionalUser.get().uuid().equals(sender.uuid()))
            return ArgumentParseResult.failure(new SubjectIsSenderException(this, commandContext));

        return optionalUser.map(ArgumentParseResult::success)
                .orElseGet(() -> ArgumentParseResult.failure(new UnknownUserException(input, this, commandContext)));
    }

}
