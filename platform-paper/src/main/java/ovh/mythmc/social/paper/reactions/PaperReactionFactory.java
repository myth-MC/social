package ovh.mythmc.social.paper.reactions;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Transformation;

import com.destroystokyo.paper.profile.PlayerProfile;

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
public final class PaperReactionFactory extends ReactionFactory {

    private final JavaPlugin plugin;

    private final HashMap<UUID, ItemDisplay> playerReaction = new HashMap<>();
    private final String itemDisplayMetadataKey = "socialReaction";

    private final float scale = 0.7f;

    @Override
    public void displayReaction(SocialUser player, Reaction emoji) {
        if (player.getPlayer() == null || 
            player.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY) || 
            player.getPlayer().getGameMode() == GameMode.SPECTATOR)
            return;

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
        skullMeta.setPlayerProfile(getProfile(reaction.texture()));
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

        itemDisplay.getScheduler().runAtFixedRate(plugin, scheduledTask -> {
            float currentScale = transformation.getScale().get(1);

            if (currentScale + growth >= targetScale) {
                transformation.getScale().set(targetScale);
                scheduledTask.cancel();
                return;
            }

            transformation.getScale().set(currentScale + growth);
            itemDisplay.setTransformation(transformation);  
        }, null, 3l, 1l);

    }

    private void playDisappearingAnimation(Player player, ItemDisplay itemDisplay) {
        itemDisplay.getWorld().playSound(itemDisplay.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.25F, 0.6F);

        itemDisplay.getScheduler().runAtFixedRate(plugin, scheduledTask -> {
            Transformation itemTransformation = itemDisplay.getTransformation();

            if (itemTransformation.getTranslation().y() < 0) {
                itemDisplay.remove();
                playerReaction.remove(player.getUniqueId());
                scheduledTask.cancel();
            }

            float currentScale = itemTransformation.getScale().get(1);
            if (currentScale > 0) {
                itemTransformation.getScale().set(currentScale - 0.2F);
            } else {
                itemTransformation.getScale().set(0);
            }

            itemTransformation.getTranslation().set(0, itemTransformation.getScale().y() - 0.1, 0);
            itemDisplay.setTransformation(itemTransformation);
        }, null, 1, 1);
    }

    private void scheduleItemDisplayUpdate(Player player, ItemDisplay itemDisplay) {
        int durationInSeconds = Social.get().getConfig().getSettings().getReactions().getDurationInSeconds();
        int updateIntervalInTicks = Social.get().getConfig().getSettings().getReactions().getUpdateIntervalInTicks();

        //int remainingTicks = durationInSeconds * 20;
        var remainingTicks = new Object() { int ticks = durationInSeconds * 20; };

        itemDisplay.getScheduler().runAtFixedRate(plugin, scheduledTask -> {
            if (itemDisplay != null && player.isOnline()) {
                if (itemDisplay.isValid() && !itemDisplay.isDead()) {
                    itemDisplay.setRotation(player.getLocation().getYaw() - 180, 0);
                }

                remainingTicks.ticks -= updateIntervalInTicks;
                if (remainingTicks.ticks <= 0) {
                    playDisappearingAnimation(player, itemDisplay);
                    scheduledTask.cancel();
                }
            } else {
                if (itemDisplay != null)
                    itemDisplay.remove();
                scheduledTask.cancel();
                
                playerReaction.remove(player.getUniqueId());
            }
        }, null, 1, updateIntervalInTicks);
    }

    private PlayerProfile getProfile(String textureUrl) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
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
