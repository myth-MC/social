package ovh.mythmc.social.api.text.injection.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialInjectionTagParser extends SocialInjectionEmptyParser {

    public final static SocialInjectionTagParser INSTANCE = new SocialInjectionTagParser();

}
