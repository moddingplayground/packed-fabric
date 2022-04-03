package net.moddingplayground.packed.api.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.moddingplayground.packed.impl.client.render.entity.BackpackArmorRendererImpl;

@Environment(EnvType.CLIENT)
public interface BackpackArmorRenderer extends ArmorRenderer, SimpleSynchronousResourceReloadListener {
    BackpackArmorRenderer INSTANCE = new BackpackArmorRendererImpl();
}
