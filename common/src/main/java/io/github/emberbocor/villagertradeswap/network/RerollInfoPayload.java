package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record RerollInfoPayload(int containerId, Item currency, int cost) {
    public static final ResourceLocation ID = new ResourceLocation(VillagerTradeSwap.MODID, "reroll_info");

    public static RerollInfoPayload read(FriendlyByteBuf buf) {
        return new RerollInfoPayload(buf.readVarInt(), Item.byId(buf.readVarInt()), buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        buf.writeVarInt(Item.getId(currency));
        buf.writeVarInt(cost);
    }
}
