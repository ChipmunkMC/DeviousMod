package me.allinkdev.deviousmod.keying;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import me.allinkdev.deviousmod.DeviousMod;
import net.minecraft.client.util.Session;

import java.nio.charset.Charset;
import java.util.Optional;

public final class BotKey {
    private final String identifier;
    private final String key;
    private final String algorithm;
    private final String template;
    private final int hashLen;
    private final int timestampForgiveness;
    private final Charset charset;

    BotKey(String identifier, String key, String algorithm, String template, int hashLen, int timestampForgiveness, Charset charset) {
        this.identifier = identifier;
        this.key = key;
        this.algorithm = algorithm;
        this.template = template;
        this.hashLen = hashLen;
        this.timestampForgiveness = timestampForgiveness;
        this.charset = charset;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getKey() {
        return this.key;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public String getTemplate() {
        return this.template;
    }

    public int getHashLen() {
        return this.hashLen;
    }

    public int getTimestampForgiveness() {
        return this.timestampForgiveness;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public static BotKey loadFromTag(final CompoundTag tag) throws IllegalArgumentException {
        final StringTag identifierTag = tag.get("Identifier");

        if (identifierTag == null) {
            throw new IllegalArgumentException("Identifier tag not found!");
        }

        final String identifier = identifierTag.getValue();

        final StringTag keyTag = tag.get("Key");

        if (keyTag == null) {
            throw new IllegalArgumentException("Key tag not found!");
        }

        final String key = keyTag.getValue();

        final StringTag algorithmTag = tag.get("Algorithm");

        if (algorithmTag == null) {
            throw new IllegalArgumentException("Algorithm tag not found!");
        }

        final String algorithm = algorithmTag.getValue();

        final StringTag templateTag = tag.get("Template");

        if (templateTag == null) {
            throw new IllegalArgumentException("Template tag not found!");
        }

        final String template = templateTag.getValue();

        final IntTag hashLenTag = tag.get("HashLen");

        if (hashLenTag == null) {
            throw new IllegalArgumentException("Hash len tag not found!");
        }

        final int hashLen = hashLenTag.getValue();

        final IntTag timestampForgivenessTag = tag.get("TimestampForgiveness");

        if (timestampForgivenessTag == null) {
            throw new IllegalArgumentException("Timestamp forgiveness tag not found!");
        }

        final int timestampForgiveness = timestampForgivenessTag.getValue();

        final StringTag charsetTag = tag.get("InputCharset");

        if (charsetTag == null) {
            throw new IllegalArgumentException("Input charset tag not found!");
        }

        final String charsetName = charsetTag.getValue();
        final Charset charset = Charset.forName(charsetName);

        return BotKey.builder()
                .identifier(identifier)
                .key(key)
                .algorithm(algorithm)
                .template(template)
                .hashLen(hashLen)
                .timestampForgiveness(timestampForgiveness)
                .charset(charset)
                .build();
    }

    public Optional<String> encode(final String command) {
        final long timestamp = System.currentTimeMillis();
        final long forgivenTimestamp = timestamp / Math.max((timestampForgiveness * 1000L), 1L);
        final Session session = DeviousMod.CLIENT.getSession();
        final String username = session.getUsername();
        final String uuid = session.getUuid();

        String toHash = template;

        toHash = toHash.replace("UUID", uuid);
        toHash = toHash.replace("NAME", username);
        toHash = toHash.replace("TIMESTAMP", String.valueOf(forgivenTimestamp));
        toHash = toHash.replace("KEY", key);
        toHash = toHash.replace("COMMAND", command);

        final Optional<String> hashOptional = DigestProvider.getHash(toHash, algorithm, charset, hashLen == 0 ? -1 : hashLen);

        if (hashOptional.isEmpty()) {
            return Optional.empty();
        }

        final String hash = hashOptional.get();

        return Optional.of(hash);
    }

    public CompoundTag getAsTag() {
        final CompoundTag tag = new CompoundTag("");
        final StringTag identifierTag = new StringTag("Identifier", identifier);
        final StringTag keyTag = new StringTag("Key", key);
        final StringTag algorithmTag = new StringTag("Algorithm", algorithm);
        final StringTag templateTag = new StringTag("Template", template);
        final IntTag hashLenTag = new IntTag("HashLen", hashLen);
        final IntTag timestampForgivenessTag = new IntTag("TimestampForgiveness", timestampForgiveness);
        final StringTag charsetTag = new StringTag("InputCharset", charset.name());

        tag.put(identifierTag);
        tag.put(keyTag);
        tag.put(algorithmTag);
        tag.put(templateTag);
        tag.put(hashLenTag);
        tag.put(timestampForgivenessTag);
        tag.put(charsetTag);

        return tag;
    }

    public static final class Builder {
        private String identifier;
        private String key;
        private String algorithm;
        private String template;
        private int hashLen;
        private int timestampForgiveness;
        private Charset charset;

        public Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder template(String template) {
            this.template = template;
            return this;
        }

        public Builder hashLen(int hashLen) {
            this.hashLen = hashLen;
            return this;
        }

        public Builder timestampForgiveness(int timestampForgiveness) {
            this.timestampForgiveness = timestampForgiveness;
            return this;
        }

        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public BotKey build() {
            return new BotKey(
                    this.identifier,
                    this.key,
                    this.algorithm,
                    this.template,
                    this.hashLen,
                    this.timestampForgiveness,
                    this.charset
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
