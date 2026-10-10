package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record RerollInfoPayload(int containerId, int cost) {
    public static final ResourceLocation ID = new ResourceLocation(VillagerTradeSwap.MODID, "reroll_info");

    public static RerollInfoPayload read(FriendlyByteBuf buf) {
        return new RerollInfoPayload(buf.readVarInt(), buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        buf.writeVarInt(cost);
    }
}
