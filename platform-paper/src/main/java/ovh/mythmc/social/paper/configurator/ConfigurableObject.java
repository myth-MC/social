package ovh.mythmc.social.paper.configurator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput.OptionEntry;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput.MultilineOptions;
import net.kyori.adventure.text.event.ClickCallback;
import ovh.mythmc.social.paper.configurator.entries.BooleanDialogEntry;
import ovh.mythmc.social.paper.configurator.entries.NumberRangeDialogEntry;
import ovh.mythmc.social.paper.configurator.entries.SingleOptionDialogEntry;
import ovh.mythmc.social.paper.configurator.entries.TextDialogEntry;

public class ConfigurableObject<O> {

    private final Map<String, ConfigurableObjectField<O, ?>> fieldMap = new HashMap<>();
    private final O object;
    private final List<Consumer<O>> actionCallbacks = new ArrayList<>();

    public ConfigurableObject(@NotNull O object) {
        this.object = object;
    }

    public <F> @NotNull ConfigurableObject<O> addField(
            @NotNull String identifier,
            @NotNull Class<F> type,
            @NotNull DialogEntryWrapper<F> wrapper,
            @NotNull Function<O, F> getter,
            @NotNull BiConsumer<O, F> setter) {

        ConfigurableObjectField<O, F> field = new ConfigurableObjectField<>(identifier, type, wrapper, getter, setter);
        this.fieldMap.put(identifier, field);
        return this;
    }

    public @NotNull ConfigurableObject<O> addBoolean(
            @NotNull String identifier,
            @NotNull Function<O, Boolean> getter,
            @NotNull BiConsumer<O, Boolean> setter) {
        return addField(identifier, Boolean.class, new BooleanDialogEntry(), getter, setter);
    }

    public @NotNull ConfigurableObject<O> addNumberRange(
            @NotNull String identifier,
            float start,
            float end,
            @NotNull Function<O, Float> getter,
            @NotNull BiConsumer<O, Float> setter) {
        return addField(identifier, Float.class, new NumberRangeDialogEntry(start, end), getter, setter);
    }

    public @NotNull ConfigurableObject<O> addNumberRange(
            @NotNull String identifier,
            float start,
            float end,
            float step,
            @NotNull Function<O, Float> getter,
            @NotNull BiConsumer<O, Float> setter) {
        return addField(identifier, Float.class, new NumberRangeDialogEntry(start, end).step(step), getter, setter);
    }

    public @NotNull ConfigurableObject<O> addSingleOption(
            @NotNull String identifier,
            @NotNull List<OptionEntry> entries,
            @NotNull Function<O, String> getter,
            @NotNull BiConsumer<O, String> setter) {
        return addField(identifier, String.class, new SingleOptionDialogEntry(entries), getter, setter);
    }

    public @NotNull ConfigurableObject<O> addTextInput(
            @NotNull String identifier,
            @NotNull Function<O, String> getter,
            @NotNull BiConsumer<O, String> setter) {
        return addField(identifier, String.class, new TextDialogEntry(), getter, setter);
    }

    public @NotNull ConfigurableObject<O> addTextInput(
            @NotNull String identifier,
            int maxLength,
            @Nullable MultilineOptions multilineOptions,
            @NotNull Function<O, String> getter,
            @NotNull BiConsumer<O, String> setter) {
        return addField(identifier, String.class,
                new TextDialogEntry().maxLength(maxLength).multiline(multilineOptions), getter, setter);
    }

    public @NotNull ConfigurableObject<O> addActionCallback(@NotNull Consumer<O> callback) {
        this.actionCallbacks.add(callback);
        return this;
    }

    /**
     * Helper that captures the wildcard {@code F} and lets the compiler unify
     * {@code field.wrapper()} (a {@code DialogEntryWrapper<F>}) with the
     * {@code ConfigurableObjectField<O, F>} passed to {@code getEntryFromView}.
     */
    private <F> F getFieldValue(@NotNull ConfigurableObjectField<O, F> field,
            @NotNull io.papermc.paper.dialog.DialogResponseView view) {
        return field.wrapper().getEntryFromView(field, view);
    }

    private <F> void applyField(@NotNull ConfigurableObjectField<O, F> field,
            @NotNull io.papermc.paper.dialog.DialogResponseView view) {
        field.setter().accept(object, getFieldValue(field, view));
    }

    public @NotNull DialogAction.CustomClickAction action() {
        return DialogAction.customClick((view, audience) -> {
            for (ConfigurableObjectField<O, ?> field : fieldMap.values()) {
                applyField(field, view);
            }

            actionCallbacks.forEach(callback -> callback.accept(object));
        }, ClickCallback.Options.builder().build());
    }

}
