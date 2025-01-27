package ovh.mythmc.social.api.chat.renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.context.SocialMessageContext;
import ovh.mythmc.social.api.events.chat.SocialChatMessagePrepareEvent;
import ovh.mythmc.social.api.users.SocialUser;

@UtilityClass
public class SocialChatRendererUtil {
    
    public SocialChatMessagePrepareEvent messagePrepareEvent(SocialUser user, ChatChannel channel, String rawMessage, Integer replyId) {
        SocialChatMessagePrepareEvent socialChatMessagePrepareEvent = new SocialChatMessagePrepareEvent(user, channel, rawMessage, replyId);
        Bukkit.getPluginManager().callEvent(socialChatMessagePrepareEvent);

        return socialChatMessagePrepareEvent;
    }

    public Collection<UUID> channelMembersWithSocialSpy(ChatChannel channel) {
        Collection<UUID> members = new ArrayList<>(channel.getMembers());

        Social.get().getUserManager().get().stream()
            .filter(user -> !members.contains(user.getUuid()) && user.isSocialSpy())
            .map(SocialUser::getUuid)
            .forEach(members::add);

        return members;
    }

    public Component getClickableChannelIcon(SocialUser user, ChatChannel channel) {
        Component channelIcon = Component.text(channel.getIcon())
            .hoverEvent(getChannelHoverText(user, channel))
            .clickEvent(ClickEvent.runCommand("/social:social channel " + channel.getName()));

        return channelIcon;
    }

    private Component getChannelHoverText(SocialUser user, ChatChannel channel) {
        Component channelHoverText = Component.empty();
        if (channel.isShowHoverText())
            channelHoverText = channelHoverText
                .append(Component.text(Social.get().getConfig().getSettings().getChat().getChannelHoverText()))
                .appendNewline()
                .append(channelHoverText);

        return channelHoverText;
    }

    public Component getNicknameWithColor(SocialUser user, ChatChannel channel) {
        Component nickname = Component.empty()
            .append(Component.text(Social.get().getConfig().getSettings().getChat().getPlayerNicknameFormat()))
            .colorIfAbsent(channel.getNicknameColor());

        return nickname;
    }

    public Component getReplyIcon(SocialUser sender, SocialChatMessagePrepareEvent message) {
        Component replyIcon = Component.empty();
        
        // Check that message is a reply
        if (!message.isReply())
            return replyIcon;

        // Check that sender has permission to send messages on this channel
        if (!Social.get().getChatManager().hasPermission(sender, message.getChannel()))
            return replyIcon;

        // Get the reply context
        SocialMessageContext reply = Social.get().getChatManager().getHistory().getById(message.getReplyId());

        // Chain of replies (thread)
        if (reply.isReply())
            message.setReplyId(reply.replyId());

        // Switch channel if necessary
        if (!reply.chatChannel().equals(message.getChannel()) && Social.get().getChatManager().hasPermission(sender, reply.chatChannel()))
            message.setChannel(reply.chatChannel());

        // Icon hover text
        Component hoverText = Component.empty();
        for (SocialMessageContext threadReply : Social.get().getChatManager().getHistory().getThread(reply, 8)) {
            hoverText = hoverText
                .append(Component.text(threadReply.sender().getNickname() + ": ", NamedTextColor.GRAY))
                .append(Component.text(threadReply.rawMessage()).color(NamedTextColor.WHITE))
                .appendNewline();
        }

        // Show '...' if there are more replies in this thread
        if (Social.get().getChatManager().getHistory().getThread(reply, 9).size() >= 8) {
            hoverText = hoverText
                .append(Component.text("...", NamedTextColor.BLUE))
                .appendNewline();
        }

        // Show 'click to reply' text
        hoverText = hoverText
            .appendNewline()
            .append(Component.text(Social.get().getConfig().getSettings().getChat().getReplyHoverText()));

        // Set icon
        replyIcon = Component.text(Social.get().getConfig().getSettings().getChat().getReplyFormat())
            .hoverEvent(hoverText)
            .clickEvent(ClickEvent.suggestCommand("(re:#" + reply.id() + ") "))
            .appendSpace()
            .append(Component.text("(#" + reply.id() + ")", NamedTextColor.DARK_GRAY))
            .appendSpace();

        return replyIcon;
    }

}
