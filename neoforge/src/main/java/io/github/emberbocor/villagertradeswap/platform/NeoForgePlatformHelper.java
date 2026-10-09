package io.github.emberbocor.villagertradeswap.platform;

import java.nio.file.Path;

import io.github.emberbocor.villagertradeswap.network.RerollInfoPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;

public final class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean canSendRerollInfo(ServerPlayer player) {
        return player.connection.hasChannel(RerollInfoPayload.TYPE);
    }

    @Override
    public void sendRerollInfo(ServerPlayer player, RerollInfoPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
