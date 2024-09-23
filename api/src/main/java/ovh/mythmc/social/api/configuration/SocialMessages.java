package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.social.api.configuration.sections.messages.ErrorsConfig;

@Configuration
@Getter
public class SocialMessages {

    @Comment("General errors")
    public ErrorsConfig errors = new ErrorsConfig();

}
