package io.github.emberbocor.villagertradeswap;

import io.github.emberbocor.villagertradeswap.client.ClientPayloadHandler;
import io.github.emberbocor.villagertradeswap.network.RerollInfoPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class VillagerTradeSwapFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(RerollInfoPayload.TYPE, (payload, context) -> ClientPayloadHandler.handleRerollInfo(payload));
    }
}
