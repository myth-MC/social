package ovh.mythmc.social.api.chat;

import lombok.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.configuration.sections.settings.ChatConfig;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public final class ChatChannel {

    private final String name;

    private final String icon;

    private final TextColor iconColor;

    private final Component hoverText;

    private final TextColor nicknameColor;

    private final String textSeparator;

    private final TextColor textColor;

    private final String permission;

    private final boolean passthrough;

    private Collection<UUID> members = new ArrayList<>();

    public boolean addMember(UUID uuid) {
        if (members.contains(uuid))
            return false;

        members.add(uuid);
        return true;
    }

    public boolean addMember(SocialPlayer socialPlayer) {
        return addMember(socialPlayer.getUuid());
    }

    public boolean removeMember(UUID uuid) {
        return members.remove(uuid);
    }

    public boolean removeMember(SocialPlayer socialPlayer) {
        return removeMember(socialPlayer.getUuid());
    }

    public static ChatChannel fromConfigField(final @NotNull ChatConfig.Channel channelField) {
        Component hoverText = Component.text("");
        for (String line : channelField.hoverText()) {
            Component parsedLine = MiniMessage.miniMessage().deserialize(line);
            hoverText = hoverText.append(parsedLine);
        }

        return new ChatChannel(
                channelField.name(),
                channelField.icon(),
                TextColor.fromHexString(channelField.iconColor()),
                hoverText,
                TextColor.fromHexString(channelField.nicknameColor()),
                channelField.textSeparator(),
                TextColor.fromHexString(channelField.textColor()),
                channelField.permission(),
                false
        );
    }

}
