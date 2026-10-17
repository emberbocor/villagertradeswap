package io.github.emberbocor.villagertradeswap;

import io.github.emberbocor.villagertradeswap.client.ClientPayloadHandler;
import io.github.emberbocor.villagertradeswap.network.FabricNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class VillagerTradeSwapFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(FabricNetwork.Info.TYPE, (info, context) -> ClientPayloadHandler.handleRerollInfo(info.payload()));
    }
}
