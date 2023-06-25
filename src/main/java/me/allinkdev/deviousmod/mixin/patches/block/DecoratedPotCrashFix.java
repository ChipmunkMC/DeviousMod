package me.allinkdev.deviousmod.mixin.patches.block;

import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DecoratedPotBlockEntity.Sherds.class)
public final class DecoratedPotCrashFix {
    @Inject(method = "getSherd(Lnet/minecraft/nbt/NbtList;I)Lnet/minecraft/item/Item;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void getSherd(final NbtList list, final int index, final CallbackInfoReturnable<Item> cir, final NbtElement nbtElement) {
        if (!Identifier.isValid(nbtElement.asString())) cir.setReturnValue(Items.BRICK);
    }
}
