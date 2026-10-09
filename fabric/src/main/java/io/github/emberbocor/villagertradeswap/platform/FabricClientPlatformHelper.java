package io.github.emberbocor.villagertradeswap.platform;

import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class FabricClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void sendRerollRequest(RerollTradePayload payload) {
        if (ClientPlayNetworking.canSend(RerollTradePayload.TYPE)) {
            ClientPlayNetworking.send(payload);
        }
    }
}
