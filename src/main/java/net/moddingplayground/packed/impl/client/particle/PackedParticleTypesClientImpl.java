package net.moddingplayground.packed.impl.client.particle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.moddingplayground.packed.api.client.particle.DeflectionEnchantHitParticle;
import net.moddingplayground.packed.api.particle.PackedParticleTypes;

@Environment(EnvType.CLIENT)
public final class PackedParticleTypesClientImpl implements PackedParticleTypes, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry particles = ParticleFactoryRegistry.getInstance();
        particles.register(ENCHANT_DEFLECTION_HIT, DeflectionEnchantHitParticle.Factory::new);
    }
}
