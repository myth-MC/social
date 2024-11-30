package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import ovh.mythmc.social.api.chat.ChannelType;
import ovh.mythmc.social.api.configuration.sections.messages.CommandsMessages;
import ovh.mythmc.social.api.configuration.sections.messages.ErrorsMessages;
import ovh.mythmc.social.api.configuration.sections.messages.InfoMessages;

@Configuration
@Getter
public class SocialMessages {

    @Comment("Message prefixes")
    private String errorPrefix = "<dark_gray>[<red>:raw_x:</red>]</dark_gray>";

    private String warningPrefix = "<dark_gray>[<yellow>:raw_warning:</yellow>]</dark_gray>";

    private String successPrefix = "<dark_gray>[<green>:raw_checkmark:</green>]</dark_gray>";

    private String infoPrefix = "<dark_gray>[<blue>:raw_comet:</blue>]</dark_gray>";

    @Comment({"", "Enabling this will make messages show in action bar instead of using chat channels"})
    private boolean useActionBar = false;

    @Comment({"", "General errors"})
    private ErrorsMessages errors = new ErrorsMessages();

    @Comment({"", "Info messages"})
    private InfoMessages info = new InfoMessages();

    @Comment({"", "Command messages"})
    private CommandsMessages commands = new CommandsMessages();

    public ChannelType getChannelType() {
        if (useActionBar)
            return ChannelType.ACTION_BAR;

        return ChannelType.CHAT;
    }

}
