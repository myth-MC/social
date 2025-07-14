package ovh.mythmc.social.api.text.injection;

import ovh.mythmc.social.api.text.injection.defaults.SocialInjectionEmptyParser;
import ovh.mythmc.social.api.text.injection.defaults.SocialInjectionLiteralParser;
import ovh.mythmc.social.api.text.injection.defaults.SocialInjectionPlaceholder;
import ovh.mythmc.social.api.text.injection.defaults.SocialInjectionTagParser;

public final class SocialInjectionParsers {

    public static final SocialInjectionEmptyParser EMPTY = SocialInjectionEmptyParser.INSTANCE;

    public static final SocialInjectionLiteralParser LITERAL = SocialInjectionLiteralParser.INSTANCE;

    public static final SocialInjectionPlaceholder PLACEHOLDER = SocialInjectionPlaceholder.INSTANCE;

    public static final SocialInjectionTagParser TAG = SocialInjectionTagParser.INSTANCE;

}
