package net.moddingplayground.packed.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.moddingplayground.packed.api.Packed;
import net.moddingplayground.packed.api.advancement.PackedCriteria;
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

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccess {
    private static @Unique final String PACKED_DEATH_BACKPACK_KEY = Packed.MOD_ID + ":death_backpack";
    private static @Unique final String PACKED_BACKPACK_KEY = "Backpack";
    private static @Unique final String PACKED_ITEMS_KEY = "Items";
    private static @Unique final String PACKED_SLOT_KEY = "Slot";

    private @Unique Pair<ItemStack, DefaultedList<ItemStack>> packed_deathBackpackInventory = null;

    @Shadow public abstract PlayerInventory getInventory();

    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * Writes death backpack data to NBT.
     */
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        Optional.ofNullable(this.packed_deathBackpackInventory).ifPresent(pair -> {
            ItemStack stack = pair.getFirst();
            DefaultedList<ItemStack> inventory = pair.getSecond();

            NbtCompound nbtDeathBackpack = new NbtCompound();

            // backpack item
            NbtCompound nbtBackpack = stack.writeNbt(new NbtCompound());
            nbtDeathBackpack.put(PACKED_BACKPACK_KEY, nbtBackpack);

            // backpack inventory
            NbtList nbtItems = new NbtList();
            for (int i = 0, l = inventory.size(); i < l; i++) {
                ItemStack is = inventory.get(i);
                if (!is.isEmpty()) {
                    NbtCompound in = is.writeNbt(new NbtCompound());
                    in.putByte(PACKED_SLOT_KEY, (byte) i);
                    nbtItems.add(in);
                }
            }
            nbtDeathBackpack.put(PACKED_ITEMS_KEY, nbtItems);

            nbt.put(PACKED_DEATH_BACKPACK_KEY, nbtDeathBackpack);
        });
    }

    /**
     * Reads death backpack data from NBT.
     */
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void onReadCustomDataFromNBT(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(PACKED_DEATH_BACKPACK_KEY, NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbtDeathBackpack = nbt.getCompound(PACKED_DEATH_BACKPACK_KEY);

            // backpack item
            NbtCompound nbtBackpack = nbtDeathBackpack.getCompound(PACKED_BACKPACK_KEY);
            ItemStack stack = ItemStack.fromNbt(nbtBackpack);

            // backpack inventory
            DefaultedList<ItemStack> inventory = DefaultedList.ofSize(BackpackItem.MAX_SLOT_COUNT, ItemStack.EMPTY);
            NbtList nbtItems = nbtDeathBackpack.getList(PACKED_ITEMS_KEY, NbtElement.COMPOUND_TYPE);
            for (NbtElement in : nbtItems) {
                if (in instanceof NbtCompound inc) {
                    int slot = inc.getByte(PACKED_SLOT_KEY);
                    ItemStack is = ItemStack.fromNbt(inc);
                    inventory.set(slot, is);
                }
            }

            this.packed_deathBackpackInventory = Pair.of(stack, inventory);
        }
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
            if (that instanceof ServerPlayerEntity player) PackedCriteria.ATTACHMENT_ENCHANT_USE.trigger(player, stack.copy());

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
