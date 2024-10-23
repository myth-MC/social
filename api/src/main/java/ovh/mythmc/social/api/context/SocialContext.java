package ovh.mythmc.social.api.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import net.kyori.adventure.text.Component;

@SuperBuilder
@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
public abstract class SocialContext {

    public enum MessageType {
        SYSTEM,
        PLAYER
    }

    @Builder.Default
    private MessageType messageType = MessageType.PLAYER;

    private Component message;

    private String rawMessage;

}
