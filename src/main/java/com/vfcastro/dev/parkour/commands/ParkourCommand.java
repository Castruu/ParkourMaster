package com.vfcastro.dev.parkour.commands;

import com.vfcastro.dev.parkour.data.ParkourData;
import com.vfcastro.dev.parkour.data.ProgressData;
import com.vfcastro.dev.parkour.entity.Checkpoint;
import com.vfcastro.dev.parkour.manager.ParkourManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NullMarked
public class ParkourCommand extends BukkitCommand {

    private final ParkourManager parkourManager;

    public ParkourCommand(@NotNull String name, @NotNull String description, String usage, ParkourManager parkourManager) {
        super(name, description, usage, Collections.emptyList());
        this.parkourManager = parkourManager;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendRichMessage("<red>Please provide an action\n</red><grey>Usage:\n" + getUsage() + "</grey>");
            return false;
        }

        if(!sender.isOp()) {
            sender.sendRichMessage("<red>You don't have permission to use this command</red>");
            return false;
        }
        if(!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>You must be a player to use this command</red>");
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("restart")) {
                UUID uuid = player.getUniqueId();
                Optional<ProgressData> playerProgressOpt = parkourManager.getPlayerProgress(uuid);
                if(playerProgressOpt.isEmpty()) {
                    sender.sendRichMessage("<red>There is no parkour to restart!</red>");
                    return false;
                }
                ParkourData data = playerProgressOpt.get().parkourData();
                Checkpoint lastCheckpoint = data.checkpoints().stream().filter(it -> it.step() == playerProgressOpt.get().progress()).findFirst().orElseThrow(
                        () -> new IllegalStateException("There is no parkour to restart!")
                );

                player.teleport(new Location(player.getWorld(), lastCheckpoint.x(), lastCheckpoint.y(), lastCheckpoint.z()));
                return true;
            }

            return false;
        } else if (args.length == 2) {
            String name = args[1];
            if (args[0].equalsIgnoreCase("create")) {
                Location location = player.getLocation();
                try {
                    parkourManager.createParkour(name, location);
                    player.sendRichMessage("<gold>Parkour created with name: " + name + " </gold>");
                    return true;
                } catch (IllegalStateException e) {
                    player.sendRichMessage("<red>An error happened while creating your parkour. Try a different name or location!</red>");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("checkpoint")) {
                try {
                    Location location = player.getLocation();
                    parkourManager.addCheckpointToParkour(name, location);
                    player.sendRichMessage("<gold>Checkpoint added to parkour " + name + " </gold>");
                    return true;
                } catch (IllegalStateException e) {
                    player.sendRichMessage("<red>An error happened while creating your checkpoint. Try a different name or location!</red>");
                } catch (IllegalArgumentException e) {
                    player.sendRichMessage("<red>No parkour with name: " + name + "</red>");
                }

            } else if (args[0].equalsIgnoreCase("finish")) {
                try {
                    parkourManager.finishParkour(name);
                    player.sendRichMessage("<gold>Parkour finalised! You can start playing it now.</gold>");
                    return true;
                } catch (IllegalStateException e) {
                    player.sendRichMessage("<red>An error happened while finalising your checkpoint. Try a different parkour name!</red>");
                }
            }

            return false;
        }

        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }
}
