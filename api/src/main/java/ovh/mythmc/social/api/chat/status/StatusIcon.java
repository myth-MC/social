package ovh.mythmc.social.api.chat.status;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.context.SocialRendererContext;

import java.util.function.Predicate;

public interface StatusIcon {

    @NotNull Component icon();

    @NotNull Predicate<SocialRendererContext> renderIf();

}
