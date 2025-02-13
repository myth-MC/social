package ovh.mythmc.social.api.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callbacks.group.SocialGroupJoin;
import ovh.mythmc.social.api.callbacks.group.SocialGroupJoinCallback;
import ovh.mythmc.social.api.callbacks.group.SocialGroupLeave;
import ovh.mythmc.social.api.callbacks.group.SocialGroupLeaveCallback;
import ovh.mythmc.social.api.users.SocialUser;

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
                TextColor.fromHexString(Social.get().getConfig().getSettings().getChat().getGroups().getColor()),
                Social.get().getConfig().getSettings().getChat().getGroups().getIcon(),
                Social.get().getConfig().getSettings().getChat().getGroups().isShowHoverText(),
                getHoverTextAsComponent(Social.get().getConfig().getSettings().getChat().getGroups().getHoverText()),
                TextColor.fromHexString(Social.get().getConfig().getSettings().getChat().getGroups().getNicknameColor()),
                Social.get().getConfig().getSettings().getChat().getGroups().getTextDivider(),
                TextColor.fromHexString(Social.get().getConfig().getSettings().getChat().getGroups().getTextColor()),
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
        if (getMembers().size() >= Social.get().getConfig().getSettings().getChat().getGroups().getPlayerLimit())
            return false;

        super.addMember(uuid);

        var callback = new SocialGroupJoin(this, getLeader());
        SocialGroupJoinCallback.INSTANCE.handle(callback);

        return true;
    }

    public boolean removeMember(UUID uuid) {
        var callback = new SocialGroupLeave(this, getLeader());
        SocialGroupLeaveCallback.INSTANCE.handle(callback);

        return super.removeMember(uuid);
    }

    public SocialUser getLeader() {
        return Social.get().getUserManager().getByUuid(leaderUuid);
    }

}
