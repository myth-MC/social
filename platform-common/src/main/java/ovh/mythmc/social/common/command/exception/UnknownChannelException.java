package ovh.mythmc.social.common.command.exception;

import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.parsing.ParserException;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.common.command.SocialCaptionKeys;
import ovh.mythmc.social.common.command.parser.ChannelParser;

public final class UnknownChannelException extends ParserException {

    private final String input;

    public UnknownChannelException(@NotNull String input, @NotNull ChannelParser parser, @NotNull CommandContext<?> context) {
        super(
            parser.getClass(), 
            context, 
            SocialCaptionKeys.ARGUMENT_PARSE_FAILURE_CHANNEL,
            CaptionVariable.of("input", input)
        );

        this.input = input;
    }

    public String input() {
        return input;
    }
    
}
