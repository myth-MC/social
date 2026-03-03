package ovh.mythmc.social.api.text.filter;

/**
 * Abstract parser capable of censoring text by matching a literal
 * text input.
 * 
 * <p>
 * Any text matching {@code literal()} will be filtered out and replaced
 * with {@code ***}.
 * </p>
 * 
 * @see SocialFilterRegex
 */
public abstract class SocialFilterLiteral extends SocialFilterRegex {

    public abstract String literal();

    @Override
    public String regex() {
        return "\\b(?i:" + literal() + ")\\b";
    }

}
