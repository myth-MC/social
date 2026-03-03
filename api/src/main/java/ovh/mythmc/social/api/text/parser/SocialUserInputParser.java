package ovh.mythmc.social.api.text.parser;

/**
 * Represents a {@link SocialContextualParser} capable of handling and
 * processing player input.
 * 
 * <p>
 * This interface doesn't add any additional methods and is used internally by
 * the {@link ovh.mythmc.social.api.text.TextProcessor} to identify parsers
 * that should be applied when using the player input mode.
 * </p>
 */
public interface SocialUserInputParser extends SocialContextualParser {
}
