package io.github.emberbocor.villagertradeswap.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.villagertradeswap.trade.MapTrades;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;

@Mixin(ExplorationMapFunction.class)
public abstract class ExplorationMapFunctionMixin {
    @Shadow
    @Final
    private HolderSet<Structure> destination;
    @Shadow
    @Final
    private Holder<MapDecorationType> mapDecoration;
    @Shadow
    @Final
    private byte zoom;
    @Shadow
    @Final
    private int searchRadius;
    @Shadow
    @Final
    private boolean skipKnownStructures;

    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    private void villagertradeswap$deferMap(ItemStack itemStack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        if (MapTrades.isDeferring() && !itemStack.isEmpty()) {
            cir.setReturnValue(MapTrades.placeholder(itemStack, context,
                    new MapTrades.Search(destination, mapDecoration, zoom, searchRadius, skipKnownStructures)));
        }
    }
}
