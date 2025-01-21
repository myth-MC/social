package ovh.mythmc.social.api.configuration.sections.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
public class GeneralSettings {
    
    @Comment("Enabling this will send more logs to console to help debugging")
    private boolean debug = false;

    @Comment("Whether to enable the update checker or not")
    private boolean updateChecker = true;

    @Comment("Time interval of the update checker in hours")
    private int updateCheckerIntervalInHours = 6;

    @Comment("Date format to use in menus and other text fields (mainly chat history)")
    private String dateFormat = "MM-dd hh:mm";
    
    @Setter
    private int migrationVersion = 1;

}
