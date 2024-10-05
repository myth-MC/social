package ovh.mythmc.social.api.features;

public interface SocialFeature {

    SocialFeatureType featureType();

    boolean canBeEnabled();

    void enable();

    void disable();

}
