package ovh.mythmc.social.common.hooks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.emojis.Emoji;
import ovh.mythmc.social.api.hooks.SocialPluginHook;
import ovh.mythmc.social.common.util.PluginUtil;

import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

@Getter
public final class ProtocolLibHook extends SocialPluginHook<ProtocolManager> implements Listener {

    public ProtocolLibHook() {
        super(ProtocolLibrary.getProtocolManager());
        PluginUtil.registerEvents(this);
    }

    @Override
    public String identifier() {
        return "ProtocolLib";
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!Social.get().getConfig().getSettings().getEmojis().isEnabled())
            return;

        for (Emoji emoji : Social.get().getEmojiManager().getEmojis()) {
            if (emoji.name().length() > 14)
                continue;

            UUID uuid = UUID.randomUUID();

            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
            packet.getPlayerInfoDataLists().write(1, Collections.singletonList(new PlayerInfoData(
                    uuid,
                    100,
                    false,
                    EnumWrappers.NativeGameMode.CREATIVE,
                    new WrappedGameProfile(uuid, ":" + emoji.name() + ":"),
                    WrappedChatComponent.fromText(":" + emoji.name() + ":")
            )));

            get().sendServerPacket(event.getPlayer(), packet);
        }
    }

}
