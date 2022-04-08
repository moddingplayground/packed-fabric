package net.moddingplayground.packed.api.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.packed.api.Packed;

public interface PackedEnchantments {
    Enchantment CAPACITY = register("capacity", new CapacityEnchantment(Enchantment.Rarity.RARE));
    Enchantment DEFLECTION = register("deflection", new DeflectionEnchantment(Enchantment.Rarity.RARE));

    private static Enchantment register(String id, Enchantment enchantment) {
        return Registry.register(Registry.ENCHANTMENT, new Identifier(Packed.MOD_ID, id), enchantment);
    }
}
