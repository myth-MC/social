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
                feature.initialize();
                if (Social.get().getConfig().getSettings().isDebug())
                    Social.get().getLogger().info("Registered feature " + feature.getClass().getSimpleName() + " (" + feature.featureType() + ")");
            }
        });
    }

    public void unregisterFeature(final @NotNull SocialFeature... features) {
        Arrays.stream(features).forEach(feature -> {
            featureMap.remove(feature);
            feature.shutdown();
            if (Social.get().getConfig().getSettings().isDebug())
                Social.get().getLogger().info("Unregistered feature " + feature.getClass().getSimpleName() + " (" + feature.featureType() + ")");
        });
    }

    public void unregisterAllFeatures() {
        for (int i = 0; i < featureMap.keySet().size(); i++) {
            SocialFeature feature = featureMap.keySet().stream().toList().get(i);
            if (!featureMap.get(feature))
                continue;

            unregisterFeature(feature);
        }
    }

    public void enableFeature(final @NotNull SocialFeature feature) {
        if (feature.canBeEnabled() && isSupported(feature.featureType())) {
            featureMap.put(feature, true);
            feature.enable();
            if (Social.get().getConfig().getSettings().isDebug())
                Social.get().getLogger().info("Enabled feature " + feature.getClass().getSimpleName() + " (" + feature.featureType() + ")");
        }
    }

    public void disableFeature(final @NotNull SocialFeature feature) {
        if (featureMap.get(feature)) {
            featureMap.put(feature, false);
            feature.disable();
            if (Social.get().getConfig().getSettings().isDebug())
                Social.get().getLogger().info("Disabled feature " + feature.getClass().getSimpleName() + " (" + feature.featureType() + ")");
        }
    }

    public void enableAllFeatures() {
        getByPriority(SocialFeatureProperties.Priority.HIGH).forEach(feature -> {
            enableFeature(feature);
        });

        getByPriority(SocialFeatureProperties.Priority.NORMAL).forEach(feature -> {
            enableFeature(feature);
        });

        getByPriority(SocialFeatureProperties.Priority.LOW).forEach(feature -> {
            enableFeature(feature);
        });
    }

    public void disableAllFeatures() {
        for (SocialFeature feature : featureMap.keySet()) {
            disableFeature(feature);
        }
    }

    public List<SocialFeature> getByType(final @NotNull SocialFeatureType socialFeatureType) {
        return featureMap.keySet().stream().filter(feature -> feature.featureType().equals(socialFeatureType)).toList();
    }

    public List<SocialFeature> getByPriority(final @NotNull SocialFeatureProperties.Priority priority) {
        List<SocialFeature> features = new ArrayList<>();
        for (SocialFeature feature : featureMap.keySet()) {
            SocialFeatureProperties.Priority featurePriority = SocialFeatureProperties.Priority.NORMAL;
            if (feature.getClass().isAnnotationPresent(SocialFeatureProperties.class))
                featurePriority = feature.getClass().getAnnotation(SocialFeatureProperties.class).priority();

            if (featurePriority.equals(priority))
                features.add(feature);
        }

        return features;
    }

    public boolean isEnabled(final @NotNull SocialFeatureType featureType) {
        boolean enabled = false;

        for (SocialFeature feature : getByType(featureType)) {
            if (featureMap.get(feature)) {
                enabled = true;
                break;
            }
        }

        return enabled;
    }

    public boolean isSupported(final @NotNull SocialFeatureType feature) {
        String version = Bukkit.getBukkitVersion().split("-")[0];

        return switch (feature) {
            case EMOJIS ->
                    version.startsWith("1.16") ||
                    version.startsWith("1.17") ||
                    version.startsWith("1.18") ||
                    version.startsWith("1.19") ||
                    version.startsWith("1.20") ||
                    version.startsWith("1.21");
            case REACTIONS ->
                    version.startsWith("1.20") ||
                    version.startsWith("1.21");
            case SERVER_LINKS ->
                    version.startsWith("1.21");
            default -> true;
        };
    }

}
