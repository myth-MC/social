package ovh.mythmc.social.api.text.filters;

public abstract class SocialFilterLiteral extends SocialFilterRegex {

    public abstract String literal();

    @Override
    public String regex() {
        return "\\b(?i:" + literal() + ")\\b";
    }

}
