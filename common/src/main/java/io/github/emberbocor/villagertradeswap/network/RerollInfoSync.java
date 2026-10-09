package io.github.emberbocor.villagertradeswap.network;

import io.github.emberbocor.villagertradeswap.config.ServerConfig;
import io.github.emberbocor.villagertradeswap.mixin.MerchantMenuAccessor;
import io.github.emberbocor.villagertradeswap.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;

public final class RerollInfoSync {
    private RerollInfoSync() {
    }

    public static void onMenuOpened(ServerPlayer player, AbstractContainerMenu container) {
        if (container instanceof MerchantMenu menu
                && ((MerchantMenuAccessor) menu).villagertradeswap$getTrader() instanceof Villager
                && Services.PLATFORM.canSendRerollInfo(player)) {
            Services.PLATFORM.sendRerollInfo(player, new RerollInfoPayload(menu.containerId, ServerConfig.rerollCost()));
        }
    }
}
