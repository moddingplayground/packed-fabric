package net.moddingplayground.packed.api.client.model.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.LivingEntity;

/**
 * Represents the model of a backpack.
 *
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value BACKPACK}</td><td>{@linkplain #root Root part}</td><td>{@link #root}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class BackpackItemModel<T extends LivingEntity> extends SinglePartEntityModel<T> {
    private static final String BACKPACK = "backpack";

    private final ModelPart root;

    public BackpackItemModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        ModelPartData backpack = root.addChild(
            BACKPACK,
            ModelPartBuilder.create()
                            .uv(0, 0)
                            .cuboid(-4.0F, -23.0F, 2.0F, 8.0F, 8.0F, 3.0F)
                            .uv(0, 21)
                            .cuboid(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.5F))
                            .uv(0, 11)
                            .cuboid(-4.0F, -23.0F, 2.0F, 8.0F, 6.0F, 3.0F, new Dilation(0.25F)),
            ModelTransform.pivot(0.0F, 24.0F, 0.0F)
        );

        return TexturedModelData.of(data, 48, 48);
    }

    public void copyFrom(BipedEntityModel<T> model) {
        model.copyStateTo(this);
        this.root.copyTransform(model.body);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
