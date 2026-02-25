package ovh.mythmc.social.common.command.exception;

import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.parsing.ParserException;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.common.command.SocialCaptionKeys;
import ovh.mythmc.social.common.command.parser.UserParser;

public final class SubjectIsSenderException extends ParserException {

    public SubjectIsSenderException(@NotNull UserParser parser, @NotNull CommandContext<?> context) {
        super(
            parser.getClass(),
            context,
            SocialCaptionKeys.ARGUMENT_PARSE_FAILURE_SUBJECT_IS_SENDER
        );
    }
    
}
