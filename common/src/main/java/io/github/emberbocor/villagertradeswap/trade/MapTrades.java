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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
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

    public static ItemStack placeholder(Search search) {
        CompoundTag marker = new CompoundTag();
        marker.put(MARKER_KEY, Search.CODEC.encodeStart(NbtOps.INSTANCE, search).getOrThrow());
        ItemStack placeholder = new ItemStack(Items.FILLED_MAP);
        placeholder.set(DataComponents.MAP_ID, PLACEHOLDER_MAP_ID);
        placeholder.set(DataComponents.CUSTOM_DATA, CustomData.of(marker));
        return placeholder;
    }

    static boolean isMap(ItemStack stack) {
        return stack.is(Items.FILLED_MAP);
    }

    static Destination destination(ItemStack map) {
        Search search = search(map);
        Set<Holder<MapDecorationType>> decorations = search != null
                ? Set.of(search.decoration())
                : map.getOrDefault(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY).decorations().values().stream()
                        .map(MapDecorations.Entry::type)
                        .collect(Collectors.toSet());
        return new Destination(map.get(DataComponents.ITEM_NAME), decorations);
    }

    @Nullable
    static MerchantOffer resolve(ServerLevel level, Villager villager, MerchantOffer offer) {
        ItemStack placeholder = offer.getResult();
        Search search = search(placeholder);
        if (search == null) {
            return offer;
        }
        BlockPos target = locate(level, search, villager.blockPosition());
        if (target == null) {
            return null;
        }
        ItemStack map = MapItem.create(level, target.getX(), target.getZ(), search.zoom(), true, true);
        MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, target, "+", search.decoration());
        map.applyComponents(placeholder.getComponentsPatch().forget(type -> type == DataComponents.MAP_ID || type == DataComponents.CUSTOM_DATA));
        CompoundTag customData = placeholder.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        customData.remove(MARKER_KEY);
        if (!customData.isEmpty()) {
            map.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
        }
        return new MerchantOffer(offer.getItemCostA(), offer.getItemCostB(), map, offer.getUses(), offer.getMaxUses(), offer.getXp(), offer.getPriceMultiplier());
    }

    @Nullable
    private static Search search(ItemStack stack) {
        Tag marker = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().get(MARKER_KEY);
        return marker != null ? Search.CODEC.parse(NbtOps.INSTANCE, marker).result().orElse(null) : null;
    }

    @Nullable
    private static BlockPos locate(ServerLevel level, Search search, BlockPos origin) {
        return STRUCTURE_CACHE.computeIfAbsent(level, key -> new HashMap<>())
                .computeIfAbsent(new SearchKey(search.destination(), search.searchRadius(), search.skipKnownStructures(), ChunkPos.containing(origin)),
                        key -> Optional.ofNullable(level.findNearestMapStructure(search.destination(), origin, search.searchRadius(), search.skipKnownStructures())))
                .orElse(null);
    }

    public record Search(TagKey<Structure> destination, Holder<MapDecorationType> decoration, byte zoom, int searchRadius, boolean skipKnownStructures) {
        private static final Codec<Search> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TagKey.codec(Registries.STRUCTURE).fieldOf("destination").forGetter(Search::destination),
                MapDecorationType.CODEC.fieldOf("decoration").forGetter(Search::decoration),
                Codec.BYTE.fieldOf("zoom").forGetter(Search::zoom),
                Codec.INT.fieldOf("search_radius").forGetter(Search::searchRadius),
                Codec.BOOL.fieldOf("skip_existing_chunks").forGetter(Search::skipKnownStructures)
        ).apply(instance, Search::new));
    }

    record Destination(@Nullable Component name, Set<Holder<MapDecorationType>> decorations) {
    }

    private record SearchKey(TagKey<Structure> destination, int searchRadius, boolean skipKnownStructures, ChunkPos chunk) {
    }
}
