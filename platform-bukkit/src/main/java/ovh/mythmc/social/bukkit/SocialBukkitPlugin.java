package ovh.mythmc.social.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class SocialBukkitPlugin extends JavaPlugin {

    private SocialBukkit bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new SocialBukkit(this);
        bootstrap.initialize();
    }

    @Override
    public void onDisable() {
        bootstrap.shutdown();
    }

}
