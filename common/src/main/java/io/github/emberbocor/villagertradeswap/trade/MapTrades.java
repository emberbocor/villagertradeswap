package io.github.emberbocor.villagertradeswap.trade;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;

public final class MapTrades {
    private static final String MARKER_KEY = VillagerTradeSwap.MODID + ":deferred_map";
    private static final MapId PLACEHOLDER_MAP_ID = new MapId(-1);
    private static final Map<ServerLevel, Map<SearchKey, Optional<BlockPos>>> STRUCTURE_CACHE = new WeakHashMap<>();

    private static boolean deferring;

    private MapTrades() {
    }

    static <T> T deferMaps(Supplier<T> generation) {
        deferring = true;
        try {
            return generation.get();
        } finally {
            deferring = false;
        }
    }

    public static boolean isDeferring() {
        return deferring;
    }

    public static ItemStack placeholder(ItemStack map, LootContext context, Search search) {
        Tag marker = Search.CODEC.encodeStart(context.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE), search).getOrThrow();
        map.set(DataComponents.MAP_ID, PLACEHOLDER_MAP_ID);
        CustomData.update(DataComponents.CUSTOM_DATA, map, tag -> tag.put(MARKER_KEY, marker));
        return map;
    }

    static boolean isMap(ItemStack stack) {
        if (!(stack.getItem() instanceof MapItem)) {
            return false;
        }
        return !stack.is(Items.FILLED_MAP) || isPlaceholder(stack) || stack.has(DataComponents.CUSTOM_NAME)
                || stack.hasNonDefault(DataComponents.ITEM_NAME) && stack.has(DataComponents.ITEM_NAME);
    }

    static Destination destination(ItemStack map, HolderLookup.Provider registries) {
        Search search = search(map, registries);
        Set<Holder<MapDecorationType>> decorations = search != null
                ? Set.of(search.decoration())
                : map.getOrDefault(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY).decorations().values().stream()
                        .map(MapDecorations.Entry::type)
                        .collect(Collectors.toSet());
        return new Destination(map.getHoverName(), decorations);
    }

    @Nullable
    static MerchantOffer resolve(ServerLevel level, Villager villager, MerchantOffer offer) {
        Search search = search(offer.getResult(), level.registryAccess());
        if (search == null) {
            return offer;
        }
        BlockPos target = locate(level, search, villager.blockPosition());
        if (target == null) {
            return null;
        }
        ItemStack map = withoutPlaceholder(offer.getResult());
        MapItem.applyNewSavedData(level, map, target.getX(), target.getZ(), search.zoom(), true, true);
        MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, target, "+", search.decoration());
        return new MerchantOffer(offer.getItemCostA(), offer.getItemCostB(), map, offer.getUses(), offer.getMaxUses(), offer.getXp(), offer.getPriceMultiplier());
    }

    private static boolean isPlaceholder(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains(MARKER_KEY);
    }

    private static ItemStack withoutPlaceholder(ItemStack placeholder) {
        ItemStack map = placeholder.copy();
        map.remove(DataComponents.MAP_ID);
        CustomData customData = map.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update(tag -> tag.remove(MARKER_KEY));
        if (customData.isEmpty()) {
            map.remove(DataComponents.CUSTOM_DATA);
        } else {
            map.set(DataComponents.CUSTOM_DATA, customData);
        }
        return map;
    }

    @Nullable
    private static Search search(ItemStack stack, HolderLookup.Provider registries) {
        Tag marker = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().get(MARKER_KEY);
        return marker != null ? Search.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), marker).result().orElse(null) : null;
    }

    @Nullable
    private static BlockPos locate(ServerLevel level, Search search, BlockPos origin) {
        return STRUCTURE_CACHE.computeIfAbsent(level, key -> new HashMap<>())
                .computeIfAbsent(new SearchKey(search.destination(), search.searchRadius(), search.skipKnownStructures(), ChunkPos.containing(origin)),
                        key -> Optional.ofNullable(level.findNearestMapStructure(search.destination(), origin, search.searchRadius(), search.skipKnownStructures())))
                .orElse(null);
    }

    public record Search(HolderSet<Structure> destination, Holder<MapDecorationType> decoration, byte zoom, int searchRadius, boolean skipKnownStructures) {
        private static final Codec<Search> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RegistryCodecs.holderSet(Registries.STRUCTURE).fieldOf("destination").forGetter(Search::destination),
                MapDecorationType.CODEC.fieldOf("decoration").forGetter(Search::decoration),
                Codec.BYTE.fieldOf("zoom").forGetter(Search::zoom),
                Codec.INT.fieldOf("search_radius").forGetter(Search::searchRadius),
                Codec.BOOL.fieldOf("skip_existing_chunks").forGetter(Search::skipKnownStructures)
        ).apply(instance, Search::new));
    }

    record Destination(Component name, Set<Holder<MapDecorationType>> decorations) {
    }

    private record SearchKey(HolderSet<Structure> destination, int searchRadius, boolean skipKnownStructures, ChunkPos chunk) {
    }
}
