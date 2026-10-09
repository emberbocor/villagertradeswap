package io.github.emberbocor.villagertradeswap.platform;

import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;
import net.neoforged.neoforge.network.PacketDistributor;

public final class NeoForgeClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void sendRerollRequest(RerollTradePayload payload) {
        PacketDistributor.sendToServer(payload);
    }
}
