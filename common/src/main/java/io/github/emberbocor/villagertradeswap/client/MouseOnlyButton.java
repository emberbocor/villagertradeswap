package io.github.emberbocor.villagertradeswap.client;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

abstract class MouseOnlyButton extends AbstractButton {
    @Nullable
    private Component tooltipText;

    MouseOnlyButton(int size, Component message) {
        super(0, 0, size, size, message);
    }

    static ResourceLocation sprite(String name) {
        return ResourceLocation.fromNamespaceAndPath(VillagerTradeSwap.MODID, name);
    }

    protected abstract ResourceLocation sprite();

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(sprite(), getX(), getY(), getWidth(), getHeight());
    }

    void setTooltipText(Component text) {
        if (!text.equals(tooltipText)) {
            tooltipText = text;
            setTooltip(Tooltip.create(text));
        }
    }

    public boolean covers(double mouseX, double mouseY) {
        return visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + getWidth() && mouseY < getY() + getHeight();
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return null;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}
