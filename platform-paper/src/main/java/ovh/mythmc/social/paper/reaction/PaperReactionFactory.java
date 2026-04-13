package ovh.mythmc.social.paper.reaction;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.bukkit.BukkitSocialUser;
import ovh.mythmc.social.api.bukkit.scheduler.BukkitSocialScheduler;
import ovh.mythmc.social.api.callback.reaction.SocialReactionTrigger;
import ovh.mythmc.social.api.callback.reaction.SocialReactionTriggerCallback;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.reaction.ReactionFactory;
import ovh.mythmc.social.api.user.SocialUser;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Paper implementation of a reaction factory.
 */
public final class PaperReactionFactory extends ReactionFactory {

    private static final float DEFAULT_SCALE = 0.7f;

    private final JavaPlugin plugin;

    public PaperReactionFactory(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, ItemDisplay> playerReactions = new ConcurrentHashMap<>();

    @Override
    public void displayReaction(@NotNull SocialUser abstractSocialUser, @NotNull Reaction reaction) {
        BukkitSocialUser user = BukkitSocialUser.from(abstractSocialUser);
        Player player = user.player().orElse(null);

        if (!canDisplayReaction(player)) {
            return;
        }

        if (playerReactions.containsKey(user.uuid())) {
            return;
        }

        ItemDisplay itemDisplay = spawnItemDisplay(user, reaction);
        playerReactions.put(user.uuid(), itemDisplay);

        scheduleItemDisplayUpdate(player, itemDisplay);
    }

    @Override
    public void play(@NotNull SocialUser abstractSocialUser, @NotNull Reaction reaction) {
        BukkitSocialUser user = BukkitSocialUser.from(abstractSocialUser);
        SocialReactionTrigger trigger = new SocialReactionTrigger(user, reaction);

        SocialReactionTriggerCallback.INSTANCE.invoke(trigger, result -> {
            if (result.cancelled()) {
                return;
            }

            user.player().ifPresent(player ->
                    BukkitSocialScheduler.get().runEntityTask(player, () ->
                            displayReaction(result.user(), result.reaction())
                    )
            );
        });
    }

    private boolean canDisplayReaction(Player player) {
        if (player == null) {
            return false;
        }

        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            return false;
        }

        return player.getGameMode() != GameMode.SPECTATOR;
    }

    private ItemDisplay spawnItemDisplay(BukkitSocialUser user, Reaction reaction) {
        Player player = user.player()
                .orElseThrow(() -> new IllegalStateException("Player must be online to display a reaction."));

        Location location = player.getLocation();
        location.setPitch(0);
        location.setYaw(location.getYaw() - 180);

        double offsetY = Social.get().getConfig().getReactions().getOffsetY();
        ItemDisplay itemDisplay = (ItemDisplay) player.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY);

        if (reaction.particle() != null) {
            spawnParticleEffect(reaction, itemDisplay);
        }

        if (reaction.sound() != null) {
            user.playSound(reaction.sound());
        }

        ItemStack itemStack = createReactionHead(reaction);
        itemDisplay.setItemStack(itemStack);
        itemDisplay.setPersistent(false);

        Transformation transformation = itemDisplay.getTransformation();
        transformation.getScale().set(0);
        transformation.getTranslation().set(0, offsetY, 0);
        itemDisplay.setTransformation(transformation);

        player.addPassenger(itemDisplay);

        playAppearingAnimation(itemDisplay, DEFAULT_SCALE);

        return itemDisplay;
    }

    private void spawnParticleEffect(Reaction reaction, ItemDisplay itemDisplay) {
        String rawParticle = reaction.particle();
        if (rawParticle == null) {
            return;
        }

        String particleName = rawParticle
                .toUpperCase(Locale.ROOT)
                .replace(".", "_")
                .replace("MINECRAFT:", "");

        try {
            Particle particle = Particle.valueOf(particleName);
            itemDisplay.getWorld().spawnParticle(
                    particle,
                    itemDisplay.getLocation().add(0, 2, 0),
                    3,
                    0.2,
                    0.2,
                    0.2
            );
        } catch (IllegalArgumentException exception) {
            plugin.getLogger().warning("Unknown particle '" + rawParticle + "' for reaction.");
        }
    }

    private ItemStack createReactionHead(Reaction reaction) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

        if (skullMeta == null) {
            return itemStack;
        }

        skullMeta.setPlayerProfile(getProfile(reaction.texture()));
        itemStack.setItemMeta(skullMeta);

        return itemStack;
    }

    private void playAppearingAnimation(ItemDisplay itemDisplay, float targetScale) {
        itemDisplay.getScheduler().runAtFixedRate(plugin, scheduledTask -> {
            itemDisplay.setInterpolationDuration(3);
            itemDisplay.setInterpolationDelay(0);

            Transformation grow = itemDisplay.getTransformation();
            grow.getScale().set(targetScale + 0.15f);
            itemDisplay.setTransformation(grow);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                itemDisplay.setInterpolationDuration(6);
                Transformation settle = itemDisplay.getTransformation();
                settle.getScale().set(targetScale);
                itemDisplay.setTransformation(settle);
            }, 4L);

            scheduledTask.cancel();
        }, null, 1L, 1L);
    }

    private void playDisappearingAnimation(Player player, ItemDisplay itemDisplay) {
        itemDisplay.getWorld().playSound(
                itemDisplay.getLocation(),
                Sound.ENTITY_ITEM_PICKUP,
                0.25F,
                0.6F
        );

        itemDisplay.setInterpolationDuration(2);
        itemDisplay.setInterpolationDelay(0);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            itemDisplay.setInterpolationDuration(4);
            itemDisplay.setInterpolationDelay(0);

            Transformation disappear = itemDisplay.getTransformation();
            disappear.getScale().set(0f);
            disappear.getTranslation().set(0, -0.1f, 0);
            itemDisplay.setTransformation(disappear);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                itemDisplay.remove();
                playerReactions.remove(player.getUniqueId());
            }, 10L);
        }, 2L);
    }

    private void scheduleItemDisplayUpdate(Player player, ItemDisplay itemDisplay) {
        int durationInSeconds = Social.get().getConfig().getReactions().getDurationInSeconds();
        int updateIntervalInTicks = Social.get().getConfig().getReactions().getUpdateIntervalInTicks();

        AtomicInteger remainingTicks = new AtomicInteger(durationInSeconds * 20);

        itemDisplay.getScheduler().runAtFixedRate(plugin, scheduledTask -> {
            if (!player.isOnline()) {
                cleanupItemDisplay(player, itemDisplay);
                scheduledTask.cancel();
                return;
            }

            if (!itemDisplay.isValid() || itemDisplay.isDead()) {
                cleanupItemDisplay(player, itemDisplay);
                scheduledTask.cancel();
                return;
            }

            itemDisplay.setRotation(player.getLocation().getYaw() - 180, 0);

            if (remainingTicks.addAndGet(-updateIntervalInTicks) <= 0) {
                playDisappearingAnimation(player, itemDisplay);
                scheduledTask.cancel();
            }
        }, null, 1L, updateIntervalInTicks);
    }

    private void cleanupItemDisplay(Player player, ItemDisplay itemDisplay) {
        if (itemDisplay != null) {
            itemDisplay.remove();
        }

        playerReactions.remove(player.getUniqueId());
    }

    private static PlayerProfile getProfile(String textureUrl) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = URI.create(textureUrl).toURL();
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);
        return profile;
    }

}
