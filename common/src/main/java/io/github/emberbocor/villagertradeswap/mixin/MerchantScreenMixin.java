package io.github.emberbocor.villagertradeswap.mixin;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradeswap.client.RerollButton;
import io.github.emberbocor.villagertradeswap.client.RerollPrice;
import io.github.emberbocor.villagertradeswap.client.RerollScreen;
import io.github.emberbocor.villagertradeswap.client.RerollToggle;
import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;
import io.github.emberbocor.villagertradeswap.platform.ClientServices;
import io.github.emberbocor.villagertradeswap.trade.Currency;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.Item;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> implements RerollScreen {
    @Unique
    private static final int VISIBLE_ROWS = 7;
    @Unique
    private static final int FIRST_ROW_Y = 18;
    @Unique
    private static final int ROW_HEIGHT = 20;
    @Unique
    private static final int BUTTON_GAP = 2;
    @Unique
    private static final int TOGGLE_Y = 4;
    @Unique
    private static final int TOGGLE_MAX_X = 86;

    @Shadow
    @Final
    private static Component TRADES_LABEL;
    @Shadow
    int scrollOff;

    @Unique
    @Nullable
    private RerollPrice villagertradeswap$rerollPrice;
    @Unique
    private final List<RerollButton> villagertradeswap$rerollButtons = new ArrayList<>();
    @Unique
    @Nullable
    private RerollToggle villagertradeswap$toggle;

    private MerchantScreenMixin(MerchantMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void villagertradeswap$setRerollPrice(Item currency, int cost) {
        villagertradeswap$rerollPrice = RerollPrice.of(currency, cost);
        villagertradeswap$updateRerollWidgets();
    }

    @Override
    public boolean villagertradeswap$isOverRerollButton(double mouseX, double mouseY) {
        return villagertradeswap$rerollButtons.stream().anyMatch(button -> button.covers(mouseX, mouseY));
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void villagertradeswap$addRerollWidgets(CallbackInfo ci) {
        villagertradeswap$rerollButtons.clear();
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            int buttonRow = row;
            villagertradeswap$rerollButtons.add(addRenderableWidget(new RerollButton(() -> villagertradeswap$requestReroll(buttonRow))));
        }
        villagertradeswap$toggle = addRenderableWidget(new RerollToggle());
        villagertradeswap$updateRerollWidgets();
    }

    @Inject(method = "renderContents", at = @At("HEAD"))
    private void villagertradeswap$updateRerollWidgetsOnRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        villagertradeswap$updateRerollWidgets();
    }

    @Unique
    private void villagertradeswap$updateRerollWidgets() {
        RerollToggle toggle = villagertradeswap$toggle;
        if (toggle == null || minecraft == null || minecraft.player == null) {
            return;
        }
        RerollPrice price = villagertradeswap$rerollPrice;
        int labelWidth = font.width(TRADES_LABEL);
        int labelRight = 5 - labelWidth / 2 + 48 + labelWidth;
        toggle.visible = price != null;
        toggle.setPosition(leftPos + Math.min(labelRight + 3, TOGGLE_MAX_X), topPos + TOGGLE_Y);

        if (price == null) {
            villagertradeswap$rerollButtons.forEach(button -> button.visible = false);
            return;
        }
        int cost = minecraft.player.hasInfiniteMaterials() ? 0 : price.cost();
        boolean affordable = Currency.count(minecraft.player.getInventory(), price.currency()) >= cost;
        int offerCount = menu.getOffers().size();
        for (int row = 0; row < villagertradeswap$rerollButtons.size(); row++) {
            RerollButton button = villagertradeswap$rerollButtons.get(row);
            button.visible = toggle.isOn() && scrollOff + row < offerCount;
            button.setPosition(leftPos - RerollButton.SIZE - BUTTON_GAP, topPos + FIRST_ROW_Y + row * ROW_HEIGHT + (ROW_HEIGHT - RerollButton.SIZE) / 2);
            button.setPrice(price.currencyName(), cost, affordable);
        }
    }

    @Unique
    private void villagertradeswap$requestReroll(int row) {
        int index = scrollOff + row;
        if (villagertradeswap$rerollPrice != null && index < menu.getOffers().size()) {
            ClientServices.PLATFORM.sendRerollRequest(new RerollTradePayload(menu.containerId, index));
        }
    }
}
