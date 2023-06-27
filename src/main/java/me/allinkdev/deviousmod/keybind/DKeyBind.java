package me.allinkdev.deviousmod.keybind;

import com.github.allinkdev.deviousmod.api.keybind.KeyBind;
import com.github.allinkdev.deviousmod.api.keybind.KeyBindLifecycle;
import com.github.allinkdev.deviousmod.api.lifecycle.GenericLifecycleTracker;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.api.factory.keybind.KeyBindLifecycleTransitionEventFactory;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public abstract class DKeyBind extends GenericLifecycleTracker<KeyBindLifecycle> implements KeyBind<KeyBinding> {
    protected final DeviousMod deviousMod;
    private final KeyBindLifecycleTransitionEventFactory transitionEventFactory = new KeyBindLifecycleTransitionEventFactory();
    private final EventManager<?> eventManager;

    protected DKeyBind(final DeviousMod deviousMod) {
        super(KeyBindLifecycle.NONE);

        this.deviousMod = deviousMod;
        this.eventManager = this.deviousMod.getEventManager();
    }

    @Override
    public int getDefaultKey() {
        return GLFW.GLFW_KEY_UNKNOWN;
    }

    @Override
    public Class<KeyBinding> getInternal() {
        return KeyBinding.class;
    }

    private void updateLifecycle(final KeyBindLifecycle newValue) {
        this.transitionEventFactory.create(this, newValue, eventManager::broadcastEvent).join();
    }

    @Override
    public void onPress() {
        this.updateLifecycle(KeyBindLifecycle.PRESSED);
    }

    @Override
    public void onDepress() {
        this.updateLifecycle(KeyBindLifecycle.DEPRESSED);
    }

    public void onRegister() {
        this.updateLifecycle(KeyBindLifecycle.REGISTERED);
    }
}
