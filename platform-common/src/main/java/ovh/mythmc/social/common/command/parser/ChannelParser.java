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
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.SimpleChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.util.registry.RegistryKey;
import ovh.mythmc.social.common.command.exception.UnauthorizedChannelException;
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

        return Social.registries().channels().values().stream()
            .filter(channel -> channel instanceof SimpleChatChannel)
            .map(ChatChannel::name)
            .toList();
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull ChatChannel> parse(
            @NonNull CommandContext<@NonNull AbstractSocialUser> commandContext, @NonNull CommandInput commandInput) {

        final String input = commandInput.readString();
        final ChatChannel channel = Social.registries().channels().value(RegistryKey.identified(input)).orElse(null);

        if (channel == null)
            return ArgumentParseResult.failure(new UnknownChannelException(input, this, commandContext));

        if (!(channel instanceof SimpleChatChannel))
            return ArgumentParseResult.failure(new UnauthorizedChannelException(input, this, commandContext));

        return ArgumentParseResult.success(channel);
    }
    
}
