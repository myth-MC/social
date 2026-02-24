package ovh.mythmc.social.api.identity;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public record SimpleIdentity(@NotNull UUID uuid, @NotNull String username) implements Identified {

}
