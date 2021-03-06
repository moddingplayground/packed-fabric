package net.moddingplayground.packed.impl.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface PlayerInventoryAccess {
    ItemStack packed_getBackpackStack(int index);
    void packed_setBackpackStack(int index, ItemStack stack);
    ItemStack packed_removeBackpackStack(int index, int amount);

    DefaultedList<ItemStack> packed_getBackpackStacks();
    void packed_setBackpackStacks(DefaultedList<ItemStack> stacks);
    void packed_clearBackpackStacks();

    int packed_getMaxCountPerBackpackStack();
}
