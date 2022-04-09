package net.moddingplayground.packed.api.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.moddingplayground.packed.api.Packed;

public class DeflectionEnchantUseCriterion extends AbstractCriterion<DeflectionEnchantUseCriterion.Conditions> {
    public static final Identifier ID = new Identifier(Packed.MOD_ID, "deflection_enchant_use");
    public static final String DAMAGE_KEY = "damage";

    public DeflectionEnchantUseCriterion() {}

    public void trigger(ServerPlayerEntity player, DamageSource source, float dealt, float taken) {
        this.trigger(player, conditions -> conditions.matches(player, source, dealt, taken));
    }

    @Override
    public Conditions conditionsFromJson(JsonObject json, EntityPredicate.Extended player, AdvancementEntityPredicateDeserializer deserializer) {
        DamagePredicate damage = DamagePredicate.fromJson(json.get(DAMAGE_KEY));
        return new Conditions(player, damage);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final DamagePredicate damage;

        public Conditions(EntityPredicate.Extended player, DamagePredicate damage) {
            super(ID, player);
            this.damage = damage;
        }

        public static Conditions create() {
            return new Conditions(EntityPredicate.Extended.EMPTY, DamagePredicate.ANY);
        }

        public static Conditions create(DamagePredicate damage) {
            return new Conditions(EntityPredicate.Extended.EMPTY, damage);
        }

        public static Conditions create(DamagePredicate.Builder damage) {
            return new Conditions(EntityPredicate.Extended.EMPTY, damage.build());
        }

        public boolean matches(ServerPlayerEntity player, DamageSource source, float dealt, float taken) {
            return this.damage.test(player, source, dealt, taken, true);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject json = super.toJson(serializer);
            json.add(DAMAGE_KEY, this.damage.toJson());
            return json;
        }
    }
}
