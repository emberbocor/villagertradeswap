package io.github.emberbocor.villagertradeswap.platform;

import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class NeoForgeClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void sendRerollRequest(RerollTradePayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }
}
