package io.github.emberbocor.villagertradeswap;

import io.github.emberbocor.villagertradeswap.config.ServerConfig;
import io.github.emberbocor.villagertradeswap.network.NeoForgeNetwork;
import io.github.emberbocor.villagertradeswap.platform.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod(VillagerTradeSwap.MODID)
public class VillagerTradeSwapNeoForge {
    public VillagerTradeSwapNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(NeoForgeNetwork::register);
        NeoForge.EVENT_BUS.addListener(NeoForgeNetwork::onContainerOpen);
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> ServerConfig.load(Services.PLATFORM.getConfigDirectory()));
    }
}
