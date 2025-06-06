package com.vfcastro.dev.parkour.entity;

import java.math.BigInteger;

public record Checkpoint(Parkour parkour, BigInteger id, int step, int x, int y, int z) {
}
