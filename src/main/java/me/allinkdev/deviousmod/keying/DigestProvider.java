package me.allinkdev.deviousmod.keying;

import me.allinkdev.deviousmod.util.NoConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public final class DigestProvider extends NoConstructor {
    private static final Set<String> EXPECTED_ALGORITHMS = Set.of("md5", "sha-1", "sha-224", "sha-256", "sha-384", "sha-512", "sha3-224", "sha3-256", "sha3-384", "sha3-512");
    private static final Set<String> AVAILABLE_ALGORITHMS;

    static {
        final Set<String> algorithms = new HashSet<>();
        final Logger logger = LoggerFactory.getLogger("Digest Argument Type Available Hashing Algorithm Checker");

        for (final String expectedAlgorithm : EXPECTED_ALGORITHMS) {
            try {
                final MessageDigest digest = MessageDigest.getInstance(expectedAlgorithm);
                algorithms.add(expectedAlgorithm);
            } catch (NoSuchAlgorithmException e) {
                logger.warn("Algorithm {} isn't available on this JVM!", expectedAlgorithm);
            }
        }

        AVAILABLE_ALGORITHMS = Collections.unmodifiableSet(algorithms);
    }

    public static Set<String> getExpectedAlgorithms() {
        return EXPECTED_ALGORITHMS;
    }

    public static Set<String> getAvailableAlgorithms() {
        return AVAILABLE_ALGORITHMS;
    }

    public static Optional<String> getHash(final String input, final String algorithm, final Charset charset) {
        return getHash(input, algorithm, charset, -1);
    }

    public static Optional<String> getHash(final String input, final String algorithm, final Charset charset, final int slice) {
        final Optional<MessageDigest> digestOptional = getDigest(algorithm);

        if (digestOptional.isEmpty()) {
            return Optional.empty();
        }

        final MessageDigest digest = digestOptional.get();

        final byte[] stringBytes = input.getBytes(charset);
        final byte[] result = digest.digest(stringBytes);
        final BigInteger resultInt = new BigInteger(1, Arrays.copyOfRange(result, 0, slice == -1 ? result.length : slice));

        return Optional.of(resultInt.toString(Character.MAX_RADIX));
    }

    public static Optional<MessageDigest> getDigest(final String algorithm) {
        try {
            final MessageDigest digest = MessageDigest.getInstance(algorithm);

            return Optional.of(digest);
        } catch (NoSuchAlgorithmException e) {
            return Optional.empty();
        }
    }
}
