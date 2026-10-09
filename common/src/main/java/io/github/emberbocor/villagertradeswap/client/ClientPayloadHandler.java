package io.github.emberbocor.villagertradeswap.client;

import io.github.emberbocor.villagertradeswap.network.RerollInfoPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;

public final class ClientPayloadHandler {
    private ClientPayloadHandler() {
    }

    public static void handleRerollInfo(RerollInfoPayload payload) {
        if (Minecraft.getInstance().screen instanceof MerchantScreen screen && screen.getMenu().containerId == payload.containerId()) {
            ((RerollInfoHolder) screen).villagertradeswap$setRerollCost(payload.cost());
        }
    }
}
