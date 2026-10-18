package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import io.github.emberbocor.villagertradeswap.trade.RerollHandler;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public final class FabricNetwork {
    private FabricNetwork() {
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(Info.TYPE, Info.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(Request.TYPE, Request.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(Request.TYPE, (request, context) -> RerollHandler.handle(context.player(), request.payload()));
    }

    // Fabric does not compare channel versions, so the version is part of the channel id.
    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> versionedType(CustomPacketPayload.Type<?> type) {
        return new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(VillagerTradeSwap.MODID,
                type.id().getPath() + "_v" + VillagerTradeSwap.NETWORK_VERSION));
    }

    public record Info(RerollInfoPayload payload) implements CustomPacketPayload {
        public static final Type<Info> TYPE = versionedType(RerollInfoPayload.TYPE);
        static final StreamCodec<RegistryFriendlyByteBuf, Info> STREAM_CODEC = RerollInfoPayload.STREAM_CODEC.map(Info::new, Info::payload);

        @Override
        public Type<Info> type() {
            return TYPE;
        }
    }

    public record Request(RerollTradePayload payload) implements CustomPacketPayload {
        public static final Type<Request> TYPE = versionedType(RerollTradePayload.TYPE);
        static final StreamCodec<ByteBuf, Request> STREAM_CODEC = RerollTradePayload.STREAM_CODEC.map(Request::new, Request::payload);

        @Override
        public Type<Request> type() {
            return TYPE;
        }
    }
}
