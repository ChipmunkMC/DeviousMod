package me.allinkdev.deviousmod.event.effect;

import com.github.allinkdev.deviousmod.api.event.Event;
import net.minecraft.entity.effect.StatusEffectInstance;

public final class ShowStatusEffectIconEvent implements Event {

    private final StatusEffectInstance effectInstance;
    private boolean showIcon;

    public ShowStatusEffectIconEvent(final StatusEffectInstance effectInstance, final boolean showIcon) {
        this.effectInstance = effectInstance;
        this.showIcon = showIcon;
    }

    public StatusEffectInstance getEffectInstance() {
        return effectInstance;
    }

    public boolean isShowIcon() {
        return showIcon;
    }

    public void setShowIcon(final boolean showIcon) {
        this.showIcon = showIcon;
    }
}
