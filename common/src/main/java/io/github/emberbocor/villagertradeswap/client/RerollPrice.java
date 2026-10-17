package io.github.emberbocor.villagertradeswap.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public record RerollPrice(Item currency, Component currencyName, int cost) {
    public static RerollPrice of(Item currency, int cost) {
        return new RerollPrice(currency, currency.getDefaultInstance().getHoverName(), cost);
    }
}
