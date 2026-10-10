package io.github.emberbocor.villagertradeswap.client;

import io.github.emberbocor.villagertradeswap.config.ClientConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class RerollToggle extends MouseOnlyButton {
    public static final int SIZE = 11;
    private static final ResourceLocation ON_TEXTURE = texture("toggle_on");
    private static final ResourceLocation ON_HIGHLIGHTED_TEXTURE = texture("toggle_on_highlighted");
    private static final ResourceLocation OFF_TEXTURE = texture("toggle_off");
    private static final ResourceLocation OFF_HIGHLIGHTED_TEXTURE = texture("toggle_off_highlighted");
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
    protected ResourceLocation texture() {
        if (isOn()) {
            return isHovered() ? ON_HIGHLIGHTED_TEXTURE : ON_TEXTURE;
        }
        return isHovered() ? OFF_HIGHLIGHTED_TEXTURE : OFF_TEXTURE;
    }

    @Override
    public void onPress() {
        ClientConfig.setShowRerollButtons(!isOn());
        updateTooltip();
    }

    private void updateTooltip() {
        Component text = isOn() ? HIDE : SHOW;
        setMessage(text);
        setTooltipText(text);
    }
}
