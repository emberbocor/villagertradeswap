package io.github.emberbocor.villagertradeswap;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(VillagerTradeSwap.MODID)
public class VillagerTradeSwap {
    public static final String MODID = "villagertradeswap";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VillagerTradeSwap(IEventBus modEventBus, ModContainer modContainer) {
    }
}
