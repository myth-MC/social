package ovh.mythmc.social.common.command.exception;

import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.parsing.ParserException;
import org.jetbrains.annotations.NotNull;
import ovh.mythmc.social.common.command.SocialCaptionKeys;
import ovh.mythmc.social.common.command.parser.RegisteredMessageParser;

public final class UnknownMessageException extends ParserException {

    private final String input;

    public UnknownMessageException(@NotNull String input, @NotNull RegisteredMessageParser parser, @NotNull CommandContext<?> context) {
        super(
            parser.getClass(), 
            context, 
            SocialCaptionKeys.ARGUMENT_PARSE_FAILURE_MESSAGE,
            CaptionVariable.of("input", input)
        );

        this.input = input;
    }

    public String input() {
        return input;
    }
    
}
