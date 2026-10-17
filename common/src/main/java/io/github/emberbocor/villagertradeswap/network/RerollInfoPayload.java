package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record RerollInfoPayload(int containerId, Item currency, int cost) implements CustomPacketPayload {
    public static final Type<RerollInfoPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(VillagerTradeSwap.MODID, "reroll_info"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RerollInfoPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RerollInfoPayload::containerId,
            ByteBufCodecs.registry(Registries.ITEM), RerollInfoPayload::currency,
            ByteBufCodecs.VAR_INT, RerollInfoPayload::cost,
            RerollInfoPayload::new);

    @Override
    public Type<RerollInfoPayload> type() {
        return TYPE;
    }
}
