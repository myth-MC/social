package ovh.mythmc.social.paper.configurator;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

public record ConfigurableObjectField<O, F>(
                @NotNull String identifier,
                @NotNull Class<F> fieldType,
                @NotNull DialogEntryWrapper<F> wrapper,
                @NotNull Function<O, F> getter,
                @NotNull BiConsumer<O, F> setter) {

}
