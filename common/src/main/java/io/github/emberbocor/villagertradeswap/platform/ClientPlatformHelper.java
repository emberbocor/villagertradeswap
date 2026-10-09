package io.github.emberbocor.villagertradeswap.platform;

import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;

public interface ClientPlatformHelper {
    void sendRerollRequest(RerollTradePayload payload);
}
