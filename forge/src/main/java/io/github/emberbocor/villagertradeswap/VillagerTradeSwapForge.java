package io.github.emberbocor.villagertradeswap;

import io.github.emberbocor.villagertradeswap.config.ServerConfig;
import io.github.emberbocor.villagertradeswap.network.ForgeNetwork;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(VillagerTradeSwap.MODID)
public class VillagerTradeSwapForge {
    public VillagerTradeSwapForge() {
        ForgeNetwork.register();
        PlayerContainerEvent.Open.BUS.addListener(ForgeNetwork::onContainerOpen);
        ServerAboutToStartEvent.BUS.addListener(event -> ServerConfig.load());
    }
}
