package me.allinkdev.deviousmod;

import me.allinkdev.deviousmod.command.CommandManager;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviousMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("deviousmod");
    private static DeviousMod INSTANCE;

    public static DeviousMod getInstance() {
        return INSTANCE;
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;

        CommandManager.init();
        ModuleManager.init();

        ClientCommandRegistrationCallback.EVENT.register(CommandManager::register);
    }
}
