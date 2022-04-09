package net.moddingplayground.packed.api.advancement;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;

public interface PackedCriteria {
    AttachmentEnchantUseCriterion ATTACHMENT_ENCHANT_USE = register(new AttachmentEnchantUseCriterion());
    DeflectionEnchantUseCriterion DEFLECTION_ENCHANT_USE = register(new DeflectionEnchantUseCriterion());

    private static <T extends Criterion<?>> T register(T criterion) {
        return Criteria.register(criterion);
    }
}
