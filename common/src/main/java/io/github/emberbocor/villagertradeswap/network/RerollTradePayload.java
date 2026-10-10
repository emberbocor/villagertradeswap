package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record RerollTradePayload(int containerId, int offerIndex) {
    public static final ResourceLocation ID = new ResourceLocation(VillagerTradeSwap.MODID, "reroll_trade");

    public static RerollTradePayload read(FriendlyByteBuf buf) {
        return new RerollTradePayload(buf.readVarInt(), buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        buf.writeVarInt(offerIndex);
    }
}
