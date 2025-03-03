package ovh.mythmc.social.common.listener;

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
import ovh.mythmc.social.api.callback.channel.SocialChannelCreateCallback;
import ovh.mythmc.social.api.callback.channel.SocialChannelDeleteCallback;
import ovh.mythmc.social.api.callback.channel.SocialChannelPostSwitchCallback;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.user.SocialUser;
import ovh.mythmc.social.common.adapter.PlatformAdapter;
import ovh.mythmc.social.api.context.SocialParserContext;

@RequiredArgsConstructor
@SuppressWarnings("null")
public final class CompanionListener implements Listener, PluginMessageListener {

    private final JavaPlugin plugin;

    private static class IdentifierKeys {

        static final String COMPANION_CHANNEL_CREATE = "social:companion-channel-create";
        static final String COMPANION_CHANNEL_DELETE = "social:companion-channel-delete";
        static final String COMPANION_CHANNEL_SWITCH = "social:companion-channel-switch";

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SocialUser user = Social.get().getUserManager().getByUuid(event.getPlayer().getUniqueId());
        if (user == null)
            return;

        Social.get().getUserManager().disableCompanion(user);

        PlatformAdapter.get().runAsyncTaskLater(plugin, () -> {
            if (user.player().isEmpty() || user.companion().isEmpty())
                return;

            user.companion().get().clear();
            user.companion().get().refresh();
            user.companion().get().mainChannel(Social.get().getChatManager().getDefaultChannel());
        }, 15);
    }

    public void registerCallbackHandlers() {
        SocialChannelCreateCallback.INSTANCE.registerHandler(IdentifierKeys.COMPANION_CHANNEL_CREATE, (ctx) -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                SocialUser user = Social.get().getUserManager().getByUuid(player.getUniqueId());
                if (user == null || user.companion().isEmpty())
                    return;
    
                if (ctx.channel() instanceof GroupChatChannel groupChannel &&
                    !groupChannel.getMemberUuids().contains(player.getUniqueId()))
                    return;
    
                if (Social.get().getChatManager().hasPermission(user, ctx.channel()))
                    user.companion().get().open(ctx.channel());
            });    
        });

        SocialChannelDeleteCallback.INSTANCE.registerHandler(IdentifierKeys.COMPANION_CHANNEL_DELETE, (ctx) -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                SocialUser user = Social.get().getUserManager().getByUuid(player.getUniqueId());
                if (user == null || user.companion().isEmpty())
                    return;
    
                if (Social.get().getChatManager().hasPermission(user, ctx.channel())) {
                    user.companion().get().close(ctx.channel());
                    if (user.getMainChannel().equals(ctx.channel()))
                        user.companion().get().mainChannel(Social.get().getChatManager().getDefaultChannel());
                }
            });
        });

        SocialChannelPostSwitchCallback.INSTANCE.registerHandler(IdentifierKeys.COMPANION_CHANNEL_SWITCH, (ctx) -> {
            if (ctx.user().companion().isEmpty())
                ctx.user().companion().get().mainChannel(ctx.channel());
        });
    }

    public void unregisterCallbackHandlers() {
        SocialChannelCreateCallback.INSTANCE.unregisterHandlers(IdentifierKeys.COMPANION_CHANNEL_CREATE);
        SocialChannelDeleteCallback.INSTANCE.unregisterHandlers(IdentifierKeys.COMPANION_CHANNEL_DELETE);
        SocialChannelPostSwitchCallback.INSTANCE.unregisterHandlers(IdentifierKeys.COMPANION_CHANNEL_SWITCH);
    }

    @Override
    public void onPluginMessageReceived(String pluginMessageChannel, Player player, byte[] message) {
        SocialUser user = Social.get().getUserManager().getByUuid(player.getUniqueId());
        if (user == null)
            return;

        switch (pluginMessageChannel) {
            case "social:refresh" -> {
                user.companion().ifPresent(companion -> companion.refresh());
            }
            case "social:bonjour" -> {
                if (Social.get().getConfig().getGeneral().isDebug())
                    Social.get().getLogger().info("Received bonjour message from " + player.getName() + "! Companion features will be enabled");

                Social.get().getUserManager().enableCompanion(user);
            }
            case "social:switch" -> {
                if (user.companion().isEmpty())
                    return;

                ChatChannel channel = Social.get().getChatManager().getChannel(new String(message));
                if (channel != null)
                    Social.get().getUserManager().setMainChannel(user, channel);
            }
            case "social:preview" -> {
                if (user.companion().isEmpty())
                    return;

                PlatformAdapter.get().runAsyncTask(plugin, () -> {
                    var filteredMessage = Social.get().getTextProcessor().parsePlayerInput(
                        SocialParserContext.builder(user, Component.text(new String(message)))
                            .build());

                    var context = new SocialRegisteredMessageContext(0, 0, user, user.getMainChannel(), Set.of(user), filteredMessage, "", null, null);
                    var rendered = Social.get().getChatManager().getRegisteredRenderer(SocialUser.class).render(SocialUser.dummy(user.getMainChannel()), context);

                    user.companion().get().preview(rendered.prefix().append(rendered.message()));
                });
            }
        }
    }

}
