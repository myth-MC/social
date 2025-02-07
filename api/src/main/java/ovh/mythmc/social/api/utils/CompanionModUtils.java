package ovh.mythmc.social.api.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.users.SocialUser;

@UtilityClass
public class CompanionModUtils {
    
    public Component asChannelable(Component component, ChatChannel channel) {
        return Component.text("#" + channel.getName() + "#")
            .append(component);
    }

    public Component asBroadcast(Component component) {
        return Component.text("#broadcast#")
            .append(component);
    }

    public String getAliasWithPrefix(ChatChannel channel) {
        String alias = channel.getName();
        Component prefix = Component.text("#");
        
        if (channel instanceof GroupChatChannel groupChatChannel) {
            alias = groupChatChannel.getAliasOrName();
            prefix = Social.get().getTextProcessor().parse(new SocialUser.Dummy(channel), groupChatChannel, ":raw_shield:");
        }

        return PlainTextComponentSerializer.plainText().serialize(prefix) + alias;
    }

    public String getIconWithoutBrackets(ChatChannel channel) {
        Component icon = Social.get().getTextProcessor().parse(new SocialUser.Dummy(channel), channel, channel.getIcon());
        
        return PlainTextComponentSerializer.plainText().serialize(icon)
            .replace("[", "")
            .replace("]", "");
    }

}
