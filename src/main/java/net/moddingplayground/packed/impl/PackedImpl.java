package net.moddingplayground.packed.impl;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ModInitializer;
import net.moddingplayground.frame.api.util.InitializationLogger;
import net.moddingplayground.packed.api.Packed;
import net.moddingplayground.packed.api.advancement.PackedCriteria;
import net.moddingplayground.packed.api.enchantment.PackedEnchantments;
import net.moddingplayground.packed.api.item.PackedItems;
import net.moddingplayground.packed.api.particle.PackedParticleTypes;
import net.moddingplayground.packed.api.sound.PackedSoundEvents;

public final class PackedImpl implements Packed, ModInitializer {
	private final InitializationLogger initializer;

	public PackedImpl() {
		this.initializer = new InitializationLogger(LOGGER, MOD_NAME);
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void onInitialize() {
		this.initializer.start();

		Reflection.initialize(
			PackedSoundEvents.class, PackedParticleTypes.class,
			PackedEnchantments.class, PackedItems.class,
			PackedCriteria.class
		);

		this.initializer.finish();
	}
}
