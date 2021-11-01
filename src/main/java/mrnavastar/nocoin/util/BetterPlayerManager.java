package mrnavastar.nocoin.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static mrnavastar.nocoin.Nocoin.playerManager;
import static mrnavastar.nocoin.Nocoin.userCache;

public class BetterPlayerManager {

    public static String getName(UUID uuid) {
        Optional<GameProfile> gameProfile = userCache.getByUuid(uuid);
        return gameProfile.map(GameProfile::getName).orElse(null);
    }

    public static UUID getUuid(String name) {
        Optional<GameProfile> gameProfile = userCache.findByName(name);
        return gameProfile.map(GameProfile::getId).orElse(null);
    }

    public static ArrayList<ServerPlayerEntity> getOnlinePlayers() {
        return (ArrayList<ServerPlayerEntity>) playerManager.getPlayerList();
    }
}