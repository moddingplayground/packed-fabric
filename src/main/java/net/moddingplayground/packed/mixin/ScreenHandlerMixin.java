package net.moddingplayground.packed.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.moddingplayground.packed.api.item.BackpackItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Shadow public abstract ItemStack getCursorStack();

    /**
     * Prevents interaction with equipped backpacks under certain conditions.
     */
    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void onInternalOnSlotClick(int slot, int button, SlotActionType type, PlayerEntity player, CallbackInfo ci) {
        if (slot == EquipmentSlot.CHEST.getOffsetEntitySlotId(4) && BackpackItem.isEquipped(player)) {
            ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack other = this.getCursorStack();
            boolean cursorBetter = other.isItemEqualIgnoreDamage(stack) && BackpackItem.getSlotCount(other) >= BackpackItem.getSlotCount(stack);
            if (!cursorBetter && !BackpackItem.isEmpty(player)) ci.cancel();
        }
    }
}
