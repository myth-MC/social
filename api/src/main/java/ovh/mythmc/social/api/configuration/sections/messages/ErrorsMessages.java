package ovh.mythmc.social.api.configuration.sections.messages;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class ErrorsMessages {

    private String cannotBeRunFromConsole = "$error_prefix This command cannot be run from console.";

    private String featureNotAvailable = "$error_prefix <red>This feature isn't available in this server.</red>";

    private String unexpectedError = "$error_prefix <red>An unexpected error has occurred.</red>";

    private String invalidCommand = "$error_prefix <red>Invalid command.</red>";

    private String notEnoughPermission = "$error_prefix <red>You do not have enough permission to do this.</red>";

    private String notEnoughArguments = "$error_prefix <red>Not enough arguments.</red>";

    private String nicknameAlreadyInUse = "$error_prefix <red>This nickname belongs to another player.</red>";

    private String nicknameTooLong = "$error_prefix <red>Nicknames cannot exceed 16 characters.</red>";

    private String typingTooFast = "$error_prefix <red>You are typing too fast!</red>";

    private String playerNotFound = "$error_prefix <red>Unknown player.</red>";

    private String channelDoesNotExist = "$error_prefix <red>That channel does not exist.</red>";

    private String unknownReaction = "$error_prefix <red>Unknown reaction.</red>";

}
