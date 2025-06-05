package ovh.mythmc.social.api.chat.channel;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ovh.mythmc.social.api.Social;
import ovh.mythmc.social.api.callback.group.*;
import ovh.mythmc.social.api.user.AbstractSocialUser;
import ovh.mythmc.social.api.util.Mutable;
import ovh.mythmc.social.api.util.registry.RegistryKey;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public final class GroupChatChannel extends SimpleChatChannel {

    public static void create(final @NotNull UUID leaderUuid, final @Nullable String alias) {
        final int code = (int) Math.floor(100000 + Math.random() * 900000);
        final var chatChannel = new GroupChatChannel(leaderUuid, alias, code);
        chatChannel.addMember(leaderUuid);

        final var callback = new SocialGroupCreate(chatChannel);
        SocialGroupCreateCallback.INSTANCE.invoke(callback);

        final var registryKey = RegistryKey.identified("g-" + chatChannel.code);
        Social.registries().channels().register(registryKey, chatChannel);
    }

    private final Mutable<UUID> leaderUuid;

    private final int code;

    private GroupChatChannel(final @NotNull UUID leaderUuid, final @Nullable String alias, final int code) {
        super(
            "G-" + code,
            alias,
            TextColor.fromHexString(Social.get().getConfig().getChat().getGroups().getColor()),
            Collections.emptyList(),
            Component.text(Social.get().getConfig().getChat().getGroups().getIcon()),
            Social.get().getConfig().getChat().getGroups().isShowHoverText(),
            SimpleChatChannel.getHoverTextAsComponent(Social.get().getConfig().getChat().getGroups().getHoverText()),
            TextColor.fromHexString(Social.get().getConfig().getChat().getGroups().getNicknameColor()),
            Social.get().getConfig().getChat().getGroups().getTextDivider(),
            TextColor.fromHexString(Social.get().getConfig().getChat().getGroups().getTextColor()),
            null,
            false
        );

        this.leaderUuid = Mutable.callback(leaderUuid, (value, newValue) -> {
            final AbstractSocialUser previousLeader = Social.get().getUserService().getByUuid(value).orElse(null);
            final AbstractSocialUser leader = Social.get().getUserService().getByUuid(newValue).orElse(null);

            final var socialGroupLeaderChange = new SocialGroupLeaderChange(this, previousLeader, leader);
            SocialGroupLeaderChangeCallback.INSTANCE.invoke(socialGroupLeaderChange);
        });
        this.code = code;
    }

    @Override
    public boolean addMember(@NotNull UUID uuid) {
        if (memberUuids.size() >= Social.get().getConfig().getChat().getGroups().getPlayerLimit())
            return false;

        super.addMember(uuid);

        final var callback = new SocialGroupJoin(this, leader());
        SocialGroupJoinCallback.INSTANCE.invoke(callback);

        return true;
    }

    public boolean removeMember(@NotNull UUID uuid) {
        final var callback = new SocialGroupLeave(this, leader());
        SocialGroupLeaveCallback.INSTANCE.invoke(callback);

        return super.removeMember(uuid);
    }

    public Mutable<UUID> leaderUuid() {
        return this.leaderUuid;
    }

    public int code() {
        return this.code;
    }

    public void leader(@NotNull AbstractSocialUser user) {
        this.leaderUuid.set(user.uuid());
    }

    public @NotNull AbstractSocialUser leader() {
        final var optionalUser = Social.get().getUserService().getByUuid(leaderUuid.get());
        Objects.requireNonNull(optionalUser.orElse(null), "leader cannot be null");

        return optionalUser.get();
    }

}
