package ovh.mythmc.social.api.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.chat.PrivateChatChannel;
import ovh.mythmc.social.api.user.AbstractSocialUser;

@UtilityClass
public class CompanionModUtils {
    
    public @NotNull TextComponent asChannelable(Component component, ChatChannel channel) {
        return Component.text("#" + channel.name() + "#")
            .append(component);
    }

    public Component asBroadcast(Component component) {
        return Component.text("#broadcast#")
            .append(component);
    }

    public String getAliasWithPrefix(ChatChannel channel) {
        String alias = channel.aliasOrName();
        Component prefix = Component.text("#");
        
        if (channel instanceof GroupChatChannel groupChatChannel) {
            prefix = Social.get().getTextProcessor().parse(AbstractSocialUser.dummy(channel), groupChatChannel, ":raw_shield:");
        }

        if (channel instanceof PrivateChatChannel privateChatChannel) {
            prefix = Social.get().getTextProcessor().parse(AbstractSocialUser.dummy(channel), privateChatChannel, ":mail:");
        }

        return PlainTextComponentSerializer.plainText().serialize(prefix) + alias;
    }

    public String getIconWithoutBrackets(ChatChannel channel) {
        Component icon = Social.get().getTextProcessor().parse(AbstractSocialUser.dummy(channel), channel, channel.icon());
        
        return PlainTextComponentSerializer.plainText().serialize(icon)
            .replace("[", "")
            .replace("]", "");
    }

}
