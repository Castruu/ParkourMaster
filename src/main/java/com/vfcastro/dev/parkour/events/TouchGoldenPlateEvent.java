package com.vfcastro.dev.parkour.events;

import com.vfcastro.dev.parkour.data.ParkourData;
import com.vfcastro.dev.parkour.data.ProgressData;
import com.vfcastro.dev.parkour.entity.Checkpoint;
import com.vfcastro.dev.parkour.manager.ParkourManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class TouchGoldenPlateEvent implements Listener {

    ParkourManager parkourManager;

    public TouchGoldenPlateEvent(ParkourManager parkourManager) {
        this.parkourManager = parkourManager;
    }


    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) return;

        Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.LIGHT_WEIGHTED_PRESSURE_PLATE) return;


        Player player = event.getPlayer();
        Checkpoint checkpoint = parkourManager.getCheckpointByLocation(block.getLocation());
        if(checkpoint == null) return;
        if(!parkourManager.isParkourFinished(checkpoint.parkour().name())) return;
        if(checkpoint.step() == 0) {
            parkourManager.startPlayerParkour(player.getUniqueId(), checkpoint.parkour().name());
            player.sendRichMessage("<green>You are starting parkour: </green><gold>" + checkpoint.parkour().name() + "</gold>");
            return;
        }
        Optional<ProgressData> playerProgress = parkourManager.getPlayerProgress(player.getUniqueId());
        if(playerProgress.isEmpty()) return;
        if(!playerProgress.get().parkourData().checkpoints().contains(checkpoint)) return;

        if(checkpoint.step() != playerProgress.get().progress() + 1) return;

        parkourManager.setPlayerProgress(player.getUniqueId(), checkpoint.parkour().name(), checkpoint.step());
        if(parkourManager.isPlayerFinished(player.getUniqueId())) {
            parkourManager.finishPlayerParkour(player.getUniqueId());
            player.sendRichMessage("<gold>You finished parkour: </gold><green>" + checkpoint.parkour().name() + "</green>");
        } else {
            player.sendRichMessage("<green>You are on checkpoint <blue>" + checkpoint.step() + "</blue> for parkour: </green><gold>" + checkpoint.parkour().name() + "</gold>");
        }

    }


}
