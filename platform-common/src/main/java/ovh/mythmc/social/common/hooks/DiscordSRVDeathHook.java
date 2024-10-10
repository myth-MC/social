package ovh.mythmc.social.common.hooks;

import github.scarsz.discordsrv.Debug;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.events.DeathMessagePostProcessEvent;
import github.scarsz.discordsrv.api.events.DeathMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.MessageFormat;
import github.scarsz.discordsrv.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.function.BiFunction;

// Todo: change to a better solution
// Copy of DiscordSRV's PlayerDeathListener with a lower EventPriority
public final class DiscordSRVDeathHook implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;

        Player player = event.getEntity();

        // respect invisibility plugins
        if (PlayerUtil.isVanished(player)) return;

        String message = event.getDeathMessage();
        if (message == null) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            message = message.replace(p.getName(), p.getDisplayName());
        }

        String finalMessage = message;
        SchedulerUtil.runTaskAsynchronously(DiscordSRV.getPlugin(), () -> runAsync(event, player, finalMessage));
    }

    private void runAsync(PlayerDeathEvent event, Player player, String deathMessage) {
        if (StringUtils.isBlank(deathMessage)) {
            DiscordSRV.debug("[social] Not sending death message for " + player.getName() + ", the death message is null");
            return;
        }

        String channelName = DiscordSRV.getPlugin().getOptionalChannel("deaths");
        MessageFormat messageFormat = DiscordSRV.getPlugin().getMessageFromConfiguration("MinecraftPlayerDeathMessage");
        if (messageFormat == null) return;

        DeathMessagePreProcessEvent preEvent = DiscordSRV.api.callEvent(new DeathMessagePreProcessEvent(channelName, messageFormat, player, deathMessage, event));
        if (preEvent.isCancelled()) {
            DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "[social] DeathMessagePreProcessEvent was cancelled, message send aborted");
            return;
        }

        // Update from event in case any listeners modified parameters
        channelName = preEvent.getChannel();
        messageFormat = preEvent.getMessageFormat();
        deathMessage = preEvent.getDeathMessage();

        if (messageFormat == null) return;

        String finalDeathMessage = StringUtils.isNotBlank(deathMessage) ? deathMessage : "";
        String avatarUrl = DiscordSRV.getAvatarUrl(event.getEntity());
        String botAvatarUrl = DiscordUtil.getJda().getSelfUser().getEffectiveAvatarUrl();
        String botName = DiscordSRV.getPlugin().getMainGuild() != null ? DiscordSRV.getPlugin().getMainGuild().getSelfMember().getEffectiveName() : DiscordUtil.getJda().getSelfUser().getName();
        String displayName = StringUtils.isNotBlank(player.getDisplayName()) ? MessageUtil.strip(player.getDisplayName()) : "";

        TextChannel destinationChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channelName);
        BiFunction<String, Boolean, String> translator = (content, needsEscape) -> {
            if (content == null) return null;
            content = content
                    .replaceAll("%time%|%date%", TimeUtil.timeStamp())
                    .replace("%username%", needsEscape ? DiscordUtil.escapeMarkdown(player.getName()) : player.getName())
                    .replace("%displayname%", needsEscape ? DiscordUtil.escapeMarkdown(displayName) : displayName)
                    .replace("%usernamenoescapes%", player.getName())
                    .replace("%displaynamenoescapes%", displayName)
                    .replace("%world%", player.getWorld().getName())
                    .replace("%deathmessage%", MessageUtil.strip(needsEscape ? DiscordUtil.escapeMarkdown(finalDeathMessage) : finalDeathMessage))
                    .replace("%deathmessagenoescapes%", MessageUtil.strip(finalDeathMessage))
                    .replace("%embedavatarurl%", avatarUrl)
                    .replace("%botavatarurl%", botAvatarUrl)
                    .replace("%botname%", botName);
            if (destinationChannel != null) content = DiscordUtil.translateEmotes(content, destinationChannel.getGuild());
            content = PlaceholderUtil.replacePlaceholdersToDiscord(content, player);
            return content;
        };
        Message discordMessage = DiscordSRV.translateMessage(messageFormat, translator);
        if (discordMessage == null) return;

        String webhookName = translator.apply(messageFormat.getWebhookName(), false);
        String webhookAvatarUrl = translator.apply(messageFormat.getWebhookAvatarUrl(), false);

        if (DiscordSRV.getLength(discordMessage) < 3) {
            DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "[social] Not sending death message, because it's less than three characters long. Message: " + messageFormat);
            return;
        }

        DeathMessagePostProcessEvent postEvent = DiscordSRV.api.callEvent(new DeathMessagePostProcessEvent(channelName, discordMessage, player, deathMessage, event, messageFormat.isUseWebhooks(), webhookName, webhookAvatarUrl, preEvent.isCancelled()));
        if (postEvent.isCancelled()) {
            DiscordSRV.debug(Debug.MINECRAFT_TO_DISCORD, "[social] DeathMessagePostProcessEvent was cancelled, message send aborted");
            return;
        }

        // Update from event in case any listeners modified parameters
        channelName = postEvent.getChannel();
        discordMessage = postEvent.getDiscordMessage();

        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channelName);
        if (postEvent.isUsingWebhooks()) {
            WebhookUtil.deliverMessage(textChannel, postEvent.getWebhookName(), postEvent.getWebhookAvatarUrl(),
                    discordMessage.getContentRaw(), discordMessage.getEmbeds().stream().findFirst().orElse(null));
        } else {
            DiscordUtil.queueMessage(textChannel, discordMessage, true);
        }
    }


}
