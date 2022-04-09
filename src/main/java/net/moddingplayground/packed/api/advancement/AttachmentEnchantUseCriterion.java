package net.moddingplayground.packed.api.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.moddingplayground.packed.api.Packed;

public class AttachmentEnchantUseCriterion extends AbstractCriterion<AttachmentEnchantUseCriterion.Conditions> {
    public static final Identifier ID = new Identifier(Packed.MOD_ID, "attachment_enchant_use");
    public static final String BACKPACK_KEY = "backpack";

    public AttachmentEnchantUseCriterion() {}

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, conditions -> conditions.test(stack));
    }

    @Override
    public Conditions conditionsFromJson(JsonObject json, EntityPredicate.Extended player, AdvancementEntityPredicateDeserializer deserializer) {
        ItemPredicate backpack = ItemPredicate.fromJson(json.get(BACKPACK_KEY));
        return new Conditions(player, backpack);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final ItemPredicate backpack;

        public Conditions(EntityPredicate.Extended player, ItemPredicate backpack) {
            super(ID, player);
            this.backpack = backpack;
        }

        public static AttachmentEnchantUseCriterion.Conditions create(EntityPredicate.Builder player, ItemPredicate.Builder backpack) {
            return new Conditions(EntityPredicate.Extended.ofLegacy(player.build()), backpack.build());
        }

        public static AttachmentEnchantUseCriterion.Conditions create() {
            return create(EntityPredicate.Builder.create(), ItemPredicate.Builder.create());
        }

        public boolean test(ItemStack stack) {
            return this.backpack.test(stack);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject json = super.toJson(serializer);
            json.add(BACKPACK_KEY, this.backpack.toJson());
            return json;
        }
    }
}
