package com.xiaozhi.tpa.data;

import com.xiaozhi.tpa.util.HomePos;
import com.xiaozhi.tpa.util.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * World-level persistent data for all players: named homes (grouped by dimension),
 * the last "back" position per player, and the auto-accept flag per player.
 *
 * <p>Stored in the overworld's {@code SavedData} registry under the key
 * {@link Reference#PLAYER_DATA_NAME}, so it survives restarts.</p>
 */
public class PlayerData extends SavedData {
    private final Map<UUID, Map<ResourceLocation, Map<String, HomePos>>> homes = new HashMap<>();
    private final Map<UUID, HomePos> backs = new HashMap<>();
    private final Map<UUID, Boolean> autoAccept = new HashMap<>();

    public PlayerData() {
        super();
    }

    /**
     * Loads (or creates) the global player data. Always uses the overworld's data
     * storage so homes/backs are shared across dimensions.
     */
    public static PlayerData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) {
            overworld = level;
        }
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(PlayerData::new, PlayerData::load),
                Reference.PLAYER_DATA_NAME);
    }

    public static PlayerData load(CompoundTag tag) {
        PlayerData data = new PlayerData();

        CompoundTag homesTag = tag.getCompound("homes");
        for (String uuidStr : homesTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidStr);
            CompoundTag dimsTag = homesTag.getCompound(uuidStr);
            Map<ResourceLocation, Map<String, HomePos>> dims = new HashMap<>();
            for (String dimStr : dimsTag.getAllKeys()) {
                ResourceLocation dim = ResourceLocation.tryParse(dimStr);
                if (dim == null) {
                    continue;
                }
                CompoundTag namesTag = dimsTag.getCompound(dimStr);
                Map<String, HomePos> names = new HashMap<>();
                for (String name : namesTag.getAllKeys()) {
                    names.put(name, HomePos.fromNBT(namesTag.getCompound(name)));
                }
                dims.put(dim, names);
            }
            data.homes.put(uuid, dims);
        }

        CompoundTag backsTag = tag.getCompound("backs");
        for (String uuidStr : backsTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidStr);
            data.backs.put(uuid, HomePos.fromNBT(backsTag.getCompound(uuidStr)));
        }

        CompoundTag autoTag = tag.getCompound("auto_accept_tpa");
        for (String uuidStr : autoTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidStr);
            data.autoAccept.put(uuid, autoTag.getBoolean(uuidStr));
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag homesTag = new CompoundTag();
        for (Map.Entry<UUID, Map<ResourceLocation, Map<String, HomePos>>> e : homes.entrySet()) {
            CompoundTag dimsTag = new CompoundTag();
            for (Map.Entry<ResourceLocation, Map<String, HomePos>> de : e.getValue().entrySet()) {
                CompoundTag namesTag = new CompoundTag();
                for (Map.Entry<String, HomePos> ne : de.getValue().entrySet()) {
                    namesTag.put(ne.getKey(), ne.getValue().toNBT());
                }
                dimsTag.put(de.getKey().toString(), namesTag);
            }
            homesTag.put(e.getKey().toString(), dimsTag);
        }
        tag.put("homes", homesTag);

        CompoundTag backsTag = new CompoundTag();
        for (Map.Entry<UUID, HomePos> e : backs.entrySet()) {
            backsTag.put(e.getKey().toString(), e.getValue().toNBT());
        }
        tag.put("backs", backsTag);

        CompoundTag autoTag = new CompoundTag();
        for (Map.Entry<UUID, Boolean> e : autoAccept.entrySet()) {
            autoTag.putBoolean(e.getKey().toString(), e.getValue());
        }
        tag.put("auto_accept_tpa", autoTag);

        return tag;
    }

    // ---- homes ----

    public void setHome(UUID uuid, ResourceLocation dim, String name, HomePos pos) {
        homes.computeIfAbsent(uuid, k -> new HashMap<>())
                .computeIfAbsent(dim, k -> new HashMap<>())
                .put(name, pos);
        setDirty();
    }

    public HomePos getHome(UUID uuid, ResourceLocation dim, String name) {
        Map<ResourceLocation, Map<String, HomePos>> dims = homes.get(uuid);
        if (dims == null) {
            return null;
        }
        Map<String, HomePos> names = dims.get(dim);
        if (names == null) {
            return null;
        }
        return names.get(name);
    }

    public void removeHome(UUID uuid, ResourceLocation dim, String name) {
        Map<ResourceLocation, Map<String, HomePos>> dims = homes.get(uuid);
        if (dims == null) {
            return;
        }
        Map<String, HomePos> names = dims.get(dim);
        if (names == null) {
            return;
        }
        names.remove(name);
        if (names.isEmpty()) {
            dims.remove(dim);
        }
        if (dims.isEmpty()) {
            homes.remove(uuid);
        }
        setDirty();
    }

    public Collection<String> getHomeNames(UUID uuid, ResourceLocation dim) {
        Map<ResourceLocation, Map<String, HomePos>> dims = homes.get(uuid);
        if (dims == null) {
            return Collections.emptyList();
        }
        Map<String, HomePos> names = dims.get(dim);
        if (names == null) {
            return Collections.emptyList();
        }
        return names.keySet();
    }

    public Map<String, HomePos> getHomes(UUID uuid, ResourceLocation dim) {
        Map<ResourceLocation, Map<String, HomePos>> dims = homes.get(uuid);
        if (dims == null) {
            return Collections.emptyMap();
        }
        return dims.getOrDefault(dim, Collections.emptyMap());
    }

    // ---- back ----

    public void setBackPosition(UUID uuid, HomePos pos) {
        backs.put(uuid, pos);
        setDirty();
    }

    public HomePos getBackPosition(UUID uuid) {
        return backs.get(uuid);
    }

    // ---- auto-accept TPA ----

    public boolean isAutoAcceptTpa(UUID uuid) {
        return autoAccept.getOrDefault(uuid, false);
    }

    public void setAutoAcceptTpa(UUID uuid, boolean value) {
        autoAccept.put(uuid, value);
        setDirty();
    }
}
