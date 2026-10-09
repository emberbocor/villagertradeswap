package io.github.emberbocor.villagertradeswap;

import io.github.emberbocor.villagertradeswap.config.ServerConfig;
import io.github.emberbocor.villagertradeswap.network.RerollInfoPayload;
import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;
import io.github.emberbocor.villagertradeswap.trade.RerollHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class VillagerTradeSwapFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(RerollInfoPayload.TYPE, RerollInfoPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RerollTradePayload.TYPE, RerollTradePayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(RerollTradePayload.TYPE, (payload, context) -> RerollHandler.handle(context.player(), payload));
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ServerConfig.load());
    }
}
