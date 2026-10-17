package io.github.emberbocor.villagertradeswap.trade;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

record TradeIdentity(TradeKind kind, Object resultData) {
    static TradeIdentity of(MerchantOffer offer) {
        ItemStack result = offer.getResult();
        return new TradeIdentity(TradeKind.of(offer), MapTrades.isMap(result) ? MapTrades.destination(result) : result.getComponentsPatch());
    }
}
