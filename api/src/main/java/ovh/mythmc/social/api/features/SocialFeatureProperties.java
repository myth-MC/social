package ovh.mythmc.social.api.features;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SocialFeatureProperties {
    
    Priority priority() default Priority.NORMAL;

    enum Priority {
        HIGH,
        NORMAL,
        LOW
    }

}
