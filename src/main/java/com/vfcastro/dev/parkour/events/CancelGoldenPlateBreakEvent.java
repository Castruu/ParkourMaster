package com.vfcastro.dev.parkour.events;

import com.vfcastro.dev.parkour.entity.Checkpoint;
import com.vfcastro.dev.parkour.manager.ParkourManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class CancelGoldenPlateBreakEvent implements Listener {

    ParkourManager parkourManager;

    public CancelGoldenPlateBreakEvent(ParkourManager parkourManager) {
        this.parkourManager = parkourManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE) return;

        Player player = event.getPlayer();
        Checkpoint checkpoint = parkourManager.getCheckpointByLocation(block.getLocation());
        if(checkpoint == null) return;

        player.sendRichMessage("<red>This is a parkour checkpoint, it cannot be broken.</red>");
        event.setCancelled(true);
    }

}
