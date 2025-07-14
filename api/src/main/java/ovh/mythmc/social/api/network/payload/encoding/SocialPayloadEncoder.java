package ovh.mythmc.social.api.network.payload.encoding;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public final class SocialPayloadEncoder {

    public static SocialPayloadEncoder empty() {
        return of("");
    }

    public static SocialPayloadEncoder of(final byte[] payload) {
        return new SocialPayloadEncoder(payload);
    }

    public static SocialPayloadEncoder of(final byte[]... bytes) {
        return new SocialPayloadEncoder(encode(bytes));
    }

    public static SocialPayloadEncoder of(final @NotNull String... strings) {
        final var encodedPayload = encode(Arrays.stream(strings)
            .map(string -> string.getBytes(StandardCharsets.UTF_8))
            .toList());

        return new SocialPayloadEncoder(encodedPayload);
    }

    private final byte[] bytes;

    private SocialPayloadEncoder(byte[] payload) {
        this.bytes = payload;
    }

    public byte[] bytes() {
        return this.bytes;
    }

    private static byte[] encode(final @NotNull List<byte[]> bytes) {
        final var out = new ByteArrayOutputStream();

        try {
            for (int i = 0; i < bytes.size(); i++) {
                out.write(bytes.get(i));
                if (bytes.size() > i + 1)
                    out.write(0x00); // End
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }

        return out.toByteArray();
    }

    private static byte[] encode(byte[]... bytes) {
        return encode(Arrays.stream(bytes).toList());
    }

}
