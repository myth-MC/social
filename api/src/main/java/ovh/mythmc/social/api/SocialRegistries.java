package ovh.mythmc.social.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.mythmc.social.api.announcements.Announcement;
import ovh.mythmc.social.api.callback.channel.SocialChannelCreate;
import ovh.mythmc.social.api.callback.channel.SocialChannelCreateCallback;
import ovh.mythmc.social.api.callback.channel.SocialChannelDelete;
import ovh.mythmc.social.api.callback.channel.SocialChannelDeleteCallback;
import ovh.mythmc.social.api.callback.group.SocialGroupDisband;
import ovh.mythmc.social.api.callback.group.SocialGroupDisbandCallback;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.chat.channel.GroupChatChannel;
import ovh.mythmc.social.api.emoji.Emoji;
import ovh.mythmc.social.api.reaction.Reaction;
import ovh.mythmc.social.api.util.registry.*;

public final class SocialRegistries {

    static final SocialRegistries INSTANCE = new SocialRegistries();

    private final NamespacedRegistry<Announcement> announcements = Registry.namespaced(Announcement.class);

    private final Channels channels = new Channels();

    private final NamespacedRegistry<Emoji> emojis = Registry.namespaced(Emoji.class);

    private final NamespacedRegistry<Reaction> reactions = Registry.namespaced(Reaction.class);

    private SocialRegistries() { }

    public NamespacedRegistry<Announcement> announcements() {
        return this.announcements;
    }

    public Channels channels() {
        return this.channels;
    }

    public NamespacedRegistry<Emoji> emojis() {
        return this.emojis;
    }

    public NamespacedRegistry<Reaction> reactions() {
        return this.reactions;
    }

    public static final class Channels extends AbstractRegistry.Identified<ChatChannel> {

        private Channels() {
            super();
        }

        @Override
        public void register(@NotNull IdentifiedRegistryKey registryKey, @NotNull ChatChannel value) {
            if (Social.get().getConfig().getGeneral().isDebug())
                Social.get().getLogger().info("Registered channel '" + value.name() + "'");

            final var callback = new SocialChannelCreate(value);
            SocialChannelCreateCallback.INSTANCE.invoke(callback);

            super.register(registryKey, value);
        }

        @Override
        public @Nullable ChatChannel unregister(@NotNull IdentifiedRegistryKey registryKey) {
            final ChatChannel channel = super.unregister(registryKey);
            if (channel != null) {
                if (Social.get().getConfig().getGeneral().isDebug())
                    Social.get().getLogger().info("Unregistered channel '" + channel.name() + "'");

                if (channel instanceof GroupChatChannel groupChatChannel) {
                    final var callback = new SocialGroupDisband(groupChatChannel);
                    SocialGroupDisbandCallback.INSTANCE.invoke(callback);
                } else {
                    final var callback = new SocialChannelDelete(channel);
                    SocialChannelDeleteCallback.INSTANCE.invoke(callback);
                }
            }

            return channel;
        }

    }

}
