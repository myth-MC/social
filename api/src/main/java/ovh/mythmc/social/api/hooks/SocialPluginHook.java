package ovh.mythmc.social.api.hooks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class SocialPluginHook<T> {

    private final T storedClass;

    public abstract String identifier();

    public boolean isEnabled() {
        return get() != null;
    }

    public T get() {
        return storedClass;
    }

}
