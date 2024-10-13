package ovh.mythmc.social.api.features;

public interface SocialFeature {

    SocialFeatureType featureType();

    boolean canBeEnabled();

    default void initialize() { }

    void enable();

    void disable();

    default void shutdown() { }

}
