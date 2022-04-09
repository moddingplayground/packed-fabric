package net.moddingplayground.packed.impl.data;

import net.moddingplayground.frame.api.toymaker.v0.generator.model.item.AbstractItemModelGenerator;
import net.moddingplayground.packed.api.Packed;

import static net.moddingplayground.packed.api.item.PackedItems.*;

public class ItemModelGenerator extends AbstractItemModelGenerator {
    public ItemModelGenerator() {
        super(Packed.MOD_ID);
    }

    @Override
    public void generate() {
        this.generatedItems(BACKPACK);
    }
}
