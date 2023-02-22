package me.allinkdev.deviousmod.tag;

import com.google.common.collect.Lists;
import net.minecraft.nbt.*;
import net.minecraft.nbt.scanner.NbtScanner;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;

public class BetterNbtListReader implements NbtType.OfVariableSize<NbtList> {
    public NbtList read(final DataInput dataInput, final int i, final NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
        nbtTagSizeTracker.add(37L);
        if (i > 512) {
            //throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            return new NbtList();
        } else {
            final byte b = dataInput.readByte();
            final int j = dataInput.readInt();
            if (b == 0 && j > 0) {
                return new NbtList();
                //throw new RuntimeException("Missing type on ListTag");
            } else {
                nbtTagSizeTracker.add(4L * (long) j);
                final NbtType<?> nbtType = NbtTypes.byId(b);
                final List<NbtElement> list = Lists.newArrayListWithCapacity(j);

                for (int k = 0; k < j; ++k) {
                    list.add(nbtType.read(dataInput, i + 1, nbtTagSizeTracker));
                }

                return new NbtList(list, b);
            }
        }
    }

    public NbtScanner.Result doAccept(final DataInput input, final NbtScanner visitor) throws IOException {
        final NbtType<?> nbtType = NbtTypes.byId(input.readByte());
        final int i = input.readInt();
        switch (visitor.visitListMeta(nbtType, i)) {
            case HALT -> {
                return NbtScanner.Result.HALT;
            }
            case BREAK -> {
                nbtType.skip(input, i);
                return visitor.endNested();
            }
            default -> {
                int j = 0;
                label34:
                for (; j < i; ++j) {
                    switch (visitor.startListItem(nbtType, j)) {
                        case HALT:
                            return NbtScanner.Result.HALT;
                        case BREAK:
                            nbtType.skip(input);
                            break label34;
                        case SKIP:
                            nbtType.skip(input);
                            break;
                        default:
                            switch (nbtType.doAccept(input, visitor)) {
                                case HALT:
                                    return NbtScanner.Result.HALT;
                                case BREAK:
                                    break label34;
                            }
                    }
                }
                final int k = i - 1 - j;
                if (k > 0) {
                    nbtType.skip(input, k);
                }
                return visitor.endNested();
            }
        }
    }

    public void skip(final DataInput input) throws IOException {
        final NbtType<?> nbtType = NbtTypes.byId(input.readByte());
        final int i = input.readInt();
        nbtType.skip(input, i);
    }

    public String getCrashReportName() {
        return "LIST";
    }

    public String getCommandFeedbackName() {
        return "TAG_List";
    }
}
