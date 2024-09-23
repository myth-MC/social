package ovh.mythmc.social.api;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class SocialSupplier {

    private Social social;

    public void set(final @NotNull Social s) {
        if (social != null)
            throw new AlreadyInitializedException();

        social = Objects.requireNonNull(s);
    }

    public @NotNull Social get() { return social; }

}
