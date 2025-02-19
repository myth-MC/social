package ovh.mythmc.social.common.text.formatter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import ovh.mythmc.social.api.context.SocialParserContext;
import ovh.mythmc.social.api.text.formatter.SocialInlineFormatter;

public final class SmallFontTextFormatter extends SocialInlineFormatter {

    @Override
    public String characters() {
        return "-#";
    }

    @Override
    public Component format(SocialParserContext context) {
        if (context.message() instanceof TextComponent textComponent) {
            textComponent = textComponent.content(toSmallCaps(textComponent.content()));
            return textComponent;
        }

        return context.message();
    }

    private static final char[] SMALL_CAPS_ALPHABET = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘqʀꜱᴛᴜᴠᴡxyᴢ".toCharArray();

    private static String toSmallCaps(String text)
    {
        if(null == text) {
            return null;
        }
        int length = text.length();
        StringBuilder smallCaps = new StringBuilder(length);
        for(int i=0; i<length; ++i) {
            char c = text.charAt(i);
            if(c >= 'a' && c <= 'z') {
                smallCaps.append(SMALL_CAPS_ALPHABET[c - 'a']);
            } else {
                smallCaps.append(c);
            }
        }
        return smallCaps.toString();
    }
    
}
