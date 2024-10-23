package ovh.mythmc.social.api.context;

import org.bukkit.command.CommandSender;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import ovh.mythmc.social.api.chat.ChannelType;

@Data
@Setter(AccessLevel.PRIVATE)
@Accessors(fluent = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SocialCommandSenderContext extends SocialContext {
    
    private CommandSender commandSender;

    @Builder.Default
    private ChannelType channelType = ChannelType.CHAT;

}
