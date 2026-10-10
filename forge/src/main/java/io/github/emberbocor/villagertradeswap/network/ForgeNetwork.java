package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import io.github.emberbocor.villagertradeswap.client.ClientPayloadHandler;
import io.github.emberbocor.villagertradeswap.trade.RerollHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ForgeNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(VillagerTradeSwap.MODID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private ForgeNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(RerollInfoPayload.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RerollInfoPayload::write)
                .decoder(RerollInfoPayload::read)
                .consumerMainThread((payload, context) -> ClientPayloadHandler.handleRerollInfo(payload))
                .add();
        CHANNEL.messageBuilder(RerollTradePayload.class, 1, NetworkDirection.PLAY_TO_SERVER)
                .encoder(RerollTradePayload::write)
                .decoder(RerollTradePayload::read)
                .consumerMainThread((payload, context) -> {
                    ServerPlayer player = context.get().getSender();
                    if (player != null) {
                        RerollHandler.handle(player, payload);
                    }
                })
                .add();
    }

    public static boolean isPresent(ServerPlayer player) {
        return CHANNEL.isRemotePresent(player.connection.connection);
    }

    public static void sendToPlayer(ServerPlayer player, RerollInfoPayload payload) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
    }

    public static void sendToServer(RerollTradePayload payload) {
        CHANNEL.sendToServer(payload);
    }

    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RerollInfoSync.onMenuOpened(player, event.getContainer());
        }
    }
}
