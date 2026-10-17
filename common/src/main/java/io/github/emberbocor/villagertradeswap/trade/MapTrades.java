package io.github.emberbocor.villagertradeswap.trade;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradeswap.mixin.TreasureMapForEmeraldsAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

final class MapTrades {
    private static final int SEARCH_RADIUS = 100;
    private static final TradeKind KIND = new TradeKind(Items.EMERALD, Items.COMPASS, Items.FILLED_MAP);
    private static final Map<ServerLevel, Map<SearchKey, Optional<BlockPos>>> STRUCTURE_CACHE = new WeakHashMap<>();

    private MapTrades() {
    }

    static boolean isMapTrade(VillagerTrades.ItemListing listing) {
        return listing.getClass() == VillagerTrades.TreasureMapForEmeralds.class;
    }

    static boolean isMap(ItemStack stack) {
        return stack.is(Items.FILLED_MAP);
    }

    static Destination destination(ItemStack map) {
        Set<Holder<MapDecorationType>> decorations = map.getOrDefault(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY).decorations().values().stream()
                .map(MapDecorations.Entry::type)
                .collect(Collectors.toSet());
        return new Destination(map.get(DataComponents.ITEM_NAME), decorations);
    }

    @Nullable
    static MerchantOffer createOffer(Villager villager, VillagerTrades.ItemListing listing, Predicate<TradeIdentity> allowed) {
        TreasureMapForEmeraldsAccessor map = (TreasureMapForEmeraldsAccessor) listing;
        Component name = Component.translatable(map.villagertradeswap$getDisplayName());
        Destination destination = new Destination(name, Set.of(map.villagertradeswap$getDestinationType()));
        if (!allowed.test(new TradeIdentity(KIND, destination)) || !(villager.level() instanceof ServerLevel level)) {
            return null;
        }
        BlockPos target = locate(level, map.villagertradeswap$getDestination(), villager.blockPosition());
        if (target == null) {
            return null;
        }
        ItemStack stack = MapItem.create(level, target.getX(), target.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(level, stack);
        MapItemSavedData.addTargetDecoration(stack, target, "+", map.villagertradeswap$getDestinationType());
        stack.set(DataComponents.ITEM_NAME, name);
        return new MerchantOffer(new ItemCost(Items.EMERALD, map.villagertradeswap$getEmeraldCost()), Optional.of(new ItemCost(Items.COMPASS)),
                stack, map.villagertradeswap$getMaxUses(), map.villagertradeswap$getVillagerXp(), 0.2F);
    }

    @Nullable
    private static BlockPos locate(ServerLevel level, TagKey<Structure> destination, BlockPos origin) {
        return STRUCTURE_CACHE.computeIfAbsent(level, key -> new HashMap<>())
                .computeIfAbsent(new SearchKey(destination, new ChunkPos(origin)),
                        key -> Optional.ofNullable(level.findNearestMapStructure(destination, origin, SEARCH_RADIUS, true)))
                .orElse(null);
    }

    record Destination(@Nullable Component name, Set<Holder<MapDecorationType>> decorations) {
    }

    private record SearchKey(TagKey<Structure> destination, ChunkPos chunk) {
    }
}
