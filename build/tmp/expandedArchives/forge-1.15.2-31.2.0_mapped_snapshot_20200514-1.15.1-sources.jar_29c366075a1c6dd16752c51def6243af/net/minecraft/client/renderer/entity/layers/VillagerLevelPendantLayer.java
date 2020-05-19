package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHeadToggle;
import net.minecraft.client.resources.data.VillagerMetadataSection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerLevelPendantLayer<T extends LivingEntity & IVillagerDataHolder, M extends EntityModel<T> & IHeadToggle> extends LayerRenderer<T, M> implements IResourceManagerReloadListener {
   private static final Int2ObjectMap<ResourceLocation> field_215352_a = Util.make(new Int2ObjectOpenHashMap<>(), (p_215348_0_) -> {
      p_215348_0_.put(1, new ResourceLocation("stone"));
      p_215348_0_.put(2, new ResourceLocation("iron"));
      p_215348_0_.put(3, new ResourceLocation("gold"));
      p_215348_0_.put(4, new ResourceLocation("emerald"));
      p_215348_0_.put(5, new ResourceLocation("diamond"));
   });
   private final Object2ObjectMap<IVillagerType, VillagerMetadataSection.HatType> field_215353_b = new Object2ObjectOpenHashMap<>();
   private final Object2ObjectMap<VillagerProfession, VillagerMetadataSection.HatType> field_215354_c = new Object2ObjectOpenHashMap<>();
   private final IReloadableResourceManager field_215355_d;
   private final String field_215356_e;

   public VillagerLevelPendantLayer(IEntityRenderer<T, M> p_i50955_1_, IReloadableResourceManager p_i50955_2_, String p_i50955_3_) {
      super(p_i50955_1_);
      this.field_215355_d = p_i50955_2_;
      this.field_215356_e = p_i50955_3_;
      p_i50955_2_.addReloadListener(this);
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (!entitylivingbaseIn.isInvisible()) {
         VillagerData villagerdata = ((IVillagerDataHolder)entitylivingbaseIn).getVillagerData();
         IVillagerType ivillagertype = villagerdata.getType();
         VillagerProfession villagerprofession = villagerdata.getProfession();
         VillagerMetadataSection.HatType villagermetadatasection$hattype = this.func_215350_a(this.field_215353_b, "type", Registry.VILLAGER_TYPE, ivillagertype);
         VillagerMetadataSection.HatType villagermetadatasection$hattype1 = this.func_215350_a(this.field_215354_c, "profession", Registry.VILLAGER_PROFESSION, villagerprofession);
         M m = this.getEntityModel();
         ((IHeadToggle)m).func_217146_a(villagermetadatasection$hattype1 == VillagerMetadataSection.HatType.NONE || villagermetadatasection$hattype1 == VillagerMetadataSection.HatType.PARTIAL && villagermetadatasection$hattype != VillagerMetadataSection.HatType.FULL);
         ResourceLocation resourcelocation = this.func_215351_a("type", Registry.VILLAGER_TYPE.getKey(ivillagertype));
         renderCutoutModel(m, resourcelocation, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, 1.0F, 1.0F, 1.0F);
         ((IHeadToggle)m).func_217146_a(true);
         if (villagerprofession != VillagerProfession.NONE && !entitylivingbaseIn.isChild()) {
            ResourceLocation resourcelocation1 = this.func_215351_a("profession", Registry.VILLAGER_PROFESSION.getKey(villagerprofession));
            renderCutoutModel(m, resourcelocation1, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, 1.0F, 1.0F, 1.0F);
            if (villagerprofession != VillagerProfession.NITWIT) {
               ResourceLocation resourcelocation2 = this.func_215351_a("profession_level", field_215352_a.get(MathHelper.clamp(villagerdata.getLevel(), 1, field_215352_a.size())));
               renderCutoutModel(m, resourcelocation2, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, 1.0F, 1.0F, 1.0F);
            }
         }

      }
   }

   private ResourceLocation func_215351_a(String p_215351_1_, ResourceLocation p_215351_2_) {
      return new ResourceLocation(p_215351_2_.getNamespace(), "textures/entity/" + this.field_215356_e + "/" + p_215351_1_ + "/" + p_215351_2_.getPath() + ".png");
   }

   public <K> VillagerMetadataSection.HatType func_215350_a(Object2ObjectMap<K, VillagerMetadataSection.HatType> p_215350_1_, String p_215350_2_, DefaultedRegistry<K> p_215350_3_, K p_215350_4_) {
      return p_215350_1_.computeIfAbsent(p_215350_4_, (p_215349_4_) -> {
         try (IResource iresource = this.field_215355_d.getResource(this.func_215351_a(p_215350_2_, p_215350_3_.getKey(p_215350_4_)))) {
            VillagerMetadataSection villagermetadatasection = iresource.getMetadata(VillagerMetadataSection.field_217827_a);
            if (villagermetadatasection != null) {
               VillagerMetadataSection.HatType villagermetadatasection$hattype = villagermetadatasection.func_217826_a();
               return villagermetadatasection$hattype;
            }
         } catch (IOException var21) {
            ;
         }

         return VillagerMetadataSection.HatType.NONE;
      });
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.field_215354_c.clear();
      this.field_215353_b.clear();
   }
}