package net.moddingplayground.packed.impl.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface PlayerEntityAccess {
    Pair<ItemStack, DefaultedList<ItemStack>> packed_getDeathBackpackInventory();
    void packed_clearDeathBackpackInventory();
}
