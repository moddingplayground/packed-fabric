package net.moddingplayground.packed.api.enchantment;

public class CapacityEnchantment extends BackpackEnchantment {
    public CapacityEnchantment(Rarity rarity) {
        super(rarity);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }
}
