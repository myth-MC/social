package ovh.mythmc.social.api.user;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import lombok.NonNull;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.chat.SignedMessage.Signature;
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

@SuppressWarnings("deprecation")
public interface SocialUserAudienceWrapper extends Audience, Identified { // 4.18.0

    SocialUser<? extends Object> user();

    private Audience audience() {
        return user().audience();
    }

    @Override
    default @NotNull Pointers pointers() {
        if (audience() == null)
            return Pointers.empty();

        return audience().pointers();
    }

    @Override
    default @NotNull Identity identity() {
        return Identity.identity(user().uuid());
    }

    @Override
    default void sendMessage(final @NonNull Identity identity, final @NonNull Component message, final @NonNull MessageType type) {
        audience().sendMessage(identity, message, type);
    }

    @Override
    default void deleteMessage(@NotNull Signature signature) {
        audience().deleteMessage(signature);
    }

    @Override
    default void deleteMessage(@NotNull SignedMessage signedMessage) {
        audience().deleteMessage(signedMessage);
    }

    @Override
    default void sendActionBar(final @NonNull Component message) {
        audience().sendActionBar(message);
    }

    @Override
    default void sendPlayerListHeaderAndFooter(final @NonNull Component header, final @NonNull Component footer) {
        audience().sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    default <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        audience().sendTitlePart(part, value);
    }

    @Override
    default void clearTitle() {
        audience().clearTitle();
    }

    @Override
    default void showBossBar(@NotNull BossBar bar) {
        audience().showBossBar(bar);
    }

    @Override
    default void hideBossBar(@NotNull BossBar bar) {
        audience().hideBossBar(bar);
    }

    @Override
    default void playSound(@NotNull Sound sound) {
        audience().playSound(sound);
    }

    @Override
    default void playSound(@NotNull Sound sound, @NotNull Emitter emitter) {
        audience().playSound(sound, emitter);
    }

    @Override
    default void playSound(@NotNull Sound sound, double x, double y, double z) {
        audience().playSound(sound, x, y, z);
    }

    @Override
    default void stopSound(@NotNull SoundStop stop) {
        audience().stopSound(stop);
    }

    @Override
    default void openBook(@NotNull Book book) {
        audience().openBook(book);
    }

    @Override
    default void sendResourcePacks(@NotNull ResourcePackRequest request) {
        audience().sendResourcePacks(request);
    }

    @Override
    default void removeResourcePacks(@NotNull UUID id, @NotNull UUID @NotNull... others) {
        audience().removeResourcePacks(id, others);
    }

    @Override
    default void clearResourcePacks() {
        audience().clearResourcePacks();
    }
    
}
