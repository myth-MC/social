package ovh.mythmc.social.paper;

import org.bukkit.plugin.java.JavaPlugin;

public class SocialPlatformPaperPlugin extends JavaPlugin {

    private SocialPlatformPaper bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new SocialPlatformPaper(this);
        bootstrap.initialize();;
    }

    @Override
    public void onDisable() {
        bootstrap.shutdown();
    }
    
}
