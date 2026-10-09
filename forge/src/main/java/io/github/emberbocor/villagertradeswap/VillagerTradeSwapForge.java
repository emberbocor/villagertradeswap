package io.github.emberbocor.villagertradeswap;

import io.github.emberbocor.villagertradeswap.config.ServerConfig;
import io.github.emberbocor.villagertradeswap.network.ForgeNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(VillagerTradeSwap.MODID)
public class VillagerTradeSwapForge {
    public VillagerTradeSwapForge() {
        ForgeNetwork.register();
        MinecraftForge.EVENT_BUS.addListener(ForgeNetwork::onContainerOpen);
        MinecraftForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> ServerConfig.load());
    }
}
