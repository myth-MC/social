package ovh.mythmc.social.bukkit.reactions;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.reactions.ReactionFactory;
import ovh.mythmc.social.api.users.SocialUser;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public final class BukkitReactionFactory extends ReactionFactory {

    private final JavaPlugin plugin;

    private final HashMap<UUID, ItemDisplay> playerReaction = new HashMap<>();
    private final String itemDisplayMetadataKey = "socialReaction";

    private final float scale = 0.7f;

    @Override
    public void displayReaction(SocialUser player, Reaction emoji) {
        if (player.getPlayer() == null || player.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY) || player.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        ItemDisplay itemDisplay = playerReaction.get(player.getUuid());
        if (itemDisplay != null)
            return;

        itemDisplay = spawnItemDisplay(player.getPlayer(), emoji);
        playerReaction.put(player.getUuid(), itemDisplay);

        scheduleItemDisplayUpdate(player.getPlayer(), itemDisplay);
    }

    private ItemDisplay spawnItemDisplay(Player player, Reaction reaction) {
        Location location = player.getLocation();
        location.setPitch(0);
        location.setYaw(location.getYaw() - 180);

        double offsetY = Social.get().getConfig().getSettings().getReactions().getOffsetY();
        ItemDisplay itemDisplay = (ItemDisplay) player.getWorld().spawnEntity(
                location,
                EntityType.ITEM_DISPLAY
        );

        itemDisplay.getWorld().playSound(itemDisplay.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.25F, 1.7F);
        if (reaction.sound() != null)
            SocialAdventureProvider.get().player(player).playSound(reaction.sound());

        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwnerProfile(getProfile(reaction.texture()));
        itemStack.setItemMeta(skullMeta);

        itemDisplay.setMetadata(itemDisplayMetadataKey, new FixedMetadataValue(plugin, true));
        itemDisplay.setItemStack(itemStack);

        itemDisplay.setPersistent(false);

        Transformation transformation = itemDisplay.getTransformation();
        transformation.getScale().set(0);
        transformation.getTranslation().set(0, offsetY, 0);
        itemDisplay.setTransformation(transformation);

        player.addPassenger(itemDisplay);

        playAnimation(itemDisplay, scale);

        return itemDisplay;
    }

    private void playAnimation(ItemDisplay itemDisplay, float targetScale) {
        Transformation transformation = itemDisplay.getTransformation();
        float growth = 0.15f;

        new BukkitRunnable() {
            @Override
            public void run() {
                float currentScale = transformation.getScale().get(1);

                if (currentScale + growth >= targetScale) {
                    transformation.getScale().set(targetScale);
                    cancel();
                    return;
                }

                transformation.getScale().set(currentScale + growth);
                itemDisplay.setTransformation(transformation);
            }
        }.runTaskTimerAsynchronously(plugin, 3, 1);

    }

    private void playDisappearingAnimation(Player player, ItemDisplay itemDisplay) {
        itemDisplay.getWorld().playSound(itemDisplay.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.25F, 0.6F);

        new BukkitRunnable() {
            @Override
            public void run() {
                Transformation itemTransformation = itemDisplay.getTransformation();

                if (itemTransformation.getTranslation().y() < 0) {
                    itemDisplay.remove();
                    playerReaction.remove(player.getUniqueId());
                    cancel();
                }

                float currentScale = itemTransformation.getScale().get(1);
                if (currentScale > 0) {
                    itemTransformation.getScale().set(currentScale - 0.2F);
                } else {
                    itemTransformation.getScale().set(0);
                }

                itemTransformation.getTranslation().set(0, itemTransformation.getScale().y() - 0.1, 0);
                itemDisplay.setTransformation(itemTransformation);
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void scheduleItemDisplayUpdate(Player player, ItemDisplay itemDisplay) {
        int durationInSeconds = Social.get().getConfig().getSettings().getReactions().getDurationInSeconds();
        int updateIntervalInTicks = Social.get().getConfig().getSettings().getReactions().getUpdateIntervalInTicks();

        new BukkitRunnable() {
            int remainingTicks = durationInSeconds * 20;

            public void run() {
                if (itemDisplay != null && player.isOnline()) {
                    if (itemDisplay.isValid() && !itemDisplay.isDead()) {
                        itemDisplay.setRotation(player.getLocation().getYaw() - 180, 0);
                    }

                    remainingTicks -= updateIntervalInTicks;
                    if (remainingTicks <= 0) {
                        playDisappearingAnimation(player, itemDisplay);
                        this.cancel();
                    }
                } else {
                    if (itemDisplay != null)
                        itemDisplay.remove();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, updateIntervalInTicks);
    }

    private PlayerProfile getProfile(String textureUrl) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID()); // Get a new player profile
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = URI.create(textureUrl).toURL(); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
    }

}
