package io.github.emberbocor.villagertradeswap.trade;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradeswap.mixin.TreasureMapForEmeraldsAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
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
        Set<MapDecoration.Type> decorations = EnumSet.noneOf(MapDecoration.Type.class);
        CompoundTag tag = map.getTag();
        if (tag != null) {
            ListTag list = tag.getList("Decorations", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                decorations.add(MapDecoration.Type.byIcon(list.getCompound(i).getByte("type")));
            }
        }
        return new Destination(map.hasCustomHoverName() ? map.getHoverName() : null, decorations);
    }

    @Nullable
    static MerchantOffer createOffer(Villager villager, VillagerTrades.ItemListing listing, Predicate<TradeIdentity> allowed) {
        TreasureMapForEmeraldsAccessor map = (TreasureMapForEmeraldsAccessor) listing;
        Component name = Component.translatable(map.villagertradeswap$getDisplayName());
        Destination destination = new Destination(name, EnumSet.of(map.villagertradeswap$getDestinationType()));
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
        stack.setHoverName(name);
        return new MerchantOffer(new ItemStack(Items.EMERALD, map.villagertradeswap$getEmeraldCost()), new ItemStack(Items.COMPASS),
                stack, map.villagertradeswap$getMaxUses(), map.villagertradeswap$getVillagerXp(), 0.2F);
    }

    @Nullable
    private static BlockPos locate(ServerLevel level, TagKey<Structure> destination, BlockPos origin) {
        return STRUCTURE_CACHE.computeIfAbsent(level, key -> new HashMap<>())
                .computeIfAbsent(new SearchKey(destination, new ChunkPos(origin)),
                        key -> Optional.ofNullable(level.findNearestMapStructure(destination, origin, SEARCH_RADIUS, true)))
                .orElse(null);
    }

    record Destination(@Nullable Component name, Set<MapDecoration.Type> decorations) {
    }

    private record SearchKey(TagKey<Structure> destination, ChunkPos chunk) {
    }
}
