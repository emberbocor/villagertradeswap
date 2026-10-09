package io.github.emberbocor.villagertradeswap.platform;

import java.nio.file.Path;

import io.github.emberbocor.villagertradeswap.network.RerollInfoPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

public final class FabricPlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean canSendRerollInfo(ServerPlayer player) {
        return ServerPlayNetworking.canSend(player, RerollInfoPayload.TYPE);
    }

    @Override
    public void sendRerollInfo(ServerPlayer player, RerollInfoPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
