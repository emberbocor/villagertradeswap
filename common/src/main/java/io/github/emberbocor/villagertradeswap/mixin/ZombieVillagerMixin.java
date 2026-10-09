package io.github.emberbocor.villagertradeswap.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradeswap.trade.OfferLevelStorage;
import io.github.emberbocor.villagertradeswap.trade.OfferLevelsHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.ZombieVillager;

@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerMixin implements OfferLevelsHolder {
    @Unique
    @Nullable
    private int[] villagertradeswap$offerLevels;

    @Override
    @Nullable
    public int[] villagertradeswap$getOfferLevels() {
        return villagertradeswap$offerLevels;
    }

    @Override
    public void villagertradeswap$setOfferLevels(@Nullable int[] levels) {
        villagertradeswap$offerLevels = levels;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void villagertradeswap$saveOfferLevels(CompoundTag tag, CallbackInfo ci) {
        OfferLevelStorage.save(tag, villagertradeswap$offerLevels);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void villagertradeswap$loadOfferLevels(CompoundTag tag, CallbackInfo ci) {
        villagertradeswap$offerLevels = OfferLevelStorage.load(tag);
    }
}
