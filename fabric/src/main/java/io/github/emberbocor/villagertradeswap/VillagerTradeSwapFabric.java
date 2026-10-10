package io.github.emberbocor.villagertradeswap;

import io.github.emberbocor.villagertradeswap.config.ServerConfig;
import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;
import io.github.emberbocor.villagertradeswap.trade.RerollHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class VillagerTradeSwapFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(RerollTradePayload.ID, (server, player, handler, buf, responseSender) -> {
            RerollTradePayload payload = RerollTradePayload.read(buf);
            server.execute(() -> RerollHandler.handle(player, payload));
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ServerConfig.load());
    }
}
