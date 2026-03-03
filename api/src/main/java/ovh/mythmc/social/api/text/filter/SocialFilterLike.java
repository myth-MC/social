package ovh.mythmc.social.api.text.filter;

import ovh.mythmc.social.api.text.parser.SocialUserInputParser;

/**
 * Represents a parser that will ONLY apply if the message was sent by a user
 *
 * <p>
 * This interface extends {@link SocialUserInputParser} although it doesn't provide
 * any additional methods.
 * </p>
 */
public interface SocialFilterLike extends SocialUserInputParser {
}
