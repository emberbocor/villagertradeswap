package io.github.emberbocor.villagertradeswap.mixin;

import java.util.OptionalInt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.villagertradeswap.network.RerollInfoSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Inject(method = "openMenu", at = @At("RETURN"))
    private void villagertradeswap$sendRerollInfo(MenuProvider menu, CallbackInfoReturnable<OptionalInt> cir) {
        if (cir.getReturnValue().isPresent()) {
            ServerPlayer player = (ServerPlayer) (Object) this;
            RerollInfoSync.onMenuOpened(player, player.containerMenu);
        }
    }
}
