package ovh.mythmc.social.common.command.parser;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import io.leangen.geantyref.TypeToken;
import org.incendo.cloud.util.StringUtils;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.SimpleChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.util.registry.RegistryKey;
import ovh.mythmc.social.common.command.exception.UnauthorizedChannelException;
import ovh.mythmc.social.common.command.exception.UnknownChannelException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChannelParser implements ArgumentParser<AbstractSocialUser, ChatChannel>, BlockingSuggestionProvider.Strings<AbstractSocialUser>, ParserDescriptor<AbstractSocialUser, ChatChannel> {

    private static final Pattern QUOTED_DOUBLE = Pattern.compile("\"(?<inner>(?:[^\"\\\\]|\\\\.)*)\"");
    private static final Pattern QUOTED_SINGLE = Pattern.compile("'(?<inner>(?:[^'\\\\]|\\\\.)*)'");

    public static ChannelParser channelParser() {
        return new ChannelParser();
    }

    @Override
    public @NonNull ArgumentParser<AbstractSocialUser, ChatChannel> parser() {
        return this;
    }

    @Override
    public @NonNull TypeToken<ChatChannel> valueType() {
        return TypeToken.get(ChatChannel.class);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(
            @NonNull CommandContext<AbstractSocialUser> commandContext, @NonNull CommandInput input) {

        return Social.registries().channels().values().stream()
            .filter(channel -> channel instanceof SimpleChatChannel && Social.get().getChatManager().hasPermission(commandContext.sender(), channel))
            .map(ChatChannel::name)
            .toList();
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull ChatChannel> parse(
            @NonNull CommandContext<@NonNull AbstractSocialUser> commandContext, @NonNull CommandInput commandInput) {

        final String input = parseQuoted(commandContext, commandInput).parsedValue().orElse(null);
        final ChatChannel channel = Social.registries().channels().value(RegistryKey.identified(input)).orElse(null);

        if (channel == null)
            return ArgumentParseResult.failure(new UnknownChannelException(input, this, commandContext));

        if (!(channel instanceof SimpleChatChannel))
            return ArgumentParseResult.failure(new UnauthorizedChannelException(input, this, commandContext));

        return ArgumentParseResult.success(channel);
    }

    private @NonNull ArgumentParseResult<String> parseQuoted(
        final @NonNull CommandContext<AbstractSocialUser> commandContext,
        final @NonNull CommandInput commandInput
    ) {
        final char peek = commandInput.peek();
        if (peek != '\'' && peek != '"') {
            return ArgumentParseResult.success(commandInput.readString());
        }

        final String string = commandInput.remainingInput();

        final Matcher doubleMatcher = QUOTED_DOUBLE.matcher(string);
        String doubleMatch = null;
        if (doubleMatcher.find()) {
            doubleMatch = doubleMatcher.group("inner");
        }
        final Matcher singleMatcher = QUOTED_SINGLE.matcher(string);
        String singleMatch = null;
        if (singleMatcher.find()) {
            singleMatch = singleMatcher.group("inner");
        }

        String inner = null;
        if (singleMatch != null && doubleMatch != null) {
            final int singleIndex = string.indexOf(singleMatch);
            final int doubleIndex = string.indexOf(doubleMatch);
            inner = doubleIndex < singleIndex ? doubleMatch : singleMatch;
        } else if (singleMatch == null && doubleMatch != null) {
            inner = doubleMatch;
        } else if (singleMatch != null) {
            inner = singleMatch;
        }

        if (inner != null) {
            final int numSpaces = StringUtils.countCharOccurrences(inner, ' ');
            for (int i = 0; i <= numSpaces; i++) {
                commandInput.readString();
            }
        } else {
            inner = commandInput.peekString();
            if (inner.startsWith("\"") || inner.startsWith("'")) {
                return ArgumentParseResult.failure(new StringParser.StringParseException(
                    commandInput.remainingInput(),
                    StringParser.StringMode.QUOTED, commandContext
                ));
            } else {
                commandInput.readString();
            }
        }

        inner = inner.replace("\\\"", "\"").replace("\\'", "'");

        return ArgumentParseResult.success(inner);
    }

}
