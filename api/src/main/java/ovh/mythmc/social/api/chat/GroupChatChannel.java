package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.groups.SocialGroupJoinEvent;
import ovh.mythmc.social.api.events.groups.SocialGroupLeaveEvent;

import java.util.UUID;

@Getter
@Setter(AccessLevel.PROTECTED)
public class GroupChatChannel extends ChatChannel {

    private UUID leaderUuid;

    private final int code;

    public GroupChatChannel(final @NotNull UUID leaderUuid, final int code) {
        super(
                "G-" + code,
                TextColor.fromHexString(Social.get().getConfig().getSettings().getChat().getGroups().getColor()),
                ChannelType.CHAT,
                Social.get().getConfig().getSettings().getChat().getGroups().getIcon(),
                Social.get().getConfig().getSettings().getChat().getGroups().isShowHoverText(),
                getHoverTextAsComponent(Social.get().getConfig().getSettings().getChat().getGroups().getHoverText()),
                TextColor.fromHexString(Social.get().getConfig().getSettings().getChat().getGroups().getNicknameColor()),
                Social.get().getConfig().getSettings().getChat().getGroups().getTextDivider(),
                TextColor.fromHexString(Social.get().getConfig().getSettings().getChat().getGroups().getTextColor()),
                null,
                false,
                false
        );

        this.leaderUuid = leaderUuid;
        this.code = code;
    }

    @Override
    public boolean addMember(UUID uuid) {
        if (getMembers().size() >= Social.get().getConfig().getSettings().getChat().getGroups().getPlayerLimit())
            return false;

        super.addMember(uuid);

        SocialGroupJoinEvent socialGroupJoinEvent = new SocialGroupJoinEvent(this, Social.get().getPlayerManager().get(uuid));
        Bukkit.getPluginManager().callEvent(socialGroupJoinEvent);

        return true;
    }

    public boolean removeMember(UUID uuid) {
        SocialGroupLeaveEvent socialGroupLeaveEvent = new SocialGroupLeaveEvent(this, Social.get().getPlayerManager().get(uuid));
        Bukkit.getPluginManager().callEvent(socialGroupLeaveEvent);

        return super.removeMember(uuid);
    }

}
