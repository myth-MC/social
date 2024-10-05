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

    private final Map<SocialFeature, Boolean> featureMap = new HashMap<>();

    public void registerFeature(final @NotNull SocialFeature... features) {
        Arrays.stream(features).forEach(feature -> {
            if (!featureMap.containsKey(feature)) {
                featureMap.put(feature, false);
                if (Social.get().getConfig().getSettings().isDebug())
                    Social.get().getLogger().info("Registered feature " + feature.featureType());
            }
        });
    }

    public void unregisterFeature(final @NotNull SocialFeature... features) {
        Arrays.stream(features).forEach(feature -> {
            featureMap.remove(feature);
            if (Social.get().getConfig().getSettings().isDebug())
                Social.get().getLogger().info("Unregistered feature " + feature.featureType());
        });
    }

    public void enableAllFeatures() {
        for (SocialFeature feature : featureMap.keySet()) {
            if (feature.canBeEnabled() && isSupported(feature.featureType())) {
                feature.enable();
                if (Social.get().getConfig().getSettings().isDebug())
                    Social.get().getLogger().info("Enabled feature " + feature.featureType());
            }
        }
    }

    public void disableAllFeatures() {
        for (int i = 0; i < featureMap.keySet().size(); i++) {
            SocialFeature feature = featureMap.keySet().stream().toList().get(i);
            if (!featureMap.get(feature))
                continue;

            feature.disable();
            if (Social.get().getConfig().getSettings().isDebug())
                Social.get().getLogger().info("Disabled feature " + feature.featureType());
        }
    }

    public List<SocialFeature> getByType(final @NotNull SocialFeatureType socialFeatureType) {
        return featureMap.keySet().stream().filter(feature -> feature.featureType().equals(socialFeatureType)).toList();
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
