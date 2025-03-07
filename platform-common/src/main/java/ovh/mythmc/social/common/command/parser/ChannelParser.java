package ovh.mythmc.social.common.command.parser;

import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import io.leangen.geantyref.TypeToken;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.common.command.exception.UnknownChannelException;

public final class ChannelParser implements ArgumentParser<AbstractSocialUser, ChatChannel>, BlockingSuggestionProvider.Strings<AbstractSocialUser>, ParserDescriptor<AbstractSocialUser, ChatChannel> {

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

        return Social.get().getChatManager().getChannels().stream()
            .filter(channel -> !(channel instanceof GroupChatChannel))
            .map(ChatChannel::getName)
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull ChatChannel> parse(
            @NonNull CommandContext<@NonNull AbstractSocialUser> commandContext, @NonNull CommandInput commandInput) {

        final String input = commandInput.readString();
        final ChatChannel channel = Social.get().getChatManager().getChannel(input);

        if (channel == null)
            return ArgumentParseResult.failure(new UnknownChannelException(input, this, commandContext));

        return ArgumentParseResult.success(channel);
    }
    
}
