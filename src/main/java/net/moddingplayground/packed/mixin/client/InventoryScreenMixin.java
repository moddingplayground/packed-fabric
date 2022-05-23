package net.moddingplayground.packed.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.moddingplayground.packed.api.Packed;
import net.moddingplayground.packed.api.item.BackpackItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
    private static final Identifier PACKED_BACKPACK_TEXTURE = new Identifier(Packed.MOD_ID, "textures/gui/backpack.png");
    private static final Identifier PACKED_BACKPACK_TEXTURE_CAPACITY = new Identifier(Packed.MOD_ID, "textures/gui/backpack_capacity.png");
    private static final Text PACKED_BACKPACK_TITLE = Text.translatable("ui.%s.backpack_title".formatted(Packed.MOD_ID));

    private InventoryScreenMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text text) {
        super(handler, inventory, text);
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    private void onDrawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.client == null) return;

        ItemStack stack = this.client.player.getEquippedStack(EquipmentSlot.CHEST);
        if (stack.getItem() instanceof BackpackItem) {
            RenderSystem.setShaderTexture(0, BackpackItem.hasCapacityEnchantment(stack) ? PACKED_BACKPACK_TEXTURE_CAPACITY : PACKED_BACKPACK_TEXTURE);
            int x = this.x + this.backgroundWidth;
            int y = this.y + 3;
            this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
            this.textRenderer.draw(matrices, PACKED_BACKPACK_TITLE, x + 3, y + 5, 0x404040);
        }
    }

    @Inject(method = "isClickOutsideBounds", at = @At("RETURN"), cancellable = true)
    private void onIsClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            PlayerEntity player = this.client.player;
            ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (stack.getItem() instanceof BackpackItem) {
                int slots = BackpackItem.getSlotCount(stack);

                int mx = (int) Math.floor(mouseX);
                int my = (int) Math.floor(mouseY);
                int sx = left + this.backgroundWidth + 5;
                int sy = top + 17;
                boolean ix = mx >= sx && mx <= sx + 17;
                boolean iy = my >= sy && my <= sy + (18 * slots) - 1;

                if (ix && iy) cir.setReturnValue(false);
            }
        }
    }
}
