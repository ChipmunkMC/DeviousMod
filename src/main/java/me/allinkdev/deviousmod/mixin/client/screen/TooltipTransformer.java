package me.allinkdev.deviousmod.mixin.client.screen;

import me.allinkdev.deviousmod.event.tooltip.TooltipRenderEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public final class TooltipTransformer {
    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void onGetWidth(final @Nullable PlayerEntity player, final TooltipContext context, final CallbackInfoReturnable<List<Text>> cir) {
        EventUtil.postEvent(new TooltipRenderEvent(cir.getReturnValue(), (ItemStack) (Object) this));
    }
}
