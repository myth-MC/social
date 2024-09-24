package ovh.mythmc.social.api.reactions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ReactionManager {

    public static final ReactionManager instance = new ReactionManager();

    private final Collection<Reaction> reactions = new ArrayList<>();

    public boolean registerReaction(final @NotNull Reaction reaction) {
        return reactions.add(reaction);
    }

    public boolean unregisterReaction(final @NotNull Reaction reaction) {
        return reactions.remove(reaction);
    }

    public Reaction get(String name) {
        for (Reaction reaction : reactions) {
            if (reaction.name().equalsIgnoreCase(name))
                return reaction;
        }

        return null;
    }

}
