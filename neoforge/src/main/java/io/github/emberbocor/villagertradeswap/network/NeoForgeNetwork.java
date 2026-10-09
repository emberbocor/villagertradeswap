package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.client.ClientPayloadHandler;
import io.github.emberbocor.villagertradeswap.trade.RerollHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NeoForgeNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private NeoForgeNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
                .playToClient(RerollInfoPayload.TYPE, RerollInfoPayload.STREAM_CODEC,
                        (payload, context) -> ClientPayloadHandler.handleRerollInfo(payload))
                .playToServer(RerollTradePayload.TYPE, RerollTradePayload.STREAM_CODEC,
                        (payload, context) -> {
                            if (context.player() instanceof ServerPlayer player) {
                                RerollHandler.handle(player, payload);
                            }
                        });
    }

    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RerollInfoSync.onMenuOpened(player, event.getContainer());
        }
    }
}
