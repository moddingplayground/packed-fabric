package net.moddingplayground.packed.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.moddingplayground.packed.api.item.BackpackItem;
import net.moddingplayground.packed.impl.inventory.PlayerInventoryAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable, PlayerInventoryAccess {
    @Shadow @Final @Mutable private List<DefaultedList<ItemStack>> combinedInventory;
    @Shadow @Final public PlayerEntity player;

    @Unique private DefaultedList<ItemStack> packed_backpack = DefaultedList.ofSize(BackpackItem.MAX_SLOT_COUNT, ItemStack.EMPTY);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(PlayerEntity player, CallbackInfo ci) {
        List<DefaultedList<ItemStack>> combined = new ArrayList<>(this.combinedInventory);
        combined.add(this.packed_backpack);
        this.combinedInventory = combined;
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void onWriteNbt(NbtList list, CallbackInfoReturnable<NbtList> cir) {
        if (BackpackItem.isEmpty(this.player)) return; // do not write if empty (for data packs checking for empty nbt)

        NbtCompound nbtBackpack = new NbtCompound();
        nbtBackpack.putBoolean(BackpackItem.INVENTORY_BACKPACK_KEY, true);

        NbtList listBackpack = new NbtList();
        for (int i = 0, l = this.packed_backpack.size(); i < l; ++i) {
            if (this.packed_backpack.get(i).isEmpty()) continue;
            NbtCompound element = new NbtCompound();
            element.putByte("Slot", (byte) i);
            this.packed_backpack.get(i).writeNbt(element);
            listBackpack.add(element);
        }
        nbtBackpack.put(BackpackItem.ITEMS_KEY, listBackpack);

        list.add(nbtBackpack);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void onReadNbt(NbtList list, CallbackInfo ci) {
        this.packed_backpack.clear();
        list.stream()
            .filter(e -> e instanceof NbtCompound compound && compound.getBoolean(BackpackItem.INVENTORY_BACKPACK_KEY))
            .findFirst()
            .map(NbtCompound.class::cast)
            .ifPresent(nbt -> {
                NbtList stacks = nbt.getList(BackpackItem.ITEMS_KEY, NbtElement.COMPOUND_TYPE);
                for (int i = 0, l = stacks.size(); i < l; i++) {
                    NbtCompound nbtStack = stacks.getCompound(i);
                    int index = nbtStack.getByte("Slot");
                    ItemStack stack = ItemStack.fromNbt(nbtStack);
                    if (stack.isEmpty()) continue;
                    this.packed_backpack.set(index, stack);
                }
            });
    }

    @Inject(method = "size", at = @At("RETURN"), cancellable = true)
    private void onSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValueI() + this.packed_backpack.size()); // add backpack to inventory size
    }

    @Inject(method = "isEmpty", at = @At("RETURN"), cancellable = true)
    private void onIsEmpty(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            if (!this.packed_backpack.stream().allMatch(ItemStack::isEmpty)) cir.setReturnValue(false); // check for backpack empty as well
        }
    }

    @Inject(
        method = "setStack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"
        )
    )
    private void onSetStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (!this.player.world.isClient && !BackpackItem.isEquipped(this.player)) this.packed_backpack.clear();
    }

    @Override
    public @Unique ItemStack packed_getBackpackStack(int index) {
        return this.packed_backpack.get(index);
    }

    @Override
    public @Unique void packed_setBackpackStack(int index, ItemStack stack) {
        this.packed_backpack.set(index, stack);
    }

    @Override
    public @Unique ItemStack packed_removeBackpackStack(int index, int amount) {
        DefaultedList<ItemStack> inventory = this.packed_backpack;
        return !inventory.get(index).isEmpty()
            ? Inventories.splitStack(inventory, index, amount)
            : ItemStack.EMPTY;
    }

    @Override
    public @Unique DefaultedList<ItemStack> packed_getBackpackStacks() {
        return DefaultedList.copyOf(ItemStack.EMPTY,
            this.packed_backpack.stream()
                                .map(ItemStack::copy)
                                .toArray(ItemStack[]::new)
        );
    }

    @Override
    public void packed_setBackpackStacks(DefaultedList<ItemStack> stacks) {
        this.packed_backpack = stacks;
    }

    @Override
    public void packed_clearBackpackStacks() {
        this.packed_backpack.clear();
    }

    @Override
    public @Unique int packed_getMaxCountPerBackpackStack() {
        return this.getMaxCountPerStack();
    }
}
