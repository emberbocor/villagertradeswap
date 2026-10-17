package io.github.emberbocor.villagertradeswap;

import io.github.emberbocor.villagertradeswap.config.ServerConfig;
import io.github.emberbocor.villagertradeswap.network.FabricNetwork;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class VillagerTradeSwapFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricNetwork.register();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ServerConfig.load());
    }
}
