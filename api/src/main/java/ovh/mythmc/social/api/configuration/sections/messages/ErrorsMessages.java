package ovh.mythmc.social.api.configuration.sections.messages;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class ErrorsMessages {

    private String cannotBeRunFromConsole = "This command cannot be run from console.";

    private String unexpectedError = "<red>An unexpected error has occurred.</red>";

    private String invalidCommand = "<red>Invalid command.</red>";

    private String notEnoughPermission = "<red>You do not have enough permission to do this.</red>";

    private String notEnoughArguments = "<red>Not enough arguments.</red>";

    private String nicknameAlreadyInUse = "<red>This nickname belongs to another player.</red>";

    private String nicknameTooLong = "<red>Nicknames cannot exceed 16 characters.</red>";

    private String playerNotFound = "<red>Unknown player.</red>";

    private String channelDoesNotExist = "<red>This channel does not exist.</red>";

}
