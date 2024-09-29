package ovh.mythmc.social.api.reactions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class ReactionManager {

    public static final ReactionManager instance = new ReactionManager();

    private final Map<Reaction, String> reactionsMap = new HashMap<>();

    public void registerReaction(final @NotNull String category,
                                 final @NotNull Reaction... reactions) {

        Arrays.stream(reactions).forEach(reaction -> reactionsMap.put(reaction, category.toLowerCase()));
    }

    public void unregisterReaction(final @NotNull Reaction reaction) {
        reactionsMap.forEach((k, v) -> {
            if (k.equals(reaction))
                reactionsMap.remove(k);
        });
    }

    public Reaction get(final @NotNull String categoryName,
                        final @NotNull String reactionName) {
        List<Reaction> category = getByCategory(categoryName);
        for (Reaction reaction : category) {
            if (reaction.name().equalsIgnoreCase(reactionName))
                return reaction;
        }

        return null;
    }

    public Collection<String> getCategories() {
        return reactionsMap.values();
    }

    public List<Reaction> getByCategory(final @NotNull String category) {
        List<Reaction> reactionList = new ArrayList<>();
        reactionsMap.forEach((k, v) -> {
            if (v.equalsIgnoreCase(category))
                reactionList.add(k);
        });

        return reactionList;
    }

    public Reaction getByName(String name) {
        for (Reaction reaction : reactionsMap.keySet()) {
            if (reaction.name().equalsIgnoreCase(name))
                return reaction;
        }

        return null;
    }

}
