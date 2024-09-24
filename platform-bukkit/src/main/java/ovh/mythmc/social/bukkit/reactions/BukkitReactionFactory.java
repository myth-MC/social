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
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.players.SocialPlayer;
import ovh.mythmc.social.api.reactions.Reaction;
import ovh.mythmc.social.api.reactions.ReactionFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public final class BukkitReactionFactory extends ReactionFactory {

    private final JavaPlugin plugin;

    private final HashMap<UUID, ItemDisplay> playerReaction = new HashMap<>();
    private final String itemDisplayMetadataKey = "socialReaction";

    public BukkitReactionFactory(final @NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        ReactionFactory.set(this);
    }

    @Override
    public void displayReaction(SocialPlayer player, Reaction emoji) {
        if (player.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY) || player.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        ItemDisplay itemDisplay = playerReaction.get(player.getUuid());
        if (itemDisplay != null)
            itemDisplay.remove();

        itemDisplay = spawnItemDisplay(player.getPlayer(), emoji);
        playerReaction.put(player.getUuid(), itemDisplay);

        scheduleItemDisplayUpdate(player.getPlayer(), itemDisplay);
    }

    private ItemDisplay spawnItemDisplay(Player player, Reaction reaction) {
        ItemDisplay itemDisplay = (ItemDisplay) player.getWorld().spawnEntity(
                player.getLocation().add(0, 2.8, 0),
                EntityType.ITEM_DISPLAY
        );

        itemDisplay.getWorld().playSound(itemDisplay.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.25F, 1.7F);
        if (reaction.sound() != null)
            itemDisplay.getWorld().playSound(itemDisplay.getLocation(), reaction.sound(), 0.75F, 1.5F);

        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwnerProfile(getProfile(reaction.texture()));
        itemStack.setItemMeta(skullMeta);

        itemDisplay.setMetadata(itemDisplayMetadataKey, new FixedMetadataValue(plugin, true));
        itemDisplay.setItemStack(itemStack);

        Transformation transformation = itemDisplay.getTransformation();
        transformation.getScale().set(0);

        itemDisplay.setTransformation(transformation);
        playAnimation(itemDisplay, 0.65F);

        return itemDisplay;
    }

    private void playAnimation(ItemDisplay itemDisplay, float targetScale) {
        Transformation transformation = itemDisplay.getTransformation();

        new BukkitRunnable() {
            @Override
            public void run() {
                float currentScale = transformation.getScale().get(1);

                if (currentScale >= targetScale) {
                    cancel();
                    return;
                }

                transformation.getScale().set(currentScale + 0.15F);
                itemDisplay.setTransformation(transformation);
            }
        }.runTaskTimerAsynchronously(plugin, 0, 1);

    }

    private void playDisappearingAnimation(Player player, ItemDisplay itemDisplay) {
        itemDisplay.getWorld().playSound(itemDisplay.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.25F, 0.6F);

        new BukkitRunnable() {
            @Override
            public void run() {
                double playerY = player.getLocation().getY();
                Location itemDisplayLocation = itemDisplay.getLocation();

                if (itemDisplayLocation.getY() < playerY) {
                    itemDisplay.remove();
                    cancel();
                }

                Transformation itemTransformation = itemDisplay.getTransformation();
                float currentScale = itemTransformation.getScale().get(1);
                if (currentScale > 0) {
                    itemTransformation.getScale().set(currentScale - 0.3F);
                } else {
                    itemTransformation.getScale().set(0);
                }

                itemDisplay.setTransformation(itemTransformation);

                itemDisplayLocation.setY(itemDisplayLocation.getY() - 0.25);
                itemDisplay.teleport(itemDisplayLocation);
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void scheduleItemDisplayUpdate(Player player, ItemDisplay itemDisplay) {
        int durationInSeconds = 3;
        int updateIntervalInTicks = 1;

        new BukkitRunnable() {
            int remainingTicks = durationInSeconds * 20;

            public void run() {
                if (itemDisplay != null && player.isOnline()) {
                    if (!itemDisplay.isDead()) {
                        Location location = player.getLocation().add(0, 2.8, 0);
                        location.setPitch(0);
                        location.setYaw(location.getYaw() - 180);

                        itemDisplay.teleport(location);
                    }

                    remainingTicks -= updateIntervalInTicks;
                    if (remainingTicks <= 0) {
                        playDisappearingAnimation(player, itemDisplay);
                        this.cancel();
                    }
                } else {
                    itemDisplay.remove();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, updateIntervalInTicks);
    }

    private static PlayerProfile getProfile(String texture) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID()); // Get a new player profile
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL("https://textures.minecraft.net/texture/" + texture); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
    }

}
