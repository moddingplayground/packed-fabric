package net.moddingplayground.packed.impl.client.render.entity;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.moddingplayground.packed.api.Packed;
import net.moddingplayground.packed.api.client.model.PackedEntityModelLayers;
import net.moddingplayground.packed.api.client.model.item.BackpackItemModel;
import net.moddingplayground.packed.api.client.render.entity.BackpackArmorRenderer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public final class BackpackArmorRendererImpl implements BackpackArmorRenderer, ClientModInitializer {
    public static final Identifier TEXTURE = new Identifier(Packed.MOD_ID, "textures/models/backpack.png");
    public static final Identifier TEXTURE_AUTHORS = new Identifier(Packed.MOD_ID, "textures/models/backpack_authors.png");

    public static final List<UUID> AUTHORS = ImmutableList.of(
        UUID.fromString("50c7e7c5-2407-4102-875f-e4b1f49ea61a"), // Andante
        UUID.fromString("2d173722-de6b-4bb8-b21b-b2843cfe395d")  // Ninni
    );

    private BackpackItemModel<LivingEntity> model;

    public BackpackArmorRendererImpl() {}

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(INSTANCE);
    }

    @Override
    public void reload(ResourceManager manager) {
        MinecraftClient client = MinecraftClient.getInstance();
        EntityModelLoader models = client.getEntityModelLoader();
        this.model = new BackpackItemModel<>(models.getModelPart(PackedEntityModelLayers.BACKPACK));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> model) {
        if (slot != EquipmentSlot.CHEST) return;
        RenderLayer layer = RenderLayer.getArmorCutoutNoCull(AUTHORS.contains(entity.getUuid()) ? TEXTURE_AUTHORS : TEXTURE);
        VertexConsumer vertex = ItemRenderer.getArmorGlintConsumer(vertices, layer, false, stack.hasGlint());
        this.model.copyFrom(model);
        this.model.render(matrices, vertex, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Packed.MOD_ID, "backpack_armor_renderer");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return Collections.singletonList(ResourceReloadListenerKeys.MODELS);
    }
}
