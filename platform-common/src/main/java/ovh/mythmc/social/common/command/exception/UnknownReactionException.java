package ovh.mythmc.social.common.command.exception;

import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.parsing.ParserException;
import org.jetbrains.annotations.NotNull;

import ovh.mythmc.social.common.command.SocialCaptionKeys;
import ovh.mythmc.social.common.command.parser.ReactionParser;

public final class UnknownReactionException extends ParserException {

    private final String category;

    private final String identifier;

    public UnknownReactionException(@NotNull String category, @NotNull String identifier, @NotNull ReactionParser parser, @NotNull CommandContext<?> context) {
        super(
            parser.getClass(),
            context,
            SocialCaptionKeys.ARGUMENT_PARSE_FAILURE_REACTION,
            CaptionVariable.of("category", category),
            CaptionVariable.of("identifier", identifier)
        );
        this.category = category;
        this.identifier = identifier;
    }

    public String category() { 
        return category;
    }

    public String identifier() {
        return identifier;
    }
    
}
