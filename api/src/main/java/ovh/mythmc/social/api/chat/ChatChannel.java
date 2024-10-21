package ovh.mythmc.social.api.chat;

import lombok.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.configuration.sections.settings.ChatSettings;
import ovh.mythmc.social.api.players.SocialPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ChatChannel {

    private final String name;

    private final TextColor color;

    private final ChannelType type;

    private final String icon;

    private final boolean showHoverText;

    private final Component hoverText;

    private final TextColor nicknameColor;

    private final String textDivider;

    private final TextColor textColor;

    private final String permission;

    private final boolean joinByDefault;

    private final boolean passthrough;

    private List<UUID> members = new ArrayList<>();

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

    public static ChatChannel fromConfigField(final @NotNull ChatSettings.Channel channelField) {
        return new ChatChannel(
                channelField.name(),
                TextColor.fromHexString(channelField.color()),
                ChannelType.CHAT,
                channelField.icon(),
                channelField.showHoverText(),
                getHoverTextAsComponent(channelField.hoverText()),
                TextColor.fromHexString(channelField.nicknameColor()),
                channelField.textDivider(),
                TextColor.fromHexString(channelField.textColor()),
                channelField.permission(),
                channelField.joinByDefault(),
                false
        );
    }

    protected static Component getHoverTextAsComponent(List<String> hoverTextList) {
        Component hoverText = Component.empty();
        for (String line : hoverTextList) {
            Component parsedLine = MiniMessage.miniMessage().deserialize(line);
            hoverText = hoverText
                    .appendNewline()
                    .append(parsedLine);
        }

        return hoverText;
    }

}
