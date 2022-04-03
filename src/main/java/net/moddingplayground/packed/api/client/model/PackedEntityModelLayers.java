package net.moddingplayground.packed.api.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.moddingplayground.packed.api.Packed;
import net.moddingplayground.packed.api.client.model.item.BackpackItemModel;

@Environment(EnvType.CLIENT)
public interface PackedEntityModelLayers {
    EntityModelLayer BACKPACK = main("backpack", BackpackItemModel::getTexturedModelData);

    private static EntityModelLayer register(String id, String layer, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        EntityModelLayer ret = new EntityModelLayer(new Identifier(Packed.MOD_ID, id), layer);
        EntityModelLayerRegistry.registerModelLayer(ret, provider);
        return ret;
    }

    private static EntityModelLayer main(String id, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        return register(id, "main", provider);
    }
}
