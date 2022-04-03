package net.moddingplayground.packed.impl.data;

import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.moddingplayground.frame.api.toymaker.v0.generator.recipe.AbstractRecipeGenerator;
import net.moddingplayground.packed.api.item.PackedItems;

public class RecipeGenerator extends AbstractRecipeGenerator {
    public RecipeGenerator(String modId) {
        super(modId);
    }

    @Override
    public void generate() {
        this.add("backpack", ShapedRecipeJsonBuilder.create(PackedItems.BACKPACK)
                                                    .input('#', Items.LEATHER)
                                                    .input('C', Items.CHEST)
                                                    .input('O', Items.LEAD)
                                                    .pattern("O O")
                                                    .pattern("#C#")
                                                    .pattern("###")
                                                    .criterion("has_lots_of_items", new InventoryChangedCriterion.Conditions(
                                                        EntityPredicate.Extended.EMPTY,
                                                        NumberRange.IntRange.atLeast(10),
                                                        NumberRange.IntRange.ANY,
                                                        NumberRange.IntRange.ANY,
                                                        new ItemPredicate[0]
                                                    ))
        );
    }
}
