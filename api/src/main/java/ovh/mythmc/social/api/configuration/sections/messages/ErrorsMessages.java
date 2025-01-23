package ovh.mythmc.social.api.configuration.sections.messages;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class ErrorsMessages {

    private String cannotBeRunFromConsole = "$(error_prefix) This command cannot be run from console.";

    private String featureNotAvailable = "$(error_prefix) <red>This feature isn't available in this server.</red>";

    private String unexpectedError = "$(error_prefix) <red>An unexpected error has occurred.</red>";

    private String invalidCommand = "$(error_prefix) <red>Invalid command: <gray>%s</gray>.</red>";

    private String invalidArgument = "$(error_prefix) <red>Invalid argument <gray>%s</gray> for type <gray>%s</gray>.</red>";

    private String notEnoughPermission = "$(error_prefix) <red>You do not have enough permission to do this.</red>";

    private String notEnoughArguments = "$(error_prefix) <red>Not enough arguments.</red>";

    private String tooManyArguments = "$(error_prefix) <red>Too many arguments.</red>";

    private String nicknameAlreadyInUse = "$(error_prefix) <red>This nickname belongs to another player.</red>";

    private String nicknameTooLong = "$(error_prefix) <red>Nicknames cannot exceed 16 characters.</red>";

    private String typingTooFast = "$(error_prefix) <red>You are typing too fast!</red>";

    private String playerNotFound = "$(error_prefix) <red>Unknown player.</red>";

    private String chooseAnotherPlayer = "$(error_prefix) <red>Choose another player.</red>";

    private String channelDoesNotExist = "$(error_prefix) <red>That channel does not exist.</red>";

    private String unknownReaction = "$(error_prefix) <red>Unknown reaction.</red>";

    private String alreadyBelongsToAGroup = "$(error_prefix) <red>You already belong to a group.</red>";

    private String doesNotBelongToAGroup = "$(error_prefix) <red>You don't belong to any group.</red>";

    private String groupDoesNotExist = "$(error_prefix) <red>That channel does not exist.</red>";

    private String groupIsFull = "$(error_prefix) <red>This group is full.</red>";

    private String groupAliasTooLong = "$(error_prefix) <red>Aliases cannot exceed 16 characters.</red>";

    private String userIsAlreadyIgnored = "$(error_prefix) <red>That user is already ignored.</red>";

    private String userIsNotIgnored = "$(error_prefix) <red>You are not ignoring that user.</red>";

    private String cannotIgnoreYourself = "$(error_prefix) <red>You cannot ignore yourself.</red>";

    private String userHasIgnoredYou = "$(error_prefix) <red>Your message has not been delivered since the recipient is ignoring you.</red>";

    private String userIsAlreadyMuted = "$(error_prefix) <red>That user is already muted.</red>";

    private String cannotMuteYourself = "$(error_prefix) <red>You cannot mute yourself.</red>";

    private String userExcemptFromMute = "$(error_prefix) <red>That user is excempt from being muted.</red> <gray>(social.mute.excempt)</gray>";    

    private String userIsNotMuted = "$(error_prefix) <red>That user is not muted.</red>";

    private String cannotSendMessageWhileMuted = "$(error_prefix) <red>You cannot send messages in <yellow>$(channel)</yellow> since you have been muted.</red>";

}
