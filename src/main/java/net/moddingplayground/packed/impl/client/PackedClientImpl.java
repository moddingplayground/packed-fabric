package net.moddingplayground.packed.impl.client;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.moddingplayground.frame.api.util.InitializationLogger;
import net.moddingplayground.packed.api.Packed;
import net.moddingplayground.packed.api.client.model.PackedEntityModelLayers;

@Environment(EnvType.CLIENT)
public final class PackedClientImpl implements Packed, ClientModInitializer {
    private final InitializationLogger initializer;

    public PackedClientImpl() {
        this.initializer = new InitializationLogger(LOGGER, MOD_NAME, EnvType.CLIENT);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitializeClient() {
        this.initializer.start();

        Reflection.initialize(PackedEntityModelLayers.class);

        this.initializer.finish();
    }
}
