package net.moddingplayground.packed.impl;

import net.fabricmc.api.ModInitializer;
import net.moddingplayground.packed.api.Packed;
import net.moddingplayground.frame.api.util.InitializationLogger;

public final class PackedImpl implements Packed, ModInitializer {
	private final InitializationLogger initializer;

	public PackedImpl() {
		this.initializer = new InitializationLogger(LOGGER, MOD_NAME);
	}

	@Override
	public void onInitialize() {
		this.initializer.start();

		//

		this.initializer.finish();
	}
}
