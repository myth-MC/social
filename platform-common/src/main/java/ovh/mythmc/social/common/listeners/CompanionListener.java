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
import ovh.mythmc.gestalt.key.IdentifierKey;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callbacks.channel.SocialChannelCreateCallback;
import ovh.mythmc.social.api.callbacks.channel.SocialChannelDeleteCallback;
import ovh.mythmc.social.api.callbacks.channel.SocialChannelPostSwitchCallback;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.chat.GroupChatChannel;
import ovh.mythmc.social.api.context.SocialRegisteredMessageContext;
import ovh.mythmc.social.api.context.SocialParserContext;
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
            if (user.player().isEmpty() || !user.isCompanion())
                return;

            user.getCompanion().clear();
            user.getCompanion().refresh();
            user.getCompanion().mainChannel(Social.get().getChatManager().getDefaultChannel());
        }, 15);
    }

    public void registerCallbackHandlers() {
        SocialChannelCreateCallback.INSTANCE.registerHandler("social:companionChannelCreate", (ctx) -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                SocialUser user = Social.get().getUserManager().getByUuid(player.getUniqueId());
                if (user == null || !user.isCompanion())
                    return;
    
                if (ctx.channel() instanceof GroupChatChannel groupChannel &&
                    !groupChannel.getMemberUuids().contains(player.getUniqueId()))
                    return;
    
                if (Social.get().getChatManager().hasPermission(user, ctx.channel()))
                    user.getCompanion().open(ctx.channel());
            });    
        });

        SocialChannelDeleteCallback.INSTANCE.registerHandler("social:companionChannelDelete", (ctx) -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                SocialUser user = Social.get().getUserManager().getByUuid(player.getUniqueId());
                if (user == null || !user.isCompanion())
                    return;
    
                if (Social.get().getChatManager().hasPermission(user, ctx.channel())) {
                    user.getCompanion().close(ctx.channel());
                    if (user.getMainChannel().equals(ctx.channel()))
                        user.getCompanion().mainChannel(Social.get().getChatManager().getDefaultChannel());
                }
            });
        });

        SocialChannelPostSwitchCallback.INSTANCE.registerHandler("social:companionMainChannelSetter", (ctx) -> {
            if (ctx.user().isCompanion())
                ctx.user().getCompanion().mainChannel(ctx.channel());
        });
    }

    public void unregisterCallbackHandlers() {
        SocialChannelCreateCallback.INSTANCE.unregisterHandlers(IdentifierKey.of("social", "companionChannelCreate"));
        SocialChannelDeleteCallback.INSTANCE.unregisterHandlers(IdentifierKey.of("social", "companionChannelDelete"));
        SocialChannelPostSwitchCallback.INSTANCE.unregisterHandlers(IdentifierKey.of("social", "companionMainChannelSetter"));
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
                    var filteredMessage = Social.get().getTextProcessor().parsePlayerInput(
                        SocialParserContext.builder(user, Component.text(new String(message)))
                            .build());

                    var context = new SocialRegisteredMessageContext(0, 0, user, user.getMainChannel(), Set.of(user), filteredMessage, "", null, null);
                    var rendered = Social.get().getChatManager().getRegisteredRenderer(SocialUser.class).render(SocialUser.dummy(user.getMainChannel()), context);

                    user.getCompanion().preview(rendered.prefix().append(rendered.message()));
                });
            }
        }
    }

}
