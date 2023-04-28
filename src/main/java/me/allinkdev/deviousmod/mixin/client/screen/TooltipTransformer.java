package me.allinkdev.deviousmod.mixin.client.screen;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.tooltip.TooltipRenderEvent;
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
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();
        final List<Text> texts = cir.getReturnValue();
        final TooltipRenderEvent event = new TooltipRenderEvent(texts, (ItemStack) (Object) this);

        eventBus.post(event);
    }
}
