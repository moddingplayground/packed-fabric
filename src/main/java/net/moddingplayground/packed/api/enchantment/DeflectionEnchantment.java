package net.moddingplayground.packed.api.enchantment;

public class DeflectionEnchantment extends BackpackEnchantment {
    public DeflectionEnchantment(Rarity weight) {
        super(weight);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
