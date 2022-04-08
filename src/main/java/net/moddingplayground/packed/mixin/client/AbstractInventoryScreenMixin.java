package net.moddingplayground.packed.mixin.client;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.moddingplayground.packed.api.item.BackpackItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin<T extends ScreenHandler> extends HandledScreen<T> {
    @Shadow protected abstract void drawStatusEffectBackgrounds(MatrixStack matrices, int x, int height, Iterable<StatusEffectInstance> effects, boolean wide);
    @Shadow protected abstract void drawStatusEffectSprites(MatrixStack matrices, int x, int height, Iterable<StatusEffectInstance> effects, boolean wide);
    @Shadow protected abstract Text getStatusEffectDescription(StatusEffectInstance effect);

    private AbstractInventoryScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "drawStatusEffects", at = @At("HEAD"), cancellable = true)
    private void onDrawStatusEffects(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.packed_offsetEffects()) {
            ci.cancel();

            Collection<StatusEffectInstance> effects = this.client.player.getStatusEffects();
            if (effects.isEmpty()) return;

            int x = this.x + this.backgroundWidth + 2 + 30;
            int y = effects.size() > 5 ? 132 / (effects.size() - 1) : 33;
            if (this.width - x < 32) return;

            List<StatusEffectInstance> sorted = Ordering.natural().sortedCopy(effects);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawStatusEffectBackgrounds(matrices, x, y, sorted, false);
            this.drawStatusEffectSprites(matrices, x, y, sorted, false);

            if (mouseX >= x && mouseX <= x + 33) {
                StatusEffectInstance effect = null;

                int iy = this.y;
                for (StatusEffectInstance ieff : sorted) {
                    if (mouseY >= iy && mouseY <= iy + y) effect = ieff;
                    iy += y;
                }

                if (effect != null) {
                    List<Text> tooltips = List.of(this.getStatusEffectDescription(effect), new LiteralText(StatusEffectUtil.durationToString(effect, 1.0f)));
                    this.renderTooltip(matrices, tooltips, Optional.empty(), mouseX, mouseY);
                }
            }
        }
    }

    private @Unique boolean packed_offsetEffects() {
        return BackpackItem.isEquipped(this.client.player) && !this.client.player.getAbilities().creativeMode;
    }
}
