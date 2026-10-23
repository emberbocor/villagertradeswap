package io.github.emberbocor.villagertradeswap.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;

@Mixin(AbstractVillager.class)
public interface AbstractVillagerAccessor {
    @Accessor("tradingPlayer")
    void villagertradeswap$setTradingPlayerField(@Nullable Player player);
}
