package ovh.mythmc.social.common.features;

import org.bukkit.event.HandlerList;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.chat.ChatChannel;
import ovh.mythmc.social.api.features.SocialFeature;
import ovh.mythmc.social.api.features.SocialFeatureType;
import ovh.mythmc.social.common.listeners.ChatListener;
import ovh.mythmc.social.common.util.PluginUtil;

public final class ChatFeature implements SocialFeature {

    private final ChatListener chatListener;

    public ChatFeature() {
        this.chatListener = new ChatListener();
    }

    @Override
    public SocialFeatureType featureType() {
        return SocialFeatureType.CHAT;
    }

    @Override
    public boolean canBeEnabled() {
        return Social.get().getConfig().getSettings().getChat().isEnabled();
    }

    @Override
    public void enable() {
        PluginUtil.registerEvents(chatListener);

        // Assign channels to every SocialPlayer
        ChatChannel defaultChannel = Social.get().getChatManager().getChannel(Social.get().getConfig().getSettings().getChat().getDefaultChannel());
        if (defaultChannel != null) {
            Social.get().getPlayerManager().get().forEach(socialPlayer -> {
                Social.get().getChatManager().assignChannelsToPlayer(socialPlayer);
                Social.get().getPlayerManager().setMainChannel(socialPlayer, defaultChannel);
            });
        }
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(chatListener);
        Social.get().getChatManager().getChannels().clear();
    }

}
