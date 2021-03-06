package net.moddingplayground.packed.api.particle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.packed.api.Packed;

public interface PackedParticleTypes {
    DefaultParticleType ENCHANT_DEFLECTION_HIT = register("enchant_deflection_hit", new DefaultParticleType(false) { });

    private static DefaultParticleType register(String id, DefaultParticleType particle) {
        return Registry.register(Registry.PARTICLE_TYPE, new Identifier(Packed.MOD_ID, id), particle);
    }
}
