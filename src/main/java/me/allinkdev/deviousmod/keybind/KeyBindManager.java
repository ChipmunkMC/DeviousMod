package me.allinkdev.deviousmod.keybind;

import com.github.allinkdev.reflector.Reflector;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.Pair;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickEndEvent;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class KeyBindManager {
    private final List<Pair<KeyBinding, KeyBind>> binds = new ArrayList<>();

    public KeyBindManager(final DeviousMod deviousMod) {
        final EventBus eventBus = deviousMod.getEventBus();
        eventBus.register(this);

        Reflector.createNew(KeyBind.class)
                .allSubClassesInSubPackage("impl")
                .map(Reflector::createNew)
                .map(r -> r.create(deviousMod))
                .map(Optional::orElseThrow)
                .map(this::createPair)
                .forEach(this.binds::add);
    }

    public void register(final KeyBind keyBind) {
        final Pair<KeyBinding, KeyBind> pair = this.createPair(keyBind);

        this.binds.add(pair);
    }

    private Pair<KeyBinding, KeyBind> createPair(final KeyBind keyBind) {
        final int defaultKey = keyBind.getDefaultKey();
        final String name = keyBind.getName();
        final KeyBinding keyBinding = new KeyBinding(name, defaultKey, "DeviousMod");

        KeyBindingHelper.registerKeyBinding(keyBinding);

        return Pair.of(keyBinding, keyBind);
    }

    private void checkPress(final Pair<KeyBinding, KeyBind> pair) {
        final KeyBinding keyBinding = pair.first();

        if (!keyBinding.wasPressed()) {
            return;
        }

        final KeyBind keyBind = pair.second();
        keyBind.onPress();
    }

    @Subscribe
    public void onTick(final ClientTickEndEvent event) {
        this.binds.forEach(this::checkPress);
    }
}
