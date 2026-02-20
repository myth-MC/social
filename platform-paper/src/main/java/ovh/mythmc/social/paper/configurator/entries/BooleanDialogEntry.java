package ovh.mythmc.social.paper.configurator.entries;

import org.jetbrains.annotations.NotNull;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.paper.configurator.ConfigurableObjectField;
import ovh.mythmc.social.paper.configurator.DialogEntryWrapper;

public class BooleanDialogEntry implements DialogEntryWrapper<Boolean> {

    @Override
    public <O> @NotNull DialogInput get(@NotNull O object,
            @NotNull ConfigurableObjectField<O, Boolean> field) {
        return DialogInput.bool(
                field.identifier(),
                Component.text(field.identifier()),
                field.getter().apply(object),
                null, null);
    }

    @Override
    public <O> @NotNull Boolean getEntryFromView(@NotNull ConfigurableObjectField<O, Boolean> field,
            @NotNull DialogResponseView view) {
        return view.getBoolean(field.identifier());
    }

}
