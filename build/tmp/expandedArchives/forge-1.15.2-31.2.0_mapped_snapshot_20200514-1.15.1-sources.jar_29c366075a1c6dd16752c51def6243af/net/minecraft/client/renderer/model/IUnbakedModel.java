package net.minecraft.client.renderer.model;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IUnbakedModel extends net.minecraftforge.client.extensions.IForgeUnbakedModel {
   Collection<ResourceLocation> getDependencies();

   Collection<Material> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors);

   @Nullable
   IBakedModel bakeModel(ModelBakery modelBakeryIn, Function<Material, TextureAtlasSprite> spriteGetterIn, IModelTransform transformIn, ResourceLocation locationIn);
}