package ovh.mythmc.social.paper.configurator.entries;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput.MultilineOptions;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.paper.configurator.ConfigurableObjectField;
import ovh.mythmc.social.paper.configurator.DialogEntryWrapper;

public class TextDialogEntry implements DialogEntryWrapper<String> {

    private int maxLength = 128;
    private MultilineOptions multilineOptions;

    @Override
    public <O> @NotNull DialogInput get(@NotNull O object,
            @NotNull ConfigurableObjectField<O, String> field) {
        return DialogInput.text(field.identifier(), Component.text(field.identifier()))
                .initial(field.getter().apply(object))
                .maxLength(maxLength)
                .multiline(multilineOptions)
                .build();
    }

    @Override
    public <O> @NotNull String getEntryFromView(@NotNull ConfigurableObjectField<O, String> field,
            @NotNull DialogResponseView view) {
        return view.getText(field.identifier());
    }

    public @NotNull TextDialogEntry maxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public @NotNull TextDialogEntry multiline(@Nullable MultilineOptions multilineOptions) {
        this.multilineOptions = multilineOptions;
        return this;
    }

}
