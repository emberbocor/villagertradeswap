package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import io.github.emberbocor.villagertradeswap.trade.RerollHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public final class FabricNetwork {
    // Fabric does not compare channel versions, so the version is part of the channel id.
    public static final ResourceLocation INFO = versioned(RerollInfoPayload.ID);
    public static final ResourceLocation REQUEST = versioned(RerollTradePayload.ID);

    private FabricNetwork() {
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(REQUEST, (server, player, handler, buf, responseSender) -> {
            RerollTradePayload payload = RerollTradePayload.read(buf);
            server.execute(() -> RerollHandler.handle(player, payload));
        });
    }

    private static ResourceLocation versioned(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), id.getPath() + "_v" + VillagerTradeSwap.NETWORK_VERSION);
    }
}
