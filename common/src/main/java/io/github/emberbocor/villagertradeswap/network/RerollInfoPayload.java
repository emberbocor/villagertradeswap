package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RerollInfoPayload(int containerId, int cost) implements CustomPacketPayload {
    public static final Type<RerollInfoPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(VillagerTradeSwap.MODID, "reroll_info"));
    public static final StreamCodec<ByteBuf, RerollInfoPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RerollInfoPayload::containerId,
            ByteBufCodecs.VAR_INT, RerollInfoPayload::cost,
            RerollInfoPayload::new);

    @Override
    public Type<RerollInfoPayload> type() {
        return TYPE;
    }
}
