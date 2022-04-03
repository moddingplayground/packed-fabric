package net.moddingplayground.packed.api.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.moddingplayground.packed.api.sound.PackedSoundEvents;

public class BackpackItem extends Item {
    public BackpackItem(Settings settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        EquipmentSlot slot = MobEntity.getPreferredEquipmentSlot(stack);
        ItemStack slotStack = player.getEquippedStack(slot);
        if (slotStack.isEmpty()) {
            player.equipStack(slot, stack.copy());
            if (!player.getAbilities().creativeMode) stack.setCount(0);
            if (!world.isClient) player.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(stack, world.isClient);
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public SoundEvent getEquipSound() {
        return PackedSoundEvents.ITEM_BACKPACK_EQUIP;
    }
}
