package ovh.mythmc.social.api.users;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import lombok.NonNull;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.sound.Sound.Emitter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import ovh.mythmc.social.api.adventure.SocialAdventureProvider;

@SuppressWarnings("deprecation")
public interface SocialUserAudienceWrapper extends Audience, Identified { // 4.18.0

    Player getPlayer();

    private Audience playerAudience() {
        return SocialAdventureProvider.get().player(getPlayer());
    }

    @Override
    default @NotNull Pointers pointers() {
        return playerAudience().pointers();
    }

    @Override
    default @NotNull Identity identity() {
        return Identity.identity(getPlayer().getUniqueId());
    }

    @Override
    default void sendMessage(final @NonNull Identity identity, final @NonNull Component message, final @NonNull MessageType type) {
        playerAudience().sendMessage(identity, message, type);
    }

    @Override
    default void sendActionBar(final @NonNull Component message) {
        playerAudience().sendActionBar(message);
    }

    @Override
    default void sendPlayerListHeaderAndFooter(final @NonNull Component header, final @NonNull Component footer) {
        playerAudience().sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    default <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        playerAudience().sendTitlePart(part, value);
    }

    @Override
    default void clearTitle() {
        playerAudience().clearTitle();
    }

    @Override
    default void showBossBar(@NotNull BossBar bar) {
        playerAudience().showBossBar(bar);
    }

    @Override
    default void hideBossBar(@NotNull BossBar bar) {
        playerAudience().hideBossBar(bar);
    }

    @Override
    default void playSound(@NotNull Sound sound) {
        playerAudience().playSound(sound);
    }

    @Override
    default void playSound(@NotNull Sound sound, @NotNull Emitter emitter) {
        playerAudience().playSound(sound, emitter);
    }

    @Override
    default void playSound(@NotNull Sound sound, double x, double y, double z) {
        playerAudience().playSound(sound, x, y, z);
    }

    @Override
    default void stopSound(@NotNull SoundStop stop) {
        playerAudience().stopSound(stop);
    }

    @Override
    default void openBook(@NotNull Book book) {
        playerAudience().openBook(book);
    }

    @Override
    default void sendResourcePacks(@NotNull ResourcePackRequest request) {
        playerAudience().sendResourcePacks(request);
    }

    @Override
    default void removeResourcePacks(@NotNull UUID id, @NotNull UUID @NotNull... others) {
        playerAudience().removeResourcePacks(id, others);
    }

    @Override
    default void clearResourcePacks() {
        playerAudience().clearResourcePacks();
    }
    
}
