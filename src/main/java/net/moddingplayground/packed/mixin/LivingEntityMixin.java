package net.moddingplayground.packed.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.moddingplayground.packed.api.advancement.PackedCriteria;
import net.moddingplayground.packed.api.item.BackpackItem;
import net.moddingplayground.packed.api.particle.PackedParticleTypes;
import net.moddingplayground.packed.api.sound.PackedSoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);
    @Shadow public abstract float getSoundPitch();
    @Shadow protected abstract float getSoundVolume();

    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
        method = "damage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = this.getEquippedStack(EquipmentSlot.CHEST);
        if (stack.getItem() instanceof BackpackItem && this.random.nextFloat() <= BackpackItem.getDeflectionChance(stack)) {
            LivingEntity that = LivingEntity.class.cast(this);
            if (BackpackItem.isBlockedByDeflection(that, source)) {
                if (this.world instanceof ServerWorld world) {
                    world.playSoundFromEntity(
                        null, that, PackedSoundEvents.ENCHANTMENT_DEFLECTION_HIT,
                        this.getSoundCategory(), this.getSoundVolume(), this.getSoundPitch()
                    );

                    Optional.ofNullable(source.getPosition()).ifPresent(pos -> {
                        world.spawnParticles(PackedParticleTypes.ENCHANT_DEFLECTION_HIT, pos.x, pos.y, pos.z, 17, 0.2D, 0.2D, 0.2D, 0);
                    });

                    if (that instanceof ServerPlayerEntity player) PackedCriteria.DEFLECTION_ENCHANT_USE.trigger(player, source, amount, amount);
                }

                cir.setReturnValue(false);
            }
        }
    }
}
