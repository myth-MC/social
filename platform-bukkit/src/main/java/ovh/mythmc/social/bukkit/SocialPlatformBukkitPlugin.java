package ovh.mythmc.social.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class SocialPlatformBukkitPlugin extends JavaPlugin {

    private SocialPlatformBukkit bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new SocialPlatformBukkit(this);
        bootstrap.initialize();
    }

    @Override
    public void onDisable() {
        bootstrap.shutdown();
    }

}
