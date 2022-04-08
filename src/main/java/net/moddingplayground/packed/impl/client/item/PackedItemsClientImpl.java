package net.moddingplayground.packed.impl.client.item;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.moddingplayground.packed.api.client.render.entity.BackpackArmorRenderer;
import net.moddingplayground.packed.api.item.BackpackItem;
import net.moddingplayground.packed.api.item.PackedItems;

@Environment(EnvType.CLIENT)
public class PackedItemsClientImpl implements PackedItems, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ArmorRenderer.register(BackpackArmorRenderer.INSTANCE, BACKPACK);
        LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(p -> !(BackpackItem.isEquipped(p)));
    }
}
