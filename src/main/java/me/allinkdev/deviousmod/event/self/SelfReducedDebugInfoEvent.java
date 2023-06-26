package me.allinkdev.deviousmod.event.self;

import com.github.allinkdev.deviousmod.api.event.Event;

public final class SelfReducedDebugInfoEvent implements Event {

    private boolean reducedDebugInfo;

    public SelfReducedDebugInfoEvent(final boolean reducedDebugInfo) {
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public void setReducedDebugInfo(final boolean reducedDebugInfo) {
        this.reducedDebugInfo = reducedDebugInfo;
    }
}
