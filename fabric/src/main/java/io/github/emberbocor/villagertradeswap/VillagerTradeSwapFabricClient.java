package io.github.emberbocor.villagertradeswap;

import io.github.emberbocor.villagertradeswap.client.ClientPayloadHandler;
import io.github.emberbocor.villagertradeswap.network.FabricNetwork;
import io.github.emberbocor.villagertradeswap.network.RerollInfoPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class VillagerTradeSwapFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(FabricNetwork.INFO, (client, handler, buf, responseSender) -> {
            RerollInfoPayload payload = RerollInfoPayload.read(buf);
            client.execute(() -> ClientPayloadHandler.handleRerollInfo(payload));
        });
    }
}
