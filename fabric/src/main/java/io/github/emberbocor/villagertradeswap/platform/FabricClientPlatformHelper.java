package io.github.emberbocor.villagertradeswap.platform;

import io.github.emberbocor.villagertradeswap.network.FabricNetwork;
import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class FabricClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void sendRerollRequest(RerollTradePayload payload) {
        if (ClientPlayNetworking.canSend(FabricNetwork.Request.TYPE)) {
            ClientPlayNetworking.send(new FabricNetwork.Request(payload));
        }
    }
}
