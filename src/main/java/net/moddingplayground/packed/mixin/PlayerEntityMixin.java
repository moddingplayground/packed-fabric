package net.moddingplayground.packed.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.moddingplayground.packed.api.enchantment.PackedEnchantments;
import net.moddingplayground.packed.api.item.BackpackItem;
import net.moddingplayground.packed.impl.entity.PlayerEntityAccess;
import net.moddingplayground.packed.impl.inventory.PlayerInventoryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccess {
    @Shadow public abstract PlayerInventory getInventory();

    private @Unique Pair<ItemStack, DefaultedList<ItemStack>> packed_deathBackpackInventory = null;

    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * Captures the player's backpack and backpack inventory before the inventory is processed.
     */
    @Inject(
        method = "dropInventory",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;vanishCursedItems()V",
            shift = At.Shift.AFTER
        )
    )
    private void onDropInventory(CallbackInfo ci) {
        PlayerEntity that = PlayerEntity.class.cast(this);
        ItemStack stack = this.getEquippedStack(EquipmentSlot.CHEST);
        if (stack.getItem() instanceof BackpackItem && EnchantmentHelper.getLevel(PackedEnchantments.ATTACHMENT, stack) > 0) {
            DefaultedList<ItemStack> inv = BackpackItem.getStacks(that);
            this.packed_deathBackpackInventory = Pair.of(stack, inv);

            // remove backpack information from inventory to prevent drops
            PlayerInventoryAccess accessi = (PlayerInventoryAccess) this.getInventory();
            this.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
            accessi.packed_clearBackpackStacks();
        }
    }

    @Override
    public @Unique Pair<ItemStack, DefaultedList<ItemStack>> packed_getDeathBackpackInventory() {
        return this.packed_deathBackpackInventory;
    }

    @Override
    public @Unique void packed_clearDeathBackpackInventory() {
        this.packed_deathBackpackInventory = null;
    }
}
