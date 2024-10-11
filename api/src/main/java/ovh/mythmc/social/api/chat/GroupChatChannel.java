package ovh.mythmc.social.api.chat;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@Setter
public class GroupChatChannel extends ChatChannel {

    private UUID leaderUuid;

    private final int code;

    public GroupChatChannel(final @NotNull UUID leaderUuid, final int code) {
        // Todo: move to settings.yml
        super(
                "G-" + code,
                NamedTextColor.BLUE,
                ChannelType.CHAT,
                "<dark_gray>[<gold>:raw_sword:</gold>]</dark_gray>",
                true,
                MiniMessage.miniMessage().deserialize("<gray>This is a private channel</gray>"),
                NamedTextColor.WHITE,
                ">",
                NamedTextColor.WHITE,
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

        if (getMembers().size() >= 8) // Todo: limit settings.yml
            return false;

        getMembers().add(uuid);
        return true;
    }

}
