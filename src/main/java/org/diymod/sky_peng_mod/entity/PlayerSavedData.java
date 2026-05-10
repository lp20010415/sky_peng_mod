package org.diymod.sky_peng_mod.entity;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.UUID;

/**
 * Dimension-independent SavedData for storing per-player mod data.
 * - In server worlds: stored in the Overworld's data storage (so it's shared across dimensions)
 * - Fallback (no server available): in-memory singleton (useful in some client-only or test contexts)
 */
public class PlayerSavedData extends SavedData {
    private static final String DATA_NAME = "diymod_playerdata";

    private CompoundTag players = new CompoundTag();

    private static final Codec<PlayerSavedData> CODEC = CompoundTag.CODEC.xmap(
            PlayerSavedData::new,
            PlayerSavedData::getPlayerData
    );

    private static final SavedDataType<PlayerSavedData> TYPE = new SavedDataType<>(
            DATA_NAME, // The unique name for this saved data.
            PlayerSavedData::new,
            CODEC,
            null
    );

    public PlayerSavedData() {}

    public PlayerSavedData(CompoundTag players) {
        this.players = players;
    }

    public static PlayerSavedData get(Level level) {
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = level.getServer().getLevel(ServerLevel.OVERWORLD);
            if (serverLevel != null) {
                return serverLevel.getDataStorage().computeIfAbsent(TYPE);
            }
        }
        // fallback in-memory
        return new PlayerSavedData();
    }

    public CompoundTag getPlayerData() {
        return players;
    }

    public CompoundTag getPlayerDataByUUID(UUID uuid) {
        String key = uuid.toString();
        return players.contains(key) ? players.getCompound(key).orElse(null) : new CompoundTag();
    }

    public void putPlayerData(UUID uuid, CompoundTag tag) {
        players.put(uuid.toString(), tag);
        setDirty();
    }

    public void setDaGuoEaten(UUID uuid, int count) {
        CompoundTag t = getPlayerDataByUUID(uuid);
        t.putInt("da_guo_eaten", count);
        putPlayerData(uuid, t);
    }

    public int addDaGuoEaten(UUID uuid) {
        CompoundTag t = getPlayerDataByUUID(uuid);
        int val = t.contains("da_guo_eaten") ? t.getIntOr("da_guo_eaten", 0) : 0;
        val++;
        t.putInt("da_guo_eaten", val);
        putPlayerData(uuid, t);
        return val;
    }

    public void setOralCancerRemaining(UUID uuid, String effect, int ticks) {
        CompoundTag t = getPlayerDataByUUID(uuid);
        if (ticks > 0) {
            t.putString("effect", effect);
            t.putInt("oral_cancer_remaining", ticks);
        } else {
            t.putString("effect", effect);
            t.remove("oral_cancer_remaining");
        }
        putPlayerData(uuid, t);
    }

    public CompoundTag getOralCancerRemaining(UUID uuid) {
        CompoundTag t = getPlayerDataByUUID(uuid);
        return t.contains("oral_cancer_remaining") ? t : null;
    }

    public void removeOralCancerRemaining(UUID uuid) {
        CompoundTag t = getPlayerDataByUUID(uuid);
        t.remove("oral_cancer_remaining");
        t.remove("effect");
        putPlayerData(uuid, t);
    }

}
