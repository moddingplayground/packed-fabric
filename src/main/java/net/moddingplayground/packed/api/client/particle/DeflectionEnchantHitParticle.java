package net.moddingplayground.packed.api.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DeflectionEnchantHitParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    public DeflectionEnchantHitParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;

        this.scale = 0.1F;
        this.velocityMultiplier = 0.78F;

        this.maxAge = 20 + (world.random.nextInt(2) * 5);
        this.collidesWithWorld = false;

        this.setSpriteForAge(spriteProvider);

        float color = 1F - (world.random.nextFloat() / 4);
        this.setColor(color, color, color);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getBrightness(float tint) {
        float age = ((float) this.age + tint) / (float) this.maxAge;
        age = MathHelper.clamp(age, 0.0f, 1.0f);
        int brightness = super.getBrightness(tint);
        int ba = brightness & 0xFF;
        int bb = brightness >> 16 & 0xFF;
        if ((ba += (int) (age * 15.0f * 16.0f)) > 240) ba = 240;
        return ba | bb << 16;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider sprite) {
            this.spriteProvider = sprite;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new DeflectionEnchantHitParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}
