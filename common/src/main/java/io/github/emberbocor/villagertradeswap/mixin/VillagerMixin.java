package io.github.emberbocor.villagertradeswap.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradeswap.trade.OfferLevelStorage;
import io.github.emberbocor.villagertradeswap.trade.OfferLevelTracker;
import io.github.emberbocor.villagertradeswap.trade.OfferLevelsHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.level.Level;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements OfferLevelsHolder {
    @Unique
    private final OfferLevelTracker villagertradeswap$offerLevels = new OfferLevelTracker();

    private VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract VillagerData getVillagerData();

    @Override
    @Nullable
    public int[] villagertradeswap$getOfferLevels() {
        return offers != null ? villagertradeswap$offerLevels.resolve(offers, getVillagerData().getLevel()) : null;
    }

    @Override
    public void villagertradeswap$setOfferLevels(@Nullable int[] levels) {
        villagertradeswap$offerLevels.load(levels, offers);
    }

    @Inject(method = {"updateTrades", "increaseMerchantCareer"}, at = @At("HEAD"))
    private void villagertradeswap$resolveExistingOffers(CallbackInfo ci) {
        villagertradeswap$getOfferLevels();
    }

    @Inject(method = {"updateTrades", "increaseMerchantCareer"}, at = @At("TAIL"))
    private void villagertradeswap$recordNewOffers(CallbackInfo ci) {
        if (offers != null) {
            villagertradeswap$offerLevels.assignUnknown(offers, getVillagerData().getLevel());
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void villagertradeswap$saveOfferLevels(CompoundTag tag, CallbackInfo ci) {
        OfferLevelStorage.save(tag, villagertradeswap$getOfferLevels());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void villagertradeswap$loadOfferLevels(CompoundTag tag, CallbackInfo ci) {
        villagertradeswap$setOfferLevels(OfferLevelStorage.load(tag));
    }
}
