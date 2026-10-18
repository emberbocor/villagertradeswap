package io.github.emberbocor.villagertradeswap.trade;

import java.util.Map;
import java.util.WeakHashMap;

import io.github.emberbocor.villagertradeswap.config.ServerConfig;
import io.github.emberbocor.villagertradeswap.mixin.MerchantMenuAccessor;
import io.github.emberbocor.villagertradeswap.network.RerollTradePayload;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class RerollHandler {
    private static final Map<ServerPlayer, Integer> LAST_REROLL_TICK = new WeakHashMap<>();

    private RerollHandler() {
    }

    public static void handle(ServerPlayer player, RerollTradePayload payload) {
        int tick = player.level().getServer().getTickCount();
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
        Item currency = ServerConfig.rerollCurrency();
        int cost = player.hasInfiniteMaterials() ? 0 : ServerConfig.rerollCost();
        if (index < 0 || index >= offers.size() || Currency.count(player.getInventory(), currency) < cost) {
            return;
        }
        OfferLevelsHolder holder = (OfferLevelsHolder) villager;
        int[] levels = holder.villagertradeswap$getOfferLevels();
        MerchantOffer offer = levels != null ? RerollCandidates.roll(player.level(), villager, offers, levels, index, ServerConfig.allowSameTradeInLevel()) : null;
        if (offer == null) {
            villager.makeSound(SoundEvents.VILLAGER_NO);
            player.sendOverlayMessage(Component.translatable("villagertradeswap.message.no_trade"));
            return;
        }
        if (cost > 0) {
            Currency.take(player.getInventory(), currency, cost);
        }
        SpecialPrices.apply(villager, player, offer);
        offers.set(index, offer);
        holder.villagertradeswap$setOfferLevels(levels);
        menu.slotsChanged(menu.getSlot(0).container);
        player.sendMerchantOffers(menu.containerId, offers, villager.getVillagerData().level(), villager.getVillagerXp(),
                villager.showProgressBar(), villager.canRestock());
        villager.makeSound(SoundEvents.VILLAGER_YES);
    }

    private static boolean canReroll(Villager villager, ServerPlayer player) {
        Holder<VillagerProfession> profession = villager.getVillagerData().profession();
        return villager.isAlive()
                && !villager.isBaby()
                && villager.getTradingPlayer() == player
                && !profession.is(VillagerProfession.NONE)
                && !profession.is(VillagerProfession.NITWIT);
    }
}
