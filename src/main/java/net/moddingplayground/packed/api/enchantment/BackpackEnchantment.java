package net.moddingplayground.packed.api.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class BackpackEnchantment extends Enchantment {
    public BackpackEnchantment(Rarity weight) {
        super(weight, EnchantmentTarget.WEARABLE, new EquipmentSlot[]{ EquipmentSlot.CHEST });
    }
}
