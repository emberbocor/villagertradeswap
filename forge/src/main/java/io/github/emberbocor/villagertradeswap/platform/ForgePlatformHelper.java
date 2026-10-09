package io.github.emberbocor.villagertradeswap.platform;

import java.nio.file.Path;

import io.github.emberbocor.villagertradeswap.network.ForgeNetwork;
import io.github.emberbocor.villagertradeswap.network.RerollInfoPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;

public final class ForgePlatformHelper implements PlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean canSendRerollInfo(ServerPlayer player) {
        return ForgeNetwork.isPresent(player);
    }

    @Override
    public void sendRerollInfo(ServerPlayer player, RerollInfoPayload payload) {
        ForgeNetwork.sendToPlayer(player, payload);
    }
}
