package ovh.mythmc.social.common.text.filter;

import ovh.mythmc.social.api.text.filter.SocialFilterRegex;

public final class URLFilter extends SocialFilterRegex {

    @Override
    public String regex() {
        return "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
    }

}
