package ovh.mythmc.social.api.text.parser;

import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import ovh.mythmc.social.api.context.SocialParserContext;

/**
 * The {@code SocialContextualPlaceholder} class provides a base for parsing and replacing placeholders
 * in messages based on a specific context. This class is an abstract implementation of the 
 * {@link SocialIdentifiedParser} interface, allowing subclasses to define custom placeholder replacements 
 * that are context-sensitive.
 * 
 * <p>This class is primarily used to replace placeholders in a message with dynamically generated
 * content based on the social context. The placeholder format is expected to be of the form
 * {@code $(identifier)} where the identifier is a unique string identifying the placeholder.
 * 
 * @see SocialParserContext
 * @see SocialIdentifiedParser
 */
public abstract class SocialContextualPlaceholder implements SocialIdentifiedParser {

    /**
     * Retrieves the replacement component for the placeholder based on the given context.
     * 
     * @param  context the social context in which the placeholder is being parsed.
     * @return a {@link Component} representing the replacement content for the placeholder.
     */
    public abstract Component get(SocialParserContext context);

    /**
     * Checks if the placeholder supports the legacy format. The default implementation returns {@code false}.
     * This method is deprecated and should not be used in future versions.
     * 
     * @return {@code true} if legacy support is enabled, otherwise {@code false}.
     * @deprecated since version 0.4. This method is no longer necessary and will be removed in future versions.
     */
    @Deprecated(since = "0.4")
    public boolean legacySupport() { 
        return false; 
    }

    /**
     * Parses the context and replaces the placeholder with the appropriate content.
     * This method uses regular expressions to match placeholders in the format {@code $(identifier)} 
     * or the legacy format {@code $identifier}. If the legacy format is supported, both formats will be matched.
     * 
     * <p>It utilizes the context’s message to replace the placeholder, returning a new message with
     * the placeholder replaced by the component generated from the {@link #get(SocialParserContext)} method.
     * 
     * @param context the social context in which the placeholder is being parsed.
     * @return        a {@link Component} representing the modified message with the placeholder replaced.
     */
    @Override
    public Component parse(SocialParserContext context) {
        // Define the regular expression to match placeholders
        var regexString = "\\$\\((?i:" + identifier() + "\\))";
        // Check if legacy support is enabled, add legacy regex if true
        if (legacySupport())
            regexString = regexString + "|\\$(?i:" + identifier() + "\\b)";

        // Replace the matched placeholder in the message with the result of get(context)
        return context.message().replaceText(TextReplacementConfig.builder()
            .match(Pattern.compile(regexString))  // Pattern to match the placeholder
            .replacement(get(context.withMessage(Component.empty())))  // Get replacement content
            .build()
        );
    }
}