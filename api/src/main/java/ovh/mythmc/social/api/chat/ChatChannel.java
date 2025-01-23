package ovh.mythmc.social.api.chat;

import lombok.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.configuration.sections.settings.ChatSettings;
import ovh.mythmc.social.api.users.SocialUser;

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

    public boolean addMember(SocialUser user) {
        return addMember(user.getUuid());
    }

    public boolean removeMember(UUID uuid) {
        return members.remove(uuid);
    }

    public boolean removeMember(SocialUser user) {
        return removeMember(user.getUuid());
    }

    public static ChatChannel fromConfigField(final @NotNull ChatSettings.Channel channelField) {
        String name = channelField.name();
        TextColor color = NamedTextColor.YELLOW;
        String icon = "<dark_gray>[<yellow>:raw_pencil:</yellow>]</dark_gray>";
        Boolean showHoverText = false;
        Component hoverText = Component.empty();
        TextColor nicknameColor = NamedTextColor.GRAY;
        String textDivider = "<gray>:raw_divider:</gray>";
        TextColor textColor = NamedTextColor.WHITE;
        String permission = null;
        boolean joinByDefault = false;

        if (channelField.inherit() != null) { // Inherit properties from another channel
            ChatChannel inherit = Social.get().getChatManager().getChannel(channelField.inherit());
            
            color = inherit.getColor();
            icon = inherit.getIcon();
            showHoverText = inherit.isShowHoverText();
            hoverText = inherit.getHoverText();
            nicknameColor = inherit.getNicknameColor();
            textDivider = inherit.getTextDivider();
            textColor = inherit.getTextColor();
            permission = inherit.getPermission();
            joinByDefault = inherit.isJoinByDefault();
        }

        if (channelField.color() != null)
            color = TextColor.fromHexString(channelField.color());

        if (channelField.icon() != null)
            icon = channelField.icon();

        if (channelField.showHoverText() != null)
            showHoverText = channelField.showHoverText();

        if (channelField.hoverText() != null)
            hoverText = getHoverTextAsComponent(channelField.hoverText());

        if (channelField.nicknameColor() != null)
            nicknameColor = TextColor.fromHexString(channelField.nicknameColor());

        if (channelField.textDivider() != null)
            textDivider = channelField.textDivider();

        if (channelField.textColor() != null)
            textColor = TextColor.fromHexString(channelField.textColor());

        if (channelField.permission() != null)
            permission = channelField.permission();

        if (channelField.joinByDefault() != null)
            joinByDefault = channelField.joinByDefault();

        return new ChatChannel(
            name, 
            color, 
            ChannelType.CHAT, 
            icon, 
            showHoverText, 
            hoverText, 
            nicknameColor, 
            textDivider, 
            textColor, 
            permission, 
            joinByDefault, 
            false
        );

        /*
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
        ); */
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
