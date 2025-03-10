package ovh.mythmc.social.api.text.injection;

import ovh.mythmc.social.api.text.injection.defaults.SocialInjectionEmptyParser;
import ovh.mythmc.social.api.text.injection.defaults.SocialInjectionPlaceholder;

public final class SocialInjectionParsers {

    public static final SocialInjectionEmptyParser EMPTY = SocialInjectionEmptyParser.INSTANCE;

    public static final SocialInjectionPlaceholder PLACEHOLDER = SocialInjectionPlaceholder.INSTANCE;

}
