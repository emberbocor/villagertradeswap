package io.github.emberbocor.villagertradeswap.trade;

import java.util.stream.IntStream;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class Currency {
    private Currency() {
    }

    public static int count(Inventory inventory, Item currency) {
        return inventory.getNonEquipmentItems().stream()
                .filter(stack -> stack.is(currency))
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    public static void take(Inventory inventory, Item currency, int amount) {
        int remaining = amount;
        int hotbarSize = Inventory.getSelectionSize();
        int[] slots = IntStream.concat(IntStream.range(hotbarSize, inventory.getNonEquipmentItems().size()), IntStream.range(0, hotbarSize)).toArray();
        for (int slot : slots) {
            ItemStack stack = inventory.getNonEquipmentItems().get(slot);
            if (remaining > 0 && stack.is(currency)) {
                int taken = Math.min(remaining, stack.getCount());
                stack.shrink(taken);
                remaining -= taken;
            }
        }
        inventory.setChanged();
    }
}
