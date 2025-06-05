package ovh.mythmc.social.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

class MutableCallbackImpl<T> extends MutableImpl<T> {

    final BiConsumer<T, T> callback;

    MutableCallbackImpl(T object, @NotNull BiConsumer<T, T> callback) {
        super(object);
        this.callback = callback;
    }

    @Override
    public void set(T object) {
        this.callback.accept(this.object, object);
        super.set(object);
    }

}
