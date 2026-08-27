package com.xiaozhi.tpa.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaozhi.tpa.util.HomePos;
import com.xiaozhi.tpa.util.Reference;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Persistent per-player data (homes, back position, auto-accept flag) serialized to a JSON
 * file in the world folder via Gson. Loaded on server start, written whenever it changes.
 */
public final class PlayerData {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static PlayerData INSTANCE;
    private static Path FILE;

    /** UUID string -> entry. */
    public Map<String, Entry> players = new HashMap<>();

    private PlayerData() {}

    public static PlayerData get() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerData();
        }
        return INSTANCE;
    }

    public static void load(MinecraftServer server) {
        FILE = server.getSavePath(WorldSavePath.ROOT).resolve(Reference.DATA_FILE);
        if (Files.exists(FILE)) {
            try {
                PlayerData loaded = GSON.fromJson(Files.readString(FILE), PlayerData.class);
                INSTANCE = loaded != null ? loaded : new PlayerData();
            } catch (IOException | RuntimeException e) {
                INSTANCE = new PlayerData();
            }
        } else {
            INSTANCE = new PlayerData();
        }
    }

    public Entry getEntry(UUID uuid) {
        return players.computeIfAbsent(uuid.toString(), k -> new Entry());
    }

    public void saveLater() {
        if (FILE == null) {
            return;
        }
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException ignored) {
        }
    }

    /** Per-player record. */
    public static class Entry {
        /** dimension string -> home name -> position. */
        public Map<String, Map<String, HomePos>> homes = new HashMap<>();
        public HomePos back;
        public boolean autoAccept;
    }

    // ---- cross-dimension home helpers (do not create an empty entry on read) ----

    private Entry entryIfPresent(UUID uuid) {
        return players.get(uuid.toString());
    }

    /** Look up a home by name across ALL dimensions (so /home works cross-dimension). */
    public HomePos getHomeAnywhere(UUID uuid, String name) {
        Entry e = entryIfPresent(uuid);
        if (e == null) {
            return null;
        }
        for (Map<String, HomePos> names : e.homes.values()) {
            HomePos pos = names.get(name);
            if (pos != null) {
                return pos;
            }
        }
        return null;
    }

    /** All home names across every dimension (for /home suggestions and /listhome). */
    public Collection<String> getAllHomeNames(UUID uuid) {
        Entry e = entryIfPresent(uuid);
        if (e == null) {
            return Collections.emptyList();
        }
        Set<String> names = new HashSet<>();
        for (Map<String, HomePos> list : e.homes.values()) {
            names.addAll(list.keySet());
        }
        return names;
    }

    /** All homes grouped by dimension (for /listhome). */
    public Map<String, Map<String, HomePos>> getAllHomes(UUID uuid) {
        Entry e = entryIfPresent(uuid);
        return e == null ? Collections.emptyMap() : e.homes;
    }

    /** Remove a named home from every dimension; returns true if it existed anywhere. */
    public boolean removeHomeAnywhere(UUID uuid, String name) {
        Entry e = entryIfPresent(uuid);
        if (e == null) {
            return false;
        }
        boolean removed = false;
        Iterator<Map.Entry<String, Map<String, HomePos>>> it = e.homes.entrySet().iterator();
        while (it.hasNext()) {
            Map<String, HomePos> names = it.next().getValue();
            if (names.remove(name) != null) {
                removed = true;
                if (names.isEmpty()) {
                    it.remove();
                }
            }
        }
        if (removed) {
            saveLater();
        }
        return removed;
    }
}
