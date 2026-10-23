package io.github.emberbocor.villagertradeswap.trade;

import io.github.emberbocor.villagertradeswap.mixin.AbstractVillagerAccessor;
import io.github.emberbocor.villagertradeswap.mixin.VillagerInvoker;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

final class SpecialPrices {
    private SpecialPrices() {
    }

    static void apply(Villager villager, Player player, MerchantOffer offer) {
        MerchantOffers single = new MerchantOffers();
        single.add(offer);
        MerchantOffers offers = villager.getOffers();
        Player tradingPlayer = villager.getTradingPlayer();
        villager.setOffers(single);
        ((AbstractVillagerAccessor) villager).villagertradeswap$setTradingPlayerField(null);
        try {
            ((VillagerInvoker) villager).villagertradeswap$updateSpecialPrices(player);
        } finally {
            ((AbstractVillagerAccessor) villager).villagertradeswap$setTradingPlayerField(tradingPlayer);
            villager.setOffers(offers);
        }
    }
}
