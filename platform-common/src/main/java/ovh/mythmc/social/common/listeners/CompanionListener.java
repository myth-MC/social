package ovh.mythmc.social.common.listeners;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.events.chat.SocialChannelCreateEvent;
import ovh.mythmc.social.api.events.chat.SocialChannelDeleteEvent;
import ovh.mythmc.social.api.events.chat.SocialChannelPostSwitchEvent;
import ovh.mythmc.social.api.users.SocialUser;
import ovh.mythmc.social.common.adapters.PlatformAdapter;

@RequiredArgsConstructor
@SuppressWarnings("null")
public final class CompanionListener implements Listener, PluginMessageListener {

    private final JavaPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialUser user = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
        if (user == null)
            return;

        Social.get().getUserManager().disableCompanion(user);

        PlatformAdapter.get().runAsyncTaskLater(plugin, () -> {
            if (user.getPlayer() == null || !user.isCompanion())
                return;

            user.getCompanion().clear();
            user.getCompanion().refresh();
            user.getCompanion().mainChannel(Social.get().getChatManager().getDefaultChannel());
        }, 15);
    }
    
    @EventHandler
    public void onChannelCreate(SocialChannelCreateEvent event) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            SocialUser user = Social.get().getUserManager().getByUuid(player.getUniqueId());
            if (user == null || !user.isCompanion())
                return;

            if (event.getChannel() instanceof GroupChatChannel groupChannel &&
                !groupChannel.getMemberUuids().contains(player.getUniqueId()))
                return;

            if (Social.get().getChatManager().hasPermission(user, event.getChannel()))
                user.getCompanion().open(event.getChannel());
        });    
    }

    @EventHandler
    public void onChannelDelete(SocialChannelDeleteEvent event) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            SocialUser user = Social.get().getUserManager().getByUuid(player.getUniqueId());
            if (user == null || !user.isCompanion())
                return;

            if (Social.get().getChatManager().hasPermission(user, event.getChannel())) {
                user.getCompanion().close(event.getChannel());
                if (user.getMainChannel().equals(event.getChannel()))
                    user.getCompanion().mainChannel(Social.get().getChatManager().getDefaultChannel());
            }
        });
    }

    @EventHandler
    public void onChannelSwitch(SocialChannelPostSwitchEvent event) {
        if (event.getUser().isCompanion())
            event.getUser().getCompanion().mainChannel(event.getChannel());
    }

    @Override
    public void onPluginMessageReceived(String pluginMessageChannel, Player player, byte[] message) {
        SocialUser user = Social.get().getUserManager().getByUuid(player.getUniqueId());
        if (user == null)
            return;

        switch (pluginMessageChannel) {
            case "social:refresh" -> {
                if (user.isCompanion())
                    user.getCompanion().refresh();
            }
            case "social:bonjour" -> {
                if (Social.get().getConfig().getGeneral().isDebug())
                    Social.get().getLogger().info("Received bonjour message from " + player.getName() + "! Companion features will be enabled");

                Social.get().getUserManager().enableCompanion(user);
            }
            case "social:switch" -> {
                if (!user.isCompanion())
                    return;

                ChatChannel channel = Social.get().getChatManager().getChannel(new String(message));
                if (channel != null)
                    Social.get().getUserManager().setMainChannel(user, channel);
            }
            case "social:preview" -> {
                if (!user.isCompanion())
                    return;

                PlatformAdapter.get().runAsyncTask(plugin, () -> {
                    var filteredMessage = Social.get().getTextProcessor().parsePlayerInput(SocialParserContext.builder()
                        .user(user)
                        .message(Component.text(new String(message)))
                        .build());

                    var context = new SocialRegisteredMessageContext(0, 0, user, user.getMainChannel(), Set.of(user), filteredMessage, "", null, null);
                    var rendered = Social.get().getChatManager().getRenderer(SocialUser.class).render(new SocialUser.Dummy(user.getMainChannel()), context);

                    user.getCompanion().preview(rendered.prefix().append(rendered.message()));
                });
            }
        }
    }

}
