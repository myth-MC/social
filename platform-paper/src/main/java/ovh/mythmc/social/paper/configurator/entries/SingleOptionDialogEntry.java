package ovh.mythmc.social.paper.configurator.entries;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.paper.configurator.ConfigurableObjectField;
import ovh.mythmc.social.paper.configurator.DialogEntryWrapper;

public class SingleOptionDialogEntry implements DialogEntryWrapper<String> {

    private final List<OptionEntry> entries = new ArrayList<>();

    public SingleOptionDialogEntry(@NotNull List<OptionEntry> entries) {
        this.entries.addAll(entries);
    }

    @Override
    public <O> @NotNull DialogInput get(@NotNull O object,
            @NotNull ConfigurableObjectField<O, String> field) {
        return DialogInput.singleOption(
                field.identifier(),
                Component.text(field.identifier()),
                entries).build();
    }

    @Override
    public <O> @NotNull String getEntryFromView(@NotNull ConfigurableObjectField<O, String> field,
            @NotNull DialogResponseView view) {
        return view.getText(field.identifier());
    }

    public @NotNull SingleOptionDialogEntry addEntry(@NotNull OptionEntry entry) {
        this.entries.add(entry);
        return this;
    }

}
