package io.github.emberbocor.villagertradeswap.trade;

import java.util.Map;
import java.util.WeakHashMap;

import io.github.emberbocor.villagertradeswap.config.ServerConfig;
import io.github.emberbocor.villagertradeswap.mixin.MerchantMenuAccessor;
import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class RerollHandler {
    private static final Map<ServerPlayer, Integer> LAST_REROLL_TICK = new WeakHashMap<>();

    private RerollHandler() {
    }

    public static void handle(ServerPlayer player, RerollTradePayload payload) {
        int tick = player.server.getTickCount();
        Integer lastTick = LAST_REROLL_TICK.put(player, tick);
        if (lastTick != null && lastTick == tick) {
            return;
        }
        if (!(player.containerMenu instanceof MerchantMenu menu)
                || menu.containerId != payload.containerId()
                || !(((MerchantMenuAccessor) menu).villagertradeswap$getTrader() instanceof Villager villager)
                || !canReroll(villager, player)) {
            return;
        }
        MerchantOffers offers = villager.getOffers();
        int index = payload.offerIndex();
        int cost = player.hasInfiniteMaterials() ? 0 : ServerConfig.rerollCost();
        if (index < 0 || index >= offers.size() || Emeralds.count(player.getInventory()) < cost) {
            return;
        }
        OfferLevelsHolder holder = (OfferLevelsHolder) villager;
        int[] levels = holder.villagertradeswap$getOfferLevels();
        MerchantOffer offer = levels != null ? RerollCandidates.roll(villager, offers, levels, index) : null;
        if (offer == null) {
            villager.makeSound(SoundEvents.VILLAGER_NO);
            player.displayClientMessage(Component.translatable("villagertradeswap.message.no_trade"), true);
            return;
        }
        if (cost > 0) {
            Emeralds.take(player.getInventory(), cost);
        }
        SpecialPrices.apply(villager, player, offer);
        offers.set(index, offer);
        holder.villagertradeswap$setOfferLevels(levels);
        menu.slotsChanged(menu.getSlot(0).container);
        player.sendMerchantOffers(menu.containerId, offers, villager.getVillagerData().getLevel(), villager.getVillagerXp(),
                villager.showProgressBar(), villager.canRestock());
        villager.makeSound(SoundEvents.VILLAGER_YES);
    }

    private static boolean canReroll(Villager villager, ServerPlayer player) {
        VillagerProfession profession = villager.getVillagerData().getProfession();
        return villager.isAlive()
                && !villager.isBaby()
                && villager.getTradingPlayer() == player
                && profession != VillagerProfession.NONE
                && profession != VillagerProfession.NITWIT;
    }
}
