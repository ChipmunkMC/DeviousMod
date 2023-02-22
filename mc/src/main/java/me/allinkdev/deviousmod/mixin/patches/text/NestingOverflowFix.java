package me.allinkdev.deviousmod.mixin.patches.text;

import com.google.gson.*;
import me.allinkdev.deviousmod.util.TextUtil;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Type;
import java.util.Optional;

@Mixin(Text.Serializer.class)
public abstract class NestingOverflowFix {
    private static final int MAXIMUM_LEVELS = 256;

    @Shadow
    protected abstract Optional<Text> getSeparator(Type type, JsonDeserializationContext context, JsonObject json);

    private MutableText deserializeSafe(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) {
        return deserializeSafe(jsonElement, type, jsonDeserializationContext, 1);
    }

    private MutableText deserializeSafe(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext, final int depth) {
        if (depth > MAXIMUM_LEVELS) {
            return TextUtil.INVALID_JSON;
        }

        if (jsonElement.isJsonPrimitive()) {
            return Text.literal(jsonElement.getAsString());
        } else {
            MutableText mutableText;
            if (!jsonElement.isJsonObject()) {
                if (jsonElement.isJsonArray()) {
                    final JsonArray jsonArray3 = jsonElement.getAsJsonArray();
                    mutableText = null;

                    for (final JsonElement jsonElement2 : jsonArray3) {
                        final MutableText mutableText2 = this.deserializeSafe(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext, depth + (jsonElement2.isJsonArray() ? 1 : 0));
                        if (mutableText == null) {
                            mutableText = mutableText2;
                        } else {
                            mutableText.append(mutableText2);
                        }
                    }

                    return mutableText;
                } else {
                    throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                }
            } else {
                final JsonObject jsonObject = jsonElement.getAsJsonObject();
                final String string;
                if (jsonObject.has("text")) {
                    string = JsonHelper.getString(jsonObject, "text");
                    mutableText = string.isEmpty() ? Text.empty() : Text.literal(string);
                } else if (jsonObject.has("translate")) {
                    string = JsonHelper.getString(jsonObject, "translate");
                    if (jsonObject.has("with")) {
                        final JsonArray jsonArray = JsonHelper.getArray(jsonObject, "with");
                        final Object[] objects = new Object[jsonArray.size()];

                        for (int i = 0; i < objects.length; ++i) {
                            final JsonElement jsonElement1 = jsonArray.get(i);

                            if (jsonElement1.isJsonArray()) {
                                objects[i] = Text.Serializer.optimizeArgument(this.deserializeSafe(jsonArray.get(i), type, jsonDeserializationContext, depth + 1));
                                continue;
                            }

                            objects[i] = Text.Serializer.optimizeArgument(this.deserializeSafe(jsonArray.get(i), type, jsonDeserializationContext));
                        }

                        mutableText = Text.translatable(string, objects);
                    } else {
                        mutableText = Text.translatable(string);
                    }
                } else if (jsonObject.has("score")) {
                    final JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "score");
                    if (!jsonObject2.has("name") || !jsonObject2.has("objective")) {
                        throw new JsonParseException("A score component needs a least a name and an objective");
                    }

                    mutableText = Text.score(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
                } else if (jsonObject.has("selector")) {
                    final Optional<Text> optional = this.getSeparator(type, jsonDeserializationContext, jsonObject);
                    mutableText = Text.selector(JsonHelper.getString(jsonObject, "selector"), optional);
                } else if (jsonObject.has("keybind")) {
                    mutableText = Text.keybind(JsonHelper.getString(jsonObject, "keybind"));
                } else {
                    if (!jsonObject.has("nbt")) {
                        throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                    }

                    string = JsonHelper.getString(jsonObject, "nbt");
                    final Optional<Text> optional2 = this.getSeparator(type, jsonDeserializationContext, jsonObject);
                    final boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
                    final NbtDataSource nbtDataSource;
                    if (jsonObject.has("block")) {
                        nbtDataSource = new BlockNbtDataSource(JsonHelper.getString(jsonObject, "block"));
                    } else if (jsonObject.has("entity")) {
                        nbtDataSource = new EntityNbtDataSource(JsonHelper.getString(jsonObject, "entity"));
                    } else {
                        if (!jsonObject.has("storage")) {
                            throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                        }

                        nbtDataSource = new StorageNbtDataSource(new Identifier(JsonHelper.getString(jsonObject, "storage")));
                    }

                    mutableText = Text.nbt(string, bl, optional2, nbtDataSource);
                }

                if (jsonObject.has("extra")) {
                    final JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "extra");
                    if (jsonArray2.size() <= 0) {
                        throw new JsonParseException("Unexpected empty array of components");
                    }

                    for (final JsonElement element : jsonArray2) {
                        mutableText.append(this.deserializeSafe(element, type, jsonDeserializationContext, element.isJsonArray() ? 1 : 0));
                    }
                }

                mutableText.setStyle(jsonDeserializationContext.deserialize(jsonElement, Style.class));
                return mutableText;
            }
        }
    }

    /**
     * @author Allink
     * @reason Patching nesting crash
     */
    @Overwrite
    public MutableText deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return deserializeSafe(jsonElement, type, jsonDeserializationContext);
    }
}
