package ovh.mythmc.social.api.network.payload.parser;

public final class SocialPayloadComponentParsers {

    public static SocialPayloadChannelParser channel() {
        return new SocialPayloadChannelParser();
    }

    public static SocialPayloadGsonComponentParser gson() {
        return new SocialPayloadGsonComponentParser();
    }

    public static SocialPayloadTextComponentParser component() {
        return new SocialPayloadTextComponentParser();
    }

    public static SocialPayloadStringParser string() {
        return new SocialPayloadStringParser();
    }

}
