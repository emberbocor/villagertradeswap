package io.github.emberbocor.villagertradeswap.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.villagertradeswap.trade.OfferLevelsHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Inject(method = "convertTo", at = @At("RETURN"))
    private void villagertradeswap$copyOfferLevels(EntityType<?> entityType, boolean transferInventory, CallbackInfoReturnable<Mob> cir) {
        if ((Object) this instanceof OfferLevelsHolder from && cir.getReturnValue() instanceof OfferLevelsHolder to) {
            to.villagertradeswap$setOfferLevels(from.villagertradeswap$getOfferLevels());
        }
    }
}
