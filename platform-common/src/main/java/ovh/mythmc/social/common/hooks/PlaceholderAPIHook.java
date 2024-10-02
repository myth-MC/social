package ovh.mythmc.social.common.hooks;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.events.SocialBootstrapEvent;
import ovh.mythmc.social.api.hooks.SocialPluginHook;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.text.SocialParser;
import ovh.mythmc.social.common.util.PluginUtil;

@Getter
public final class PlaceholderAPIHook extends SocialPluginHook<PlaceholderAPI> implements SocialParser, Listener {

    private final String identifier = "social";
    private final String author = "myth-MC";
    private final String version = Social.get().version();

    public PlaceholderAPIHook() {
        // this.register();
        // ^ not necessary for now
        super(null);
        PluginUtil.registerEvents(this);
        Social.get().getTextProcessor().registerParser(this);
    }

    @EventHandler
    public void onSocialBootstrap(SocialBootstrapEvent event) {
        Social.get().getTextProcessor().registerParser(this);
    }

    @Override
    public Component parse(SocialPlayer socialPlayer, Component message) {
        String serialized = MiniMessage.miniMessage().serialize(message);
        String parsedMessage = PlaceholderAPI.setPlaceholders(socialPlayer.getPlayer(), serialized);
        return MiniMessage.miniMessage().deserialize(parsedMessage);
    }

    @Override
    public String identifier() {
        return "PlaceholderAPI";
    }

}
