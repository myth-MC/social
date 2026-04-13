package ovh.mythmc.social.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.chat.channel.PrivateChatChannel;
import ovh.mythmc.social.api.user.ConsoleSocialUser;

/**
 * Utility class for companion mod interactions.
 */
public final class CompanionModUtils {

    private CompanionModUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static @NotNull TextComponent asChannelable(Component component, ChatChannel channel) {
        return Component.text("#" + channel.name() + "#")
                .append(component);
    }

    public static Component asBroadcast(Component component) {
        return Component.text("#broadcast#")
                .append(component);
    }

    public static String getAliasWithPrefix(ChatChannel channel) {
        String alias = channel.aliasOrName();
        Component prefix = Component.text("#");

        if (channel instanceof GroupChatChannel groupChatChannel) {
            prefix = Social.get().getTextProcessor().parse(ConsoleSocialUser.get(groupChatChannel), groupChatChannel,
                    ":raw_shield:");
        }

        if (channel instanceof PrivateChatChannel privateChatChannel) {
            prefix = Social.get().getTextProcessor().parse(ConsoleSocialUser.get(privateChatChannel), privateChatChannel, ":mail:");
        }

        return PlainTextComponentSerializer.plainText().serialize(prefix) + alias;
    }

    public static String getIconWithoutBrackets(ChatChannel channel) {
        Component icon = Social.get().getTextProcessor().parse(ConsoleSocialUser.get(channel), channel, channel.icon());

        return PlainTextComponentSerializer.plainText().serialize(icon)
                .replace("[", "")
                .replace("]", "");
    }

}

