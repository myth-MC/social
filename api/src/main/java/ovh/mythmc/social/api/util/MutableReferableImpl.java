package ovh.mythmc.social.api.util;

import java.util.function.Function;

class MutableReferableImpl<T, R> extends MutableImpl<T> {

    R reference;

    final Function<R, T> map;

    final Function<T, R> reverse;

    MutableReferableImpl(T object, Function<R, T> map, Function<T, R> reverse) {
        super(object);
        this.map = map;
        this.reverse = reverse;
    }

    @Override
    public T get() {
        return map.apply(reference);
    }

    @Override
    public void set(T object) {
        this.reference = reverse.apply(object);
    }

}
