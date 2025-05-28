package com.vfcastro.dev.parkour;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public class ParkourMasterBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                (commands) -> {
                    commands.registrar().register(
                            "test", (commandSourceStack, args) ->
                            {
                                commandSourceStack.getExecutor().sendPlainMessage("Hello!");
                            }
                    );
                });
    }

}
