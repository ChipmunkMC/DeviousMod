package me.allinkdev.deviousmod.test;

import com.github.steveice10.opennbt.tag.builtin.*;
import me.allinkdev.deviousmod.data.DataCompound;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.security.SecureRandom;

class DataCompoundTest {
    private static final Logger LOGGER = LoggerFactory.getLogger("DeviousMod/Tests");
    private static final SecureRandom RANDOM = new SecureRandom();

    protected Tag generateRandomTag(int depth) {
        final int type = RANDOM.nextInt(1, 12);
        final int keyLen = RANDOM.nextInt(3, 20);
        final int arrayLen = RANDOM.nextInt(1, 30);
        final String name = RandomStringUtils.randomAlphanumeric(keyLen);

        if (depth >= 10) {
            return new DoubleTag(name, RANDOM.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE));
        }

        switch (type) {
            case 1 -> {
                final byte[] bytes = new byte[arrayLen];

                RANDOM.nextBytes(bytes);

                return new ByteArrayTag(name, bytes);
            }
            case 2 -> {
                final byte[] bytes = new byte[1];
                RANDOM.nextBytes(bytes);

                return new ByteTag(name, bytes[0]);
            }
            case 3 -> {
                final CompoundTag compoundTag = new CompoundTag(name);

                for (int i = 0; i < arrayLen; i++) {
                    compoundTag.put(generateRandomTag(depth + 1));
                }

                return compoundTag;
            }
            case 4 -> {
                final double generatedDouble = RANDOM.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);

                return new DoubleTag(name, generatedDouble);
            }
            case 5 -> {
                final float generatedFloat = RANDOM.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);

                return new FloatTag(name, generatedFloat);
            }
            case 6 -> {
                final int[] ints = new int[arrayLen];

                for (int i = 0; i < arrayLen; i++) {
                    ints[i] = RANDOM.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
                }

                return new IntArrayTag(name, ints);
            }
            case 7 -> {
                final int generatedInt = RANDOM.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);

                return new IntTag(name, generatedInt);
            }
            case 8 -> {
                final ListTag listTag = new ListTag(name, FloatTag.class);

                for (int i = 0; i < arrayLen; i++) {
                    listTag.add(new FloatTag(name, RANDOM.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE)));
                }

                return listTag;
            }
            case 9 -> {
                final long[] longs = new long[arrayLen];

                for (int i = 0; i < arrayLen; i++) {
                    longs[i] = RANDOM.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
                }

                return new LongArrayTag(name, longs);
            }
            case 10 -> {
                final long generatedLong = RANDOM.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);

                return new LongTag(name, generatedLong);
            }
            case 11 -> {
                final short generatedShort = (short) RANDOM.nextInt(Short.MIN_VALUE, Short.MAX_VALUE);

                return new ShortTag(name, generatedShort);
            }
            case 12 -> {
                final String generatedString = RandomStringUtils.randomPrint(arrayLen);

                return new StringTag(name, generatedString);
            }
        }

        throw new IllegalStateException("Somehow didn't generate a tag???");
    }

    @Test
    void compoundTag() {
        final long timestamp = System.currentTimeMillis();
        final String fileName = String.format("test-%d.bin", timestamp);
        final Path dir = Path.of(".");
        final Path filePath = Path.of(fileName);
        final DataCompound dataCompound = new DataCompound(fileName, dir, filePath);
        final CompoundTag compoundTag = dataCompound.getCompoundTag();
        final File file = dataCompound.getFile();
        final int tagCount = RANDOM.nextInt(8, 255);

        for (int i = 0; i < tagCount; i++) {
            compoundTag.put(generateRandomTag(1));
        }

        LOGGER.info("Saving generated tag to disk!");

        dataCompound.save();

        final long fileSize = file.length();
        LOGGER.info("Saved to disk, taking up {} KB ({} MB, {} GB)", fileSize / 1024, (fileSize / 1024) / 1024, ((fileSize / 1024) / 1024) / 1024);
        LOGGER.info("Reading from disk!");
        final DataCompound readTest = new DataCompound(fileName, dir, filePath);
        final CompoundTag diskTag = readTest.getCompoundTag();
    }
}
