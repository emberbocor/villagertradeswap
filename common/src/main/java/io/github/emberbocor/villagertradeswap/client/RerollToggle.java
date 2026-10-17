package io.github.emberbocor.villagertradeswap.client;

import io.github.emberbocor.villagertradeswap.config.ClientConfig;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class RerollToggle extends MouseOnlyButton {
    public static final int SIZE = 11;
    private static final WidgetSprites SPRITES = new WidgetSprites(
            sprite("toggle_on"), sprite("toggle_off"), sprite("toggle_on_highlighted"), sprite("toggle_off_highlighted"));
    private static final Component HIDE = Component.translatable("villagertradeswap.toggle.hide");
    private static final Component SHOW = Component.translatable("villagertradeswap.toggle.show");

    public RerollToggle() {
        super(SIZE, HIDE);
        updateTooltip();
    }

    public boolean isOn() {
        return ClientConfig.showRerollButtons();
    }

    @Override
    protected Identifier sprite() {
        return SPRITES.get(isOn(), isHovered());
    }

    @Override
    public void onPress(InputWithModifiers input) {
        ClientConfig.setShowRerollButtons(!isOn());
        updateTooltip();
    }

    private void updateTooltip() {
        Component text = isOn() ? HIDE : SHOW;
        setMessage(text);
        setTooltipText(text);
    }
}
