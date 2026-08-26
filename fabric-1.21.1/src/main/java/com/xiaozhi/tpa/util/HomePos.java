package com.xiaozhi.tpa.util;

import net.minecraft.util.Identifier;

/**
 * A stored position: coordinates, facing angles and the dimension identifier it belongs to.
 * Plain POJO (fields public, no-arg constructor) so Gson can (de)serialize it to JSON.
 */
public class HomePos {
    public double x, y, z;
    public float yaw, pitch;
    /** Dimension identifier, e.g. "minecraft:overworld". */
    public String dimension;

    public HomePos() {}

    public HomePos(double x, double y, double z, float yaw, float pitch, String dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.dimension = dimension;
    }

    public Identifier identifier() {
        Identifier id = Identifier.tryParse(dimension);
        return id != null ? id : Identifier.of("minecraft", "overworld");
    }
}
