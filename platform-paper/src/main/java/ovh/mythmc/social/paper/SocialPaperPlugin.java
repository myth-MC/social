package ovh.mythmc.social.paper;

import org.bukkit.plugin.java.JavaPlugin;

public class SocialPaperPlugin extends JavaPlugin {

    private SocialPaper bootstrap;

    @Override
    public void onEnable() {
        bootstrap = new SocialPaper(this);
        bootstrap.initialize();;
    }

    @Override
    public void onDisable() {
        bootstrap.shutdown();
    }
    
}
