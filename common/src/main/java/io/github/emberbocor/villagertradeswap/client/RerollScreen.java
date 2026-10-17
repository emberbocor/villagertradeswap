package io.github.emberbocor.villagertradeswap.client;

import net.minecraft.world.item.Item;

public interface RerollScreen {
    void villagertradeswap$setRerollPrice(Item currency, int cost);

    boolean villagertradeswap$isOverRerollButton(double mouseX, double mouseY);
}
