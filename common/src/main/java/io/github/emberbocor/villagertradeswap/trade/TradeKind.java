package io.github.emberbocor.villagertradeswap.trade;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

public record TradeKind(Item costA, Item costB, Item result) {
    public static TradeKind of(MerchantOffer offer) {
        return new TradeKind(
                offer.getItemCostA().item().value(),
                offer.getItemCostB().map(cost -> cost.item().value()).orElse(Items.AIR),
                offer.getResult().getItem());
    }
}
