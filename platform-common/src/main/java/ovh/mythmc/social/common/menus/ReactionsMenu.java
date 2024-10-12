package ovh.mythmc.social.common.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.reactions.Reaction;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

public class ReactionsMenu {

    private PaginatedGui getBase() {
        PaginatedGui gui = Gui.paginated()
                .title(text("Reactions"))
                .rows(6)
                .create();

        GuiItem blackDecoration = ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(empty()).asGuiItem();
        GuiItem whiteDecoration = ItemBuilder.from(Material.WHITE_STAINED_GLASS_PANE).name(empty()).asGuiItem();

        gui.setItem(List.of(0, 1, 9, 10), blackDecoration);
        gui.setItem(List.of(2, 11), whiteDecoration);
        gui.setItem(List.of(6, 15), whiteDecoration);
        gui.setItem(List.of(7, 8, 16, 17), blackDecoration);
        gui.setItem(List.of(18, 19, 20, 21, 22, 23, 24, 25, 26), whiteDecoration);
        gui.setItem(List.of(27, 35), whiteDecoration);
        gui.setItem(List.of(36, 37, 38, 39, 40, 41, 42, 43, 44), whiteDecoration);
        gui.setItem(List.of(45, 46), blackDecoration);
        gui.setItem(List.of(47, 49, 51), whiteDecoration);
        gui.setItem(List.of(52, 53), blackDecoration);

        gui.setItem(6, 4, ItemBuilder.from(Material.ARROW).name(text("Previous")).asGuiItem(event -> gui.previous()));
        gui.setItem(6, 6, ItemBuilder.from(Material.ARROW).name(text("Next")).asGuiItem(event -> gui.next()));

        gui.disableAllInteractions();

        return gui;
    }

    /*
    private void populateCategories(PaginatedGui gui) {
        GuiItem deniedItem = ItemBuilder.from(Material.BARRIER).name(empty()).asGuiItem();

        gui.setItem(List.of(10, 11, 12, 13, 14, 15, 16), deniedItem);
        int i = 0;
        for (String categoryName : Social.get().getReactionManager().getCategories()) {
            if (i >= 7) break;
            GuiItem categoryItem = ItemBuilder.from(Material.PAPER)
                    .name(text(categoryName, NamedTextColor.GRAY))
                    .asGuiItem();

            gui.setItem(i + 10, categoryItem);
            i++;
        }
    }

     */

    private void populateRecentReactions(PaginatedGui gui) {
        GuiItem deniedItem = ItemBuilder.from(Material.BARRIER).name(empty()).asGuiItem();

        gui.setItem(List.of(3, 4, 5, 12, 13, 14), deniedItem);
    }

    private void populateReactions(PaginatedGui gui) {
        for (Reaction reaction : Social.get().getReactionManager().getByCategory("server")) {
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwnerProfile(getProfile(reaction.texture()));
            itemStack.setItemMeta(skullMeta);

            Component lore = text("Category: ", NamedTextColor.GRAY)
                    .append(text("SERVER", NamedTextColor.YELLOW))
                    .appendNewline()
                    .append(text("Click here to trigger this reaction", NamedTextColor.DARK_GRAY));

            GuiItem guiItem = ItemBuilder.from(itemStack)
                    .name(text(reaction.name(), NamedTextColor.YELLOW))
                    .lore(lore)
                    .asGuiItem(event -> {
                        Player player = (Player) event.getWhoClicked();
                        player.performCommand("social:reaction server " + reaction.name());
                        gui.close(player);
                    });

            gui.addItem(guiItem);
        }
    }

    public void openMenu(Player player) {
        PaginatedGui gui = getBase();
        populateRecentReactions(gui);
        populateReactions(gui);

        gui.open(player);
    }

    private PlayerProfile getProfile(String textureUrl) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID()); // Get a new player profile
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(textureUrl); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
    }

}
