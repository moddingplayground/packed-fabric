package net.moddingplayground.packed.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.moddingplayground.packed.api.item.BackpackItem;
import net.moddingplayground.packed.api.screen.slot.BackpackSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory> {
    private PlayerScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(PlayerInventory inventory, boolean server, PlayerEntity player, CallbackInfo ci) {
        for (int i = 0; i < BackpackItem.MAX_SLOT_COUNT; i++) this.addSlot(new BackpackSlot(inventory, i, 176 + 6, 18 + (i * 18)));
    }
}
