package com.vfcastro.dev.parkour.manager;

import com.vfcastro.dev.parkour.ParkourMaster;
import com.vfcastro.dev.parkour.data.ParkourData;
import com.vfcastro.dev.parkour.data.ProgressData;
import com.vfcastro.dev.parkour.database.ParkourDatabaseManager;
import com.vfcastro.dev.parkour.entity.Checkpoint;
import com.vfcastro.dev.parkour.entity.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParkourManager {

    private final Map<String, ParkourData> parkoursMap = new HashMap<>();
    private final Map<UUID, ProgressData> activePlayers = new HashMap<>();

    private final ParkourMaster plugin;
    private final ParkourDatabaseManager parkourDatabaseManager;

    public ParkourManager(ParkourMaster plugin, ParkourDatabaseManager parkourDatabaseManager) {
        this.parkourDatabaseManager = parkourDatabaseManager;
        this.plugin = plugin;

        loadParkourFromDatabase();
    }


    private void loadParkourFromDatabase() {
        List<Parkour> parkourList = parkourDatabaseManager.getAllParkour();
        if (parkourList == null) {
            plugin.getServer().sendRichMessage("<red>Problem while loading parkour.</red>");
            return;
        }

        for(Parkour parkour : parkourList) {
            List<Checkpoint> checkpoints = parkourDatabaseManager.getCheckpointsFromParkour(parkour);
            checkpoints.forEach(this::addPressurePlateToCheckpoint);
            parkoursMap.put(parkour.name(), new ParkourData(parkour, checkpoints));
        }
    }

    private void addPressurePlateToCheckpoint(Checkpoint checkpoint) {
        Location location = new Location(Bukkit.getWorlds().getFirst(), checkpoint.x(), checkpoint.y(), checkpoint.z());
        location.getBlock().setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
    }

    public Optional<ParkourData> getParkourDataByParkourName(String parkourName) {
        return Optional.ofNullable(parkoursMap.get(parkourName));
    }

    public Optional<ProgressData> getPlayerProgress(UUID playerUUID) {
        return Optional.ofNullable(activePlayers.get(playerUUID));
    }

    public void createParkour(String name, Location location) {
        Parkour parkour = new Parkour(
                null,
                name,
                false
        );
        BigInteger parkourId = parkourDatabaseManager.createParkour(parkour);
        Parkour persistedParkour = parkourDatabaseManager.getParkour(parkourId);
        Checkpoint checkpoint = new Checkpoint(
                persistedParkour,
                null,
                0,
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
        parkourDatabaseManager.addParkourCheckpointToParkour(persistedParkour, checkpoint);
        parkoursMap.put(name, new ParkourData(persistedParkour, List.of(checkpoint)));
        addPressurePlateToCheckpoint(checkpoint);
    }

    public void addCheckpointToParkour(String name, Location location) {
        ParkourData parkourData = getParkourDataByParkourName(name).orElseThrow(
                () -> new IllegalArgumentException("Parkour not found with name: " + name)
        );
        if(parkourData.parkour().finished()) {
            throw new IllegalStateException("Parkour " + name + " is already finished.");
        }
        int nextStep = parkourData.checkpoints().stream()
                .mapToInt(Checkpoint::step)
                .max()
                .orElse(0) + 1;
        Checkpoint checkpoint = new Checkpoint(
                parkourData.parkour(),
                null,
                nextStep,
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
        parkourDatabaseManager.addParkourCheckpointToParkour(parkourData.parkour(), checkpoint);
        ParkourData pData = new ParkourData(parkourData.parkour(),
                Stream.concat(
                  parkourData.checkpoints().stream(),
                  Stream.of(checkpoint)
                ).collect(Collectors.toList()));
        parkoursMap.put(name, pData);
        addPressurePlateToCheckpoint(checkpoint);
    }

    public boolean isParkourFinished(String name) {
        return getParkourDataByParkourName(name).map(it -> it.parkour().finished()).orElseThrow(
                () -> new IllegalArgumentException("Parkour not found with name: " + name)
        );
    }

    public void finishParkour(String name) {
        ParkourData parkourData = getParkourDataByParkourName(name).orElseThrow(
                () -> new IllegalArgumentException("Parkour not found with name: " + name)
        );

        parkourDatabaseManager.finishParkour(parkourData.parkour());

        ParkourData updatedParkourData = new ParkourData(
                new Parkour(
                        parkourData.parkour().id(),
                        parkourData.parkour().name(),
                        true
                ),
                parkourData.checkpoints()
        );

        parkoursMap.put(name, updatedParkourData);
    }

    public void startPlayerParkour(UUID playerUUID, String name) {
        setPlayerProgress(playerUUID, name, 0);

    }

    public boolean isPlayerFinished(UUID playerUUID) {
        ProgressData progressData = getPlayerProgress(playerUUID).orElseThrow(
                () -> new IllegalArgumentException("Player not found with uuid: " + playerUUID)
        );
        int lastStep = progressData.parkourData().checkpoints().stream()
                .max(Comparator.comparingInt(Checkpoint::step))
                .orElseThrow()
                .step();
        return progressData.progress() >= lastStep;
    }

    public void finishPlayerParkour(UUID playerUUID) {
        activePlayers.remove(playerUUID);
    }

    public void setPlayerProgress(UUID playerUUID, String name, Integer progress) {
        ParkourData data = getParkourDataByParkourName(name).orElseThrow(
                () -> new IllegalArgumentException("No parkour with name " + name + " exists")
        );

        activePlayers.put(playerUUID, new ProgressData(progress, data));
    }

    public Checkpoint getCheckpointByLocation(Location location) {
        return parkoursMap.values().stream()
                .flatMap(parkourData -> parkourData.checkpoints().stream())
                .filter(checkpoint ->
                        location.getBlockX() == checkpoint.x() &&
                                location.getBlockY() == checkpoint.y() &&
                                location.getBlockZ() == checkpoint.z()
                )
                .findFirst()
                .orElse(null);
    }

}
