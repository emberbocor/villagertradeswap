package io.github.emberbocor.villagertradeswap.client;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradeswap.VillagerTradeSwap;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

abstract class MouseOnlyButton extends AbstractButton {
    @Nullable
    private Component tooltipText;

    MouseOnlyButton(int size, Component message) {
        super(0, 0, size, size, message);
    }

    static Identifier sprite(String name) {
        return Identifier.fromNamespaceAndPath(VillagerTradeSwap.MODID, name);
    }

    protected abstract Identifier sprite();

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite(), getX(), getY(), getWidth(), getHeight());
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
    public boolean keyPressed(KeyEvent event) {
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
