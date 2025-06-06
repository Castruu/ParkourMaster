package com.vfcastro.dev.parkour.data;

import com.vfcastro.dev.parkour.entity.Checkpoint;
import com.vfcastro.dev.parkour.entity.Parkour;

import java.util.List;

public record ParkourData(Parkour parkour, List<Checkpoint> checkpoints) {}
