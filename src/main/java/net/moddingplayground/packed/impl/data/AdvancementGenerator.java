package net.moddingplayground.packed.impl.data;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.moddingplayground.frame.api.toymaker.v0.generator.advancement.AbstractAdvancementGenerator;
import net.moddingplayground.packed.api.Packed;
import net.moddingplayground.packed.api.advancement.AttachmentEnchantUseCriterion;
import net.moddingplayground.packed.api.advancement.DeflectionEnchantUseCriterion;
import net.moddingplayground.packed.api.item.PackedItems;

public class AdvancementGenerator extends AbstractAdvancementGenerator {
    public static final String OBTAIN_BACKPACK = "obtain_backpack";
    public static final String ATTACHMENT_ENCHANT_USE = "attachment_enchant_use";
    public static final String DEFLECTION_ENCHANT_USE = "deflection_enchant_use";

    public static final ItemStack ENCHANTED_BACKPACK = Util.make(new ItemStack(PackedItems.BACKPACK), stack -> {
        NbtList enchantments = new NbtList();
        enchantments.add(new NbtCompound());
        stack.getOrCreateNbt().put("Enchantments", enchantments);
    });

    public AdvancementGenerator() {
        super(Packed.MOD_ID);
    }

    @Override
    public void generate() {
        Advancement root = Advancement.Builder.create().build(new Identifier("adventure/root"));

        // obtain backpack
        Advancement.Builder obtainBackpack =
            Advancement.Builder.create()
                               .parent(root)
                               .display(
                                   PackedItems.BACKPACK, title(OBTAIN_BACKPACK), description(OBTAIN_BACKPACK),
                                   null, AdvancementFrame.GOAL, false, false, false
                               )
                               .criterion("obtain_backpack", InventoryChangedCriterion.Conditions.items(PackedItems.BACKPACK));
        Advancement builtObtainBackpack = build(obtainBackpack, OBTAIN_BACKPACK);

        // attachment
        Advancement.Builder attachmentEnchantUse =
            Advancement.Builder.create()
                               .parent(builtObtainBackpack)
                               .display(
                                   ENCHANTED_BACKPACK, title(ATTACHMENT_ENCHANT_USE), description(ATTACHMENT_ENCHANT_USE),
                                   null, AdvancementFrame.TASK, true, true, false
                               )
                               .criterion("use_enchantment", AttachmentEnchantUseCriterion.Conditions.create());

        // deflection
        Advancement.Builder deflectionEnchantUse =
            Advancement.Builder.create()
                               .parent(builtObtainBackpack)
                               .display(
                                   ENCHANTED_BACKPACK, title(DEFLECTION_ENCHANT_USE), description(DEFLECTION_ENCHANT_USE),
                                   null, AdvancementFrame.TASK, true, true, false
                               )
                               .criterion("use_enchantment", DeflectionEnchantUseCriterion.Conditions.create());

        // add
        this.add(OBTAIN_BACKPACK, obtainBackpack);
        this.add(ATTACHMENT_ENCHANT_USE, attachmentEnchantUse);
        this.add(DEFLECTION_ENCHANT_USE, deflectionEnchantUse);
    }

    public TranslatableText title(String id) {
        return new TranslatableText("advancements.%s.%s.title".formatted(Packed.MOD_ID, id));
    }

    public TranslatableText description(String id) {
        return new TranslatableText("advancements.%s.%s.description".formatted(Packed.MOD_ID, id));
    }

    public Advancement build(Advancement.Builder builder, String id) {
        return builder.build(new Identifier(Packed.MOD_ID, id));
    }
}
