package me.allinkdev.deviousmod.keying;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.settings.AbstractDataStore;
import net.minecraft.client.util.Session;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public final class BotKey extends AbstractDataStore {
    private String identifier;
    private String key;
    private String algorithm;
    private String template;
    private int hashLen;
    private int timestampForgiveness;
    private Charset charset;

    public BotKey(final Path path) {
        super(path);
    }

    BotKey(final UUID uuid, final String identifier, final String key, final String algorithm, final String template, final int hashLen, final int timestampForgiveness, final Charset charset) {
        super(BotKeyProvider.KEY_PATH.resolve(uuid.toString() + ".json"));

        this.identifier = identifier;
        this.key = key;
        this.algorithm = algorithm;
        this.template = template;
        this.hashLen = hashLen;
        this.timestampForgiveness = timestampForgiveness;
        this.charset = charset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void load() throws IOException {
        load(BotKey.class);
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

    @Override
    public void save() throws IOException {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("identifier", this.identifier);
        jsonObject.addProperty("key", this.key);
        jsonObject.addProperty("algorithm", this.algorithm);
        jsonObject.addProperty("template", this.template);
        jsonObject.addProperty("hashLen", this.hashLen);
        jsonObject.addProperty("timestampForgiveness", this.timestampForgiveness);
        jsonObject.addProperty("charset", this.charset.name());

        Files.writeString(this.path, GSON.toJson(jsonObject));
    }

    private JsonElement getElementWithThrowIfNull(final JsonObject jsonObject, final String name) {
        final JsonElement jsonElement = jsonObject.get(name);

        if (jsonElement == null) {
            throw new NullPointerException("Bot key JSON object does not contain the field \" " + name + "\".");
        }

        return jsonElement;
    }

    private JsonPrimitive getAsPrimitiveWithThrow(final JsonElement jsonElement) {
        if (!jsonElement.isJsonPrimitive()) {
            throw new IllegalStateException("Element isn't a primitive!");
        }

        return jsonElement.getAsJsonPrimitive();
    }

    private String getAsStringWithThrow(final JsonPrimitive jsonPrimitive) {
        if (!jsonPrimitive.isString()) {
            throw new IllegalStateException("Element isn't a string!");
        }

        return jsonPrimitive.getAsString();
    }

    private int getAsIntWithThrow(final JsonPrimitive jsonPrimitive) {
        if (!jsonPrimitive.isNumber()) {
            throw new IllegalStateException("Element isn't an integer!");
        }

        return jsonPrimitive.getAsInt();
    }

    @Override
    protected void load(final JsonElement jsonElement) throws IOException {
        if (!jsonElement.isJsonObject()) {
            throw new IllegalArgumentException("Found unexpected non-JSON object in bot key!");
        }

        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        this.identifier = this.getAsStringWithThrow(this.getAsPrimitiveWithThrow(this.getElementWithThrowIfNull(jsonObject, "identifier")));
        this.key = this.getAsStringWithThrow(this.getAsPrimitiveWithThrow(this.getElementWithThrowIfNull(jsonObject, "key")));
        this.template = this.getAsStringWithThrow(this.getAsPrimitiveWithThrow(this.getElementWithThrowIfNull(jsonObject, "template")));
        this.algorithm = this.getAsStringWithThrow(this.getAsPrimitiveWithThrow(this.getElementWithThrowIfNull(jsonObject, "algorithm")));
        this.hashLen = this.getAsIntWithThrow(this.getAsPrimitiveWithThrow(this.getElementWithThrowIfNull(jsonObject, "hashLen")));
        this.timestampForgiveness = this.getAsIntWithThrow(this.getAsPrimitiveWithThrow(this.getElementWithThrowIfNull(jsonObject, "timestampForgiveness")));
        this.charset = Charset.forName(this.getAsStringWithThrow(this.getAsPrimitiveWithThrow(this.getElementWithThrowIfNull(jsonObject, "charset"))));
    }

    public static final class Builder {
        private String identifier;
        private String key;
        private String algorithm;
        private String template;
        private int hashLen;
        private int timestampForgiveness;
        private Charset charset;

        public Builder identifier(final String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder key(final String key) {
            this.key = key;
            return this;
        }

        public Builder algorithm(final String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder template(final String template) {
            this.template = template;
            return this;
        }

        public Builder hashLen(final int hashLen) {
            this.hashLen = hashLen;
            return this;
        }

        public Builder timestampForgiveness(final int timestampForgiveness) {
            this.timestampForgiveness = timestampForgiveness;
            return this;
        }

        public Builder charset(final Charset charset) {
            this.charset = charset;
            return this;
        }

        public BotKey build() {
            return new BotKey(
                    UUID.randomUUID(),
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
}
