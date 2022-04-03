package net.moddingplayground.packed.api.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.packed.api.Packed;

public interface PackedItems {
    Item BACKPACK = unstackable("backpack", s -> new BackpackItem(s.equipmentSlot(stack -> EquipmentSlot.CHEST)));

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Packed.MOD_ID, id), item);
    }

    private static Item unstackable(String id, ItemFactory<Item> factory) {
        return register(id, factory.create(new FabricItemSettings().maxCount(1).group(PackedItemGroups.ALL)));
    }

    @FunctionalInterface interface ItemFactory<T extends Item> { T create(FabricItemSettings settings); }
}
