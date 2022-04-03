package net.moddingplayground.packed.impl.data;

import net.moddingplayground.frame.api.toymaker.v0.ToymakerEntrypoint;
import net.moddingplayground.frame.api.toymaker.v0.registry.generator.ItemModelGeneratorStore;
import net.moddingplayground.frame.api.toymaker.v0.registry.generator.RecipeGeneratorStore;
import net.moddingplayground.packed.api.Packed;

public final class PackedToymakerImpl implements Packed, ToymakerEntrypoint {
    @Override
    public void onInitializeToymaker() {
        ItemModelGeneratorStore.register(() -> new ItemModelGenerator(MOD_ID));
        RecipeGeneratorStore.register(() -> new RecipeGenerator(MOD_ID));
    }
}
