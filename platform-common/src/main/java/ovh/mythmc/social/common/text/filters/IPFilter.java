package ovh.mythmc.social.common.text.filters;

import ovh.mythmc.social.api.text.filters.SocialFilterRegex;

public final class IPFilter extends SocialFilterRegex {

    @Override
    public String regex() {
        return "\\b(?:(?:2(?:[0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])\\.){3}(?:(?:2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9]))\\b";
    }

}
