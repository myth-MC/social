package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.group.SocialGroupJoin;
import ovh.mythmc.social.api.callback.group.SocialGroupJoinCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupLeave;
import ovh.mythmc.social.api.callback.group.SocialGroupLeaveCallback;
import ovh.mythmc.social.api.user.SocialUser;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

@Getter
@Setter(AccessLevel.PROTECTED)
public class GroupChatChannel extends ChatChannel {

    private UUID leaderUuid;

    private String alias;

    private final int code;

    public GroupChatChannel(final @NotNull UUID leaderUuid, final @Nullable String alias, final int code) {
        super(
                "G-" + code,
                TextColor.fromHexString(Social.get().getConfig().getChat().getGroups().getColor()),
                Social.get().getConfig().getChat().getGroups().getIcon(),
                Social.get().getConfig().getChat().getGroups().isShowHoverText(),
                getHoverTextAsComponent(Social.get().getConfig().getChat().getGroups().getHoverText()),
                TextColor.fromHexString(Social.get().getConfig().getChat().getGroups().getNicknameColor()),
                Social.get().getConfig().getChat().getGroups().getTextDivider(),
                TextColor.fromHexString(Social.get().getConfig().getChat().getGroups().getTextColor()),
                null,
                false
        );

        this.leaderUuid = leaderUuid;
        this.alias = alias;
        this.code = code;
    }

    public String getAliasOrName() {
        return Optional.ofNullable(alias)
            .orElse(this.getName());
    }

    @Override
    public boolean addMember(UUID uuid) {
        if (getMembers().size() >= Social.get().getConfig().getChat().getGroups().getPlayerLimit())
            return false;

        super.addMember(uuid);

        var callback = new SocialGroupJoin(this, getLeader());
        SocialGroupJoinCallback.INSTANCE.invoke(callback);

        return true;
    }

    public boolean removeMember(UUID uuid) {
        var callback = new SocialGroupLeave(this, getLeader());
        SocialGroupLeaveCallback.INSTANCE.invoke(callback);

        return super.removeMember(uuid);
    }

    public SocialUser getLeader() {
        return Social.get().getUserManager().getByUuid(leaderUuid);
    }

}
