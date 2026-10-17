package io.github.emberbocor.villagertradeswap.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradeswap.trade.OfferLevelStorage;
import io.github.emberbocor.villagertradeswap.trade.OfferLevelsHolder;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    private void villagertradeswap$saveOfferLevels(ValueOutput output, CallbackInfo ci) {
        OfferLevelStorage.save(output, villagertradeswap$offerLevels);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void villagertradeswap$loadOfferLevels(ValueInput input, CallbackInfo ci) {
        villagertradeswap$offerLevels = OfferLevelStorage.load(input);
    }
}
