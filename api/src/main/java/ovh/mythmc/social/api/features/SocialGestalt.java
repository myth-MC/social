package ovh.mythmc.social.api.features;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;

import java.util.*;

public final class SocialGestalt {

    private static SocialGestalt socialGestalt;

    public static void set(final @NotNull SocialGestalt s) {
        socialGestalt = s;
    }

    public static @NotNull SocialGestalt get() {
        return socialGestalt;
    }

    private final List<SocialFeature> featureList = new ArrayList<>();

    public void registerFeature(final @NotNull SocialFeature... features) {
        Arrays.stream(features).forEach(feature -> {
            if (!featureList.contains(feature)) {
                featureList.add(feature);
                if (Social.get().getConfig().getSettings().isDebug())
                    Social.get().getLogger().info("Registered feature " + feature.featureType());
            }
        });
    }

    public void unregisterFeature(final @NotNull SocialFeature... features) {
        featureList.removeAll(Arrays.stream(features).toList());
    }

    public void enableAllFeatures() {
        for (SocialFeature feature : featureList) {
            if (feature.canBeEnabled() && isSupported(feature.featureType())) {
                feature.enable();
                if (Social.get().getConfig().getSettings().isDebug())
                    Social.get().getLogger().info("Enabled feature " + feature.featureType());
            }
        }
    }

    public void disableAllFeatures() {
        for (int i = 0; i < featureList.size(); i++) {
            SocialFeature feature = featureList.get(i);
            feature.disable();
            if (Social.get().getConfig().getSettings().isDebug())
                Social.get().getLogger().info("Disabled feature " + feature.featureType());
        }
    }

    public List<SocialFeature> getByType(final @NotNull SocialFeatureType socialFeatureType) {
        return featureList.stream().filter(feature -> feature.featureType().equals(socialFeatureType)).toList();
    }

    public boolean isEnabled(final @NotNull SocialFeatureType featureType) {
        return getByType(featureType) != null && !getByType(featureType).isEmpty();
    }

    public boolean isSupported(final @NotNull SocialFeatureType feature) {
        String version = Bukkit.getBukkitVersion().split("-")[0];

        return switch (feature) {
            case EMOJIS -> version.startsWith("1.18") ||
                    version.startsWith("1.19") ||
                    version.startsWith("1.20") ||
                    version.startsWith("1.21");
            case REACTIONS -> version.startsWith("1.20") ||
                    version.startsWith("1.21");
            default -> true;
        };
    }

}
