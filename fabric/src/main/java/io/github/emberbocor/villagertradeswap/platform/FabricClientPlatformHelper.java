package io.github.emberbocor.villagertradeswap.platform;

import io.github.emberbocor.villagertradeswap.network.FabricNetwork;
import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

public final class FabricClientPlatformHelper implements ClientPlatformHelper {
    @Override
    public void sendRerollRequest(RerollTradePayload payload) {
        if (ClientPlayNetworking.canSend(FabricNetwork.REQUEST)) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            payload.write(buf);
            ClientPlayNetworking.send(FabricNetwork.REQUEST, buf);
        }
    }
}
