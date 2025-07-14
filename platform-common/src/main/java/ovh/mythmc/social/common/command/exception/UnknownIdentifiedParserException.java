package ovh.mythmc.social.common.command.exception;

import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.parsing.ParserException;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.common.command.SocialCaptionKeys;
import ovh.mythmc.social.common.command.parser.IdentifiedParserParser;

public final class UnknownIdentifiedParserException extends ParserException {

    private final String input;

    public UnknownIdentifiedParserException(@NotNull String input, @NotNull IdentifiedParserParser<?> parser, @NotNull CommandContext<?> context) {
        super(
            parser.getClass(),
            context,
            SocialCaptionKeys.ARGUMENT_PARSE_FAILURE_IDENTIFIED_PARSER,
            CaptionVariable.of("input", input)
        );
        this.input = input;
    }

    public String input() {
        return input;
    }
    
}
