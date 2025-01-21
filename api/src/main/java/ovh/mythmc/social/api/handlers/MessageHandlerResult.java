package ovh.mythmc.social.api.handlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import ovh.mythmc.social.api.context.SocialHandlerContext;
import ovh.mythmc.social.api.users.SocialUser;

@NonExtendable
public interface MessageHandlerResult {

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    final class Valid implements MessageHandlerResult {

        private final @NotNull SocialUser user;

        private final @NotNull SocialHandlerContext context;

    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    final class Invalid implements MessageHandlerResult {

        private final @NotNull SocialUser user;

        private final @NotNull SocialHandlerContext context;

        private final @NotNull String message;

    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    final class Ignored implements MessageHandlerResult {

        private final @NotNull SocialUser user;

        private final @NotNull SocialHandlerContext context;

    }
    
}
