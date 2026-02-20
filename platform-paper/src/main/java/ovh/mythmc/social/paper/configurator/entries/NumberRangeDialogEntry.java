package ovh.mythmc.social.paper.configurator.entries;

import org.jetbrains.annotations.NotNull;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.paper.configurator.ConfigurableObjectField;
import ovh.mythmc.social.paper.configurator.DialogEntryWrapper;

public class NumberRangeDialogEntry implements DialogEntryWrapper<Float> {

    private final float start;
    private final float end;
    private Float step = 1f;

    public NumberRangeDialogEntry(float start, float end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public <O> @NotNull DialogInput get(@NotNull O object,
            @NotNull ConfigurableObjectField<O, Float> field) {
        return DialogInput.numberRange(
                field.identifier(),
                Component.text(field.identifier()),
                start,
                end)
                .initial(field.getter().apply(object))
                .step(step)
                .build();
    }

    @Override
    public <O> @NotNull Float getEntryFromView(@NotNull ConfigurableObjectField<O, Float> field,
            @NotNull DialogResponseView view) {
        return view.getFloat(field.identifier());
    }

    public @NotNull NumberRangeDialogEntry step(@NotNull Float step) {
        this.step = step;
        return this;
    }

}
