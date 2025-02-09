package ovh.mythmc.social.api.text.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

import lombok.Builder;
import lombok.Singular;
import net.kyori.adventure.text.Component;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.parsers.SocialContextualParser;

@Experimental
@Builder
public class SocialParserGroup implements SocialContextualParser {

    @Singular("parser") private final List<SocialContextualParser> content = new ArrayList<>();

    public List<SocialContextualParser> get() {
        return List.copyOf(content);
    }

    public void add(final @NotNull SocialContextualParser... parsers) {
        Arrays.stream(parsers).forEach(content::add);
    }

    public void remove(final @NotNull SocialContextualParser... parsers) {
        Arrays.stream(parsers).forEach(content::remove);
    }

    public void removeAll() {
        get().forEach(content::remove);
    }

    @Experimental
    public Component requestToGroup(@NotNull SocialContextualParser requester, @NotNull SocialParserContext context) {
        return SocialContextualParser.request(context, content.stream().filter(parser -> !parser.getClass().equals(requester.getClass())).toList());
    }

    @Override
    public Component parse(SocialParserContext context) {
        return SocialContextualParser.request(context.withGroup(this), content);
    }

}
