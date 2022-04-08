package net.moddingplayground.packed.api.screen.slot;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.moddingplayground.packed.api.item.BackpackItem;
import net.moddingplayground.packed.impl.inventory.PlayerInventoryAccess;

public class BackpackSlot extends Slot {
    private final PlayerInventory inventory;
    private final PlayerInventoryAccess access;

    public BackpackSlot(PlayerInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.inventory = inventory;
        this.access = (PlayerInventoryAccess) inventory;
    }

    @Override
    public ItemStack getStack() {
        return this.access.packed_getBackpackStack(this.getIndex());
    }

    @Override
    public void setStack(ItemStack stack) {
        this.access.packed_setBackpackStack(this.getIndex(), stack);
        this.markDirty();
    }

    @Override
    public ItemStack takeStack(int amount) {
        return this.access.packed_removeBackpackStack(this.getIndex(), amount);
    }

    @Override
    public int getMaxItemCount() {
        return this.access.packed_getMaxCountPerBackpackStack();
    }

    @Override
    public boolean isEnabled() {
        PlayerEntity player = this.inventory.player;
        if (player.getAbilities().creativeMode) return false;

        ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (stack.getItem() instanceof BackpackItem) {
            int slotCount = BackpackItem.getSlotCount(stack);
            return this.getIndex() < slotCount;
        }

        return false;
    }
}
