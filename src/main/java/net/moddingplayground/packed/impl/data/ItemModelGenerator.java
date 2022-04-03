package net.moddingplayground.packed.impl.data;

import net.moddingplayground.frame.api.toymaker.v0.generator.model.item.AbstractItemModelGenerator;

import static net.moddingplayground.packed.api.item.PackedItems.*;

public class ItemModelGenerator extends AbstractItemModelGenerator {
    public ItemModelGenerator(String modId) {
        super(modId);
    }

    @Override
    public void generate() {
        this.generatedItems(BACKPACK);
    }
}
