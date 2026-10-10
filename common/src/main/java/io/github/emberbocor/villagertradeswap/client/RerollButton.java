package io.github.emberbocor.villagertradeswap.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class RerollButton extends MouseOnlyButton {
    public static final int SIZE = 14;
    private static final ResourceLocation TEXTURE = texture("reroll");
    private static final ResourceLocation HIGHLIGHTED_TEXTURE = texture("reroll_highlighted");
    private static final ResourceLocation DISABLED_TEXTURE = texture("reroll_disabled");
    private static final Component TITLE = Component.translatable("villagertradeswap.button.reroll");

    private final Runnable action;

    public RerollButton(Runnable action) {
        super(SIZE, TITLE);
        this.action = action;
    }

    public void setCost(int cost, boolean affordable) {
        active = affordable;
        Component price = cost == 0 ? Component.translatable("villagertradeswap.cost.free")
                : cost == 1 ? Component.translatable("villagertradeswap.cost.emerald")
                : Component.translatable("villagertradeswap.cost.emeralds", cost);
        Component detail = affordable ? price.copy().withStyle(ChatFormatting.GRAY)
                : Component.translatable("villagertradeswap.cost.not_enough", price).withStyle(ChatFormatting.RED);
        setTooltipText(TITLE.copy().append("\n").append(detail));
    }

    @Override
    protected ResourceLocation texture() {
        if (!active) {
            return DISABLED_TEXTURE;
        }
        return isHovered() ? HIGHLIGHTED_TEXTURE : TEXTURE;
    }

    @Override
    public void onPress() {
        action.run();
    }
}
