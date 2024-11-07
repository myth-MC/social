package ovh.mythmc.social.api.text.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SocialParserProperties {

    ParserPriority priority() default ParserPriority.NORMAL;

    enum ParserPriority {
        HIGHEST,
        HIGH,
        NORMAL,
        LOW,
        LOWEST
    }

}
