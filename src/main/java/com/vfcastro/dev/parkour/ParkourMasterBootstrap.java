package com.vfcastro.dev.parkour;

import com.vfcastro.dev.parkour.commands.ParkourCommand;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public class ParkourMasterBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                (commands) -> commands.registrar().register(
                        ParkourCommand.createCommand().build()
                ));
    }

}
