package ovh.mythmc.social.api.channels.handlers;

import org.jetbrains.annotations.ApiStatus.Experimental;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@Experimental
@UtilityClass
public class ChannelHandlers {

    @Getter 
    private ActionBarChannelHandler actionBarChannelHandler = new ActionBarChannelHandler();

    @Getter 
    private ChatChannelHandler chatChannelHandler = new ChatChannelHandler();
    
}
