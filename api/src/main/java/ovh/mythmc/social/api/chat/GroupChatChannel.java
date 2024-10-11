package ovh.mythmc.social.api.chat;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;

import java.util.UUID;

@Getter
@Setter
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
        if (getMembers().contains(uuid))
            return false;

        if (getMembers().size() >= Social.get().getConfig().getSettings().getChat().getGroups().getPlayerLimit())
            return false;

        getMembers().add(uuid);
        return true;
    }

}
