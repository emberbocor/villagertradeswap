package io.github.emberbocor.villagertradeswap.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

@Mixin(VillagerTrades.TreasureMapForEmeralds.class)
public interface TreasureMapForEmeraldsAccessor {
    @Accessor("emeraldCost")
    int villagertradeswap$getEmeraldCost();

    @Accessor("destination")
    TagKey<Structure> villagertradeswap$getDestination();

    @Accessor("displayName")
    String villagertradeswap$getDisplayName();

    @Accessor("destinationType")
    MapDecoration.Type villagertradeswap$getDestinationType();

    @Accessor("maxUses")
    int villagertradeswap$getMaxUses();

    @Accessor("villagerXp")
    int villagertradeswap$getVillagerXp();
}
