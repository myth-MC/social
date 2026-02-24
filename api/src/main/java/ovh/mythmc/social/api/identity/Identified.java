package ovh.mythmc.social.api.identity;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public interface Identified {

    @NotNull
    UUID uuid();

    @NotNull
    String username();

}
