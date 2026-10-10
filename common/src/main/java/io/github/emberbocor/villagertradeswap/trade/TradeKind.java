package io.github.emberbocor.villagertradeswap.trade;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.MerchantOffer;

public record TradeKind(Item costA, Item costB, Item result) {
    public static TradeKind of(MerchantOffer offer) {
        return new TradeKind(offer.getBaseCostA().getItem(), offer.getCostB().getItem(), offer.getResult().getItem());
    }
}
