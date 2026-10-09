package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RerollTradePayload(int containerId, int offerIndex) implements CustomPacketPayload {
    public static final Type<RerollTradePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(VillagerTradeSwap.MODID, "reroll_trade"));
    public static final StreamCodec<ByteBuf, RerollTradePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RerollTradePayload::containerId,
            ByteBufCodecs.VAR_INT, RerollTradePayload::offerIndex,
            RerollTradePayload::new);

    @Override
    public Type<RerollTradePayload> type() {
        return TYPE;
    }
}
