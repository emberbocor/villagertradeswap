package io.github.emberbocor.villagertradeswap.platform;

import java.nio.file.Path;

import io.github.emberbocor.villagertradeswap.network.RerollInfoPayload;
import net.minecraft.server.level.ServerPlayer;

public interface PlatformHelper {
    Path getConfigDirectory();

    boolean canSendRerollInfo(ServerPlayer player);

    void sendRerollInfo(ServerPlayer player, RerollInfoPayload payload);
}
