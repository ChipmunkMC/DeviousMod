package me.allinkdev.deviousmod.keybind;

import com.github.allinkdev.deviousmod.api.keybind.KeyBind;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.github.allinkdev.deviousmod.api.managers.KeyBindManager;
import com.github.allinkdev.reflector.Reflector;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.Pair;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickEndEvent;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

import java.util.*;

public final class DKeyBindManager implements KeyBindManager<KeyBinding> {
    private final List<Pair<KeyBinding, KeyBind<KeyBinding>>> binds = new ArrayList<>();

    public DKeyBindManager(final DeviousMod deviousMod) {
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        eventManager.registerListener(this);

        Reflector.createNew(DKeyBind.class)
                .allSubClassesInSubPackage("impl")
                .map(Reflector::createNew)
                .map(r -> r.create(deviousMod))
                .map(Optional::orElseThrow)
                .map(this::createPair)
                .forEach(this.binds::add);
    }

    @Override
    public void register(final KeyBind<KeyBinding> keyBind) {
        final Pair<KeyBinding, KeyBind<KeyBinding>> pair = this.createPair(keyBind);

        this.binds.add(pair);
    }

    private Pair<KeyBinding, KeyBind<KeyBinding>> createPair(final KeyBind<KeyBinding> keyBind) {
        final int defaultKey = keyBind.getDefaultKey();
        final String name = keyBind.getName();
        final KeyBinding keyBinding = new KeyBinding(name, defaultKey, "DeviousMod");

        KeyBindingHelper.registerKeyBinding(keyBinding);
        ((DKeyBind) keyBind).onRegister(); // FIXME: 6/4/23 If the KeyBindManager is passed a KeyBind<KeyBinding> that isn't the DKeyBind implementation, this code can cause a ClassCastException. However, I am not fixing this issue currently, as the API doesn't expose methods for registering keybinds, yet. If/when it does, this code WILL need to be updated.

        return Pair.of(keyBinding, keyBind);
    }

    private void checkPress(final Pair<KeyBinding, KeyBind<KeyBinding>> pair) {
        final KeyBinding keyBinding = pair.first();

        if (!keyBinding.wasPressed()) {
            return;
        }

        final KeyBind<KeyBinding> keyBind = pair.second();
        keyBind.onPress();
        keyBind.onDepress();
    }

    @Override
    public void onTick() {
        this.binds.forEach(this::checkPress);
    }

    @Override
    public Set<KeyBind<KeyBinding>> getKeyBinds() {
        final Set<KeyBind<KeyBinding>> keyBinds = new LinkedHashSet<>();
        this.binds.stream().map(Pair::second).forEach(keyBinds::add);

        return Collections.unmodifiableSet(keyBinds);
    }

    @Subscribe
    public void onTickEvent(final ClientTickEndEvent event) {
        this.binds.forEach(this::checkPress);
    }
}
