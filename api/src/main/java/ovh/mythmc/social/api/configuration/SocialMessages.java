package ovh.mythmc.social.api.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.api.chat.channel.ChatChannel;
import ovh.mythmc.social.api.configuration.section.messages.CommandsMessages;
import ovh.mythmc.social.api.configuration.section.messages.ErrorsMessages;
import ovh.mythmc.social.api.configuration.section.messages.InfoMessages;

/**
 * Configuration for social system messages.
 */
@Configuration
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

    public @NotNull String getErrorPrefix() {
        return errorPrefix;
    }

    public @NotNull String getWarningPrefix() {
        return warningPrefix;
    }

    public @NotNull String getSuccessPrefix() {
        return successPrefix;
    }

    public @NotNull String getInfoPrefix() {
        return infoPrefix;
    }

    public boolean isUseActionBar() {
        return useActionBar;
    }

    public @NotNull ErrorsMessages getErrors() {
        return errors;
    }

    public @NotNull InfoMessages getInfo() {
        return info;
    }

    public @NotNull CommandsMessages getCommands() {
        return commands;
    }

    public ChatChannel.ChannelType getChannelType() {
        if (useActionBar)
            return ChatChannel.ChannelType.ACTION_BAR;

        return ChatChannel.ChannelType.CHAT;
    }

}

