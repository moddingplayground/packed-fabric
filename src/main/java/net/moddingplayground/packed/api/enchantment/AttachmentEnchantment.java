package net.moddingplayground.packed.api.enchantment;

public class AttachmentEnchantment extends BackpackEnchantment {
    public AttachmentEnchantment(Rarity rarity) {
        super(rarity);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }
}
