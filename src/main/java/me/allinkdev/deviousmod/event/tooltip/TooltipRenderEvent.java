package me.allinkdev.deviousmod.event.tooltip;

import com.github.allinkdev.deviousmod.api.event.Event;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public final class TooltipRenderEvent implements Event {
    private final List<Text> texts;
    private final ItemStack itemStack;

    public TooltipRenderEvent(final List<Text> texts, final ItemStack itemStack) {
        this.texts = texts;
        this.itemStack = itemStack;
    }

    public List<Text> getTexts() {
        return this.texts;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
