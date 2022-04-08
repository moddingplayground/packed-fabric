package net.moddingplayground.packed.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.moddingplayground.packed.api.item.BackpackItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin<T extends ScreenHandler> extends HandledScreen<T> {
    private AbstractInventoryScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @ModifyVariable(method = "drawStatusEffects", at = @At("STORE"), index = 4)
    private int modifyDrawStatusEffectsX(int x) {
        return this.packed_offsetEffects() ? x + 30 : x;
    }

    @ModifyVariable(method = "drawStatusEffects", at = @At("STORE"), index = 7)
    private boolean modifyDrawStatusEffectsLarge(boolean large) {
        return !this.packed_offsetEffects() && large;
    }

    private @Unique boolean packed_offsetEffects() {
        return BackpackItem.isEquipped(this.client.player) && !this.client.player.getAbilities().creativeMode;
    }
}
