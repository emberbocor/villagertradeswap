package io.github.emberbocor.villagertradeswap.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.villagertradeswap.client.RerollScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Inject(method = "hasClickedOutside", at = @At("HEAD"), cancellable = true)
    private void villagertradeswap$keepClicksOnRerollButtons(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton,
            CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof RerollScreen screen && screen.villagertradeswap$isOverRerollButton(mouseX, mouseY)) {
            cir.setReturnValue(false);
        }
    }
}
