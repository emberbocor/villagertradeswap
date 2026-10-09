package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import io.github.emberbocor.villagertradeswap.client.ClientPayloadHandler;
import io.github.emberbocor.villagertradeswap.trade.RerollHandler;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;

public final class ForgeNetwork {
    private static final int PROTOCOL_VERSION = 1;

    private static Channel<CustomPacketPayload> channel;

    private ForgeNetwork() {
    }

    public static void register() {
        channel = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(VillagerTradeSwap.MODID, "main"))
                .networkProtocolVersion(PROTOCOL_VERSION)
                .payloadChannel()
                .play()
                .clientbound()
                .addMain(RerollInfoPayload.TYPE, RerollInfoPayload.STREAM_CODEC.cast(),
                        (payload, context) -> ClientPayloadHandler.handleRerollInfo(payload))
                .serverbound()
                .addMain(RerollTradePayload.TYPE, RerollTradePayload.STREAM_CODEC.cast(), (payload, context) -> {
                    ServerPlayer player = context.getSender();
                    if (player != null) {
                        RerollHandler.handle(player, payload);
                    }
                })
                .build();
    }

    public static boolean isPresent(ServerPlayer player) {
        return channel.isRemotePresent(player.connection.getConnection());
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        channel.send(payload, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToServer(CustomPacketPayload payload) {
        channel.send(payload, PacketDistributor.SERVER.noArg());
    }

    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RerollInfoSync.onMenuOpened(player, event.getContainer());
        }
    }
}
