package io.github.emberbocor.villagertradeswap.mixin;

import java.util.OptionalInt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.emberbocor.villagertradeswap.client.RerollInfoHolder;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> implements RerollInfoHolder {
    @Unique
    private OptionalInt villagertradeswap$rerollCost = OptionalInt.empty();

    private MerchantScreenMixin(MerchantMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public OptionalInt villagertradeswap$getRerollCost() {
        return villagertradeswap$rerollCost;
    }

    @Override
    public void villagertradeswap$setRerollCost(int cost) {
        villagertradeswap$rerollCost = OptionalInt.of(cost);
    }
}
