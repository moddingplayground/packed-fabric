package net.moddingplayground.packed.impl.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.moddingplayground.packed.api.Packed;
import net.moddingplayground.packed.api.advancement.AttachmentEnchantUseCriterion;
import net.moddingplayground.packed.api.advancement.DeflectionEnchantUseCriterion;
import net.moddingplayground.packed.api.item.PackedItems;

import java.util.function.Consumer;

public final class PackedDataGeneratorImpl implements Packed, DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator gen) {
        gen.addProvider(ModelProvider::new);
        gen.addProvider(RecipeProvider::new);
        gen.addProvider(AdvancementProvider::new);
    }

    private static class ModelProvider extends FabricModelProvider {
        public ModelProvider(FabricDataGenerator gen) {
            super(gen);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator gen) {}

        @Override
        public void generateItemModels(ItemModelGenerator gen) {
            gen.register(PackedItems.BACKPACK, Models.GENERATED);
        }
    }

    private static class RecipeProvider extends FabricRecipeProvider {
        public RecipeProvider(FabricDataGenerator gen) {
            super(gen);
        }

        @Override
        protected void generateRecipes(Consumer<RecipeJsonProvider> gen) {
            ShapedRecipeJsonBuilder.create(PackedItems.BACKPACK)
                                   .input('#', Items.LEATHER)
                                   .input('C', Items.CHEST)
                                   .input('O', Items.STRING)
                                   .pattern("O O")
                                   .pattern("#C#")
                                   .pattern("###")
                                   .criterion(hasItem(Items.CHEST), conditionsFromItem(Items.CHEST))
                                   .criterion("has_lots_of_items", new InventoryChangedCriterion.Conditions(
                                       EntityPredicate.Extended.EMPTY, NumberRange.IntRange.atLeast(10),
                                       NumberRange.IntRange.ANY, NumberRange.IntRange.ANY,
                                       new ItemPredicate[0]
                                   ))
                                   .offerTo(gen);
        }
    }

    private static class AdvancementProvider extends FabricAdvancementProvider {
        public static final ItemStack ENCHANTED_BACKPACK = Util.make(new ItemStack(PackedItems.BACKPACK), stack -> {
            NbtList enchantments = new NbtList();
            enchantments.add(new NbtCompound());
            stack.getOrCreateNbt().put("Enchantments", enchantments);
        });

        protected AdvancementProvider(FabricDataGenerator gen) {
            super(gen);
        }

        @Override
        public void generateAdvancement(Consumer<Advancement> gen) {
            Advancement root = Advancement.Builder.create().build(new Identifier("adventure/root"));

            Advancement obtainBackpack = create("obtain_backpack", new ItemStack(PackedItems.BACKPACK), advancement -> {
                advancement.criterion("obtain_backpack", InventoryChangedCriterion.Conditions.items(PackedItems.BACKPACK));
            }, false, root, gen);
            Advancement attachmentEnchantUse = create("attachment_enchant_use", ENCHANTED_BACKPACK, advancement -> {
                advancement.criterion("use_enchantment", AttachmentEnchantUseCriterion.Conditions.create());
            }, true, obtainBackpack, gen);
            Advancement deflectionEnchantUse = create("deflection_enchant_use", ENCHANTED_BACKPACK, advancement -> {
                advancement.criterion("use_enchantment", DeflectionEnchantUseCriterion.Conditions.create());
            }, true, obtainBackpack, gen);
        }

        public Advancement create(
            String id, ItemStack icon, Consumer<Advancement.Builder> conditions,
            boolean visible, Advancement parent, Consumer<Advancement> gen
        ) {
            Advancement.Builder advancement = Advancement.Builder.create()
                                                                 .parent(parent)
                                                                 .display(icon,
                                                                     Text.translatable("advancements.%s.%s.title".formatted(Packed.MOD_ID, id)),
                                                                     Text.translatable("advancements.%s.%s.description".formatted(Packed.MOD_ID, id)),
                                                                     null, AdvancementFrame.TASK, visible, visible, false
                                                                 );
            conditions.accept(advancement);
            return advancement.build(gen, Packed.MOD_ID + Identifier.NAMESPACE_SEPARATOR + id);
        }
    }
}
