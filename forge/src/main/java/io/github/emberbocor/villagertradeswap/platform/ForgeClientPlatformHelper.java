package io.github.emberbocor.villagertradeswap.platform;

import io.github.emberbocor.villagertradeswap.network.ForgeNetwork;
import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;

public final class ForgeClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void sendRerollRequest(RerollTradePayload payload) {
        ForgeNetwork.sendToServer(payload);
    }
}
