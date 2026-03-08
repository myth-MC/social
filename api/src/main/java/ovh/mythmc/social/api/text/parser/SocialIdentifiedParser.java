package ovh.mythmc.social.api.text.parser;

/**
 * Represents a {@link SocialContextualParser} that can be identified with
 * a given {@code identifier} in the form of a {@link String}.
 */
public interface SocialIdentifiedParser extends SocialContextualParser {

    /**
     * Gets the {@code identifier} of this {@link SocialContextualParser}.
     * @return the {@code identifier} of this {@link SocialContextualParser}
     */
    String identifier();
    
}
