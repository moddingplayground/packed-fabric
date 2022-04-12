package net.moddingplayground.packed.api.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.moddingplayground.packed.api.Packed;
import net.moddingplayground.packed.api.enchantment.PackedEnchantments;
import net.moddingplayground.packed.api.sound.PackedSoundEvents;
import net.moddingplayground.packed.impl.inventory.PlayerInventoryAccess;

import java.util.Optional;

public class BackpackItem extends Item implements Wearable {
    public static final String ITEMS_KEY = "Items";
    public static final String INVENTORY_BACKPACK_KEY = Packed.MOD_ID + ":backpack";

    public static final int MAX_SLOT_COUNT = getSlotCount(PackedEnchantments.CAPACITY.getMaxLevel());

    public static final float  MAX_DEFLECTION_CHANCE =  0.90F;
    public static final double DEFLECTION_ANGLE      = -0.17D;

    public BackpackItem(Settings settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    public static boolean isBlockedByDeflection(LivingEntity entity, DamageSource source) {
        boolean pierces = source.getSource() instanceof PersistentProjectileEntity projectile && projectile.getPierceLevel() > 0;
        if (!source.bypassesArmor() && !pierces) {
            return Optional.ofNullable(source.getPosition()).map(pos -> {
                Vec3d rot = entity.getRotationVec(1.0F).rotateY(180.0F);
                Vec3d rel = pos.relativize(entity.getPos()).normalize();
                rel = new Vec3d(rel.x, 0.0D, rel.z);
                return rel.dotProduct(rot) < DEFLECTION_ANGLE;
            }).orElse(false);
        }
        return false;
    }

    /* Interactions */

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

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }

    /* Utilities */

    public static DefaultedList<ItemStack> getStacks(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        PlayerInventoryAccess access = (PlayerInventoryAccess) inventory;
        return access.packed_getBackpackStacks();
    }

    public static boolean isEmpty(PlayerEntity player) {
        return getStacks(player).stream().allMatch(ItemStack::isEmpty);
    }

    public static boolean isEquipped(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof BackpackItem;
    }

    // deflection
    public static float getDeflectionChance(int level) {
        return MAX_DEFLECTION_CHANCE * ((float) level / PackedEnchantments.DEFLECTION.getMaxLevel());
    }

    public static float getDeflectionChance(ItemStack stack) {
        int level = EnchantmentHelper.getLevel(PackedEnchantments.DEFLECTION, stack);
        return getDeflectionChance(level);
    }

    // capacity
    public static boolean hasCapacityEnchantment(ItemStack stack) {
        return EnchantmentHelper.getLevel(PackedEnchantments.CAPACITY, stack) > 0;
    }

    public static int getSlotCount(ItemStack stack) {
        int level = EnchantmentHelper.getLevel(PackedEnchantments.CAPACITY, stack);
        return getSlotCount(level);
    }

    public static int getSlotCount(int level) {
        return 5 + (2 * level);
    }
}
