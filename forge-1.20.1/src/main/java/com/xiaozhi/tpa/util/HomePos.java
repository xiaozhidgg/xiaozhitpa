package com.xiaozhi.tpa.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/**
 * A stored position: coordinates, facing angles and the dimension it belongs to.
 * Serialized to/from NBT so homes and the "back" position survive restarts.
 */
public class HomePos {
    public final double x, y, z;
    public final float yaw, pitch;
    public final ResourceLocation dimension;

    public HomePos(double x, double y, double z, float yaw, float pitch, ResourceLocation dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.dimension = dimension;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", x);
        tag.putDouble("y", y);
        tag.putDouble("z", z);
        tag.putFloat("yaw", yaw);
        tag.putFloat("pitch", pitch);
        tag.putString("dim", dimension.toString());
        return tag;
    }

    public static HomePos fromNBT(CompoundTag tag) {
        ResourceLocation dim = ResourceLocation.tryParse(tag.getString("dim"));
        if (dim == null) {
            dim = ResourceLocation.tryParse("minecraft:overworld");
            if (dim == null) {
                dim = new ResourceLocation("minecraft", "overworld");
            }
        }
        return new HomePos(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"),
                tag.getFloat("yaw"), tag.getFloat("pitch"), dim);
    }
}
