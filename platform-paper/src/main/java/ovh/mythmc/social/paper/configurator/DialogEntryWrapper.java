package ovh.mythmc.social.paper.configurator;

import org.jetbrains.annotations.NotNull;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;

public interface DialogEntryWrapper<F> {

    @NotNull
    <O> DialogInput get(@NotNull O object,
            @NotNull ConfigurableObjectField<O, F> field);

    @NotNull
    <O> F getEntryFromView(@NotNull ConfigurableObjectField<O, F> field,
            @NotNull DialogResponseView view);

}
