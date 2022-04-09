package net.moddingplayground.packed.impl.item;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.moddingplayground.packed.api.Packed;
import net.moddingplayground.packed.api.enchantment.PackedEnchantments;
import net.moddingplayground.packed.impl.entity.PlayerEntityAccess;
import net.moddingplayground.packed.impl.inventory.PlayerInventoryAccess;

import java.util.Optional;

public final class PackedItemsImpl implements Packed, ModInitializer {
    private static final String PACKED_ATTACHMENT_ENCHANTMENT_ID = Registry.ENCHANTMENT.getId(PackedEnchantments.ATTACHMENT).toString();

    @Override
    public void onInitialize() {
        ServerPlayerEvents.COPY_FROM.register((old, nu, alive) -> {
            if (alive) return; // do not run if the old player is not dead (i.e. returning from the end)
            if (nu.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) return; // do not unnecessarily try to run if keep inventory is enabled

            PlayerEntityAccess accesso = (PlayerEntityAccess) old;
            Optional.ofNullable(accesso.packed_getDeathBackpackInventory()).ifPresent(backpack -> {
                ItemStack stack = backpack.getFirst();
                DefaultedList<ItemStack> inventory = backpack.getSecond();
                PlayerInventoryAccess accessi = (PlayerInventoryAccess) nu.getInventory();

                // remove attachment enchantment, and clean up nbt if empty
                NbtCompound nbt = stack.getNbt();
                Optional.ofNullable(nbt)
                        .map(n -> n.getList("Enchantments", NbtElement.COMPOUND_TYPE))
                        .ifPresent(enchantments -> {
                            enchantments.removeIf(e -> e instanceof NbtCompound n && n.getString("id").equals(PACKED_ATTACHMENT_ENCHANTMENT_ID));
                            if (enchantments.isEmpty()) nbt.remove("Enchantments");
                            if (nbt.isEmpty()) stack.setNbt(null);
                        });

                // requip backpack
                nu.equipStack(EquipmentSlot.CHEST, stack);
                accessi.packed_setBackpackStacks(inventory);

                // clear stored backpack information on old player
                accesso.packed_clearDeathBackpackInventory();
            });
        });
    }
}
