package ovh.mythmc.social.paper.adventure;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.audience.Audience;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.user.AbstractSocialUser;

public final class PaperAdventureProvider extends SocialAdventureProvider {

    @Override
    public Audience user(@NotNull AbstractSocialUser user) {
        return Bukkit.getPlayer(user.uuid());
    }

    @Override
    public Audience console() {
        return Bukkit.getConsoleSender();
    }
    
}
