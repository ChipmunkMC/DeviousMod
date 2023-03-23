package me.allinkdev.deviousmod.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.DCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class TestCommand extends DCommand {
    public TestCommand(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public String getCommandName() {
        return "testing";
    }

    @Override
    public int execute(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final String algorithm = StringArgumentType.getString(context, "algorithm");
        final MessageDigest digest;

        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No algo", e);
        }

        final String message = StringArgumentType.getString(context, "message");
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final FastBufferedOutputStream byteArrayBufferStream = new FastBufferedOutputStream(byteArrayOutputStream);
        final DigestOutputStream digestOutputStream = new DigestOutputStream(byteArrayBufferStream, digest);
        final FastBufferedOutputStream bufferedDigestOutputStream = new FastBufferedOutputStream(digestOutputStream);

        try {
            bufferedDigestOutputStream.write(message.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            bufferedDigestOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            byteArrayBufferStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final BigInteger bigInteger = new BigInteger(1, digest.digest());
        final String asHex = bigInteger.toString(16);

        context.getSource().sendFeedback(Text.of(asHex));

        return 1;
    }
}
