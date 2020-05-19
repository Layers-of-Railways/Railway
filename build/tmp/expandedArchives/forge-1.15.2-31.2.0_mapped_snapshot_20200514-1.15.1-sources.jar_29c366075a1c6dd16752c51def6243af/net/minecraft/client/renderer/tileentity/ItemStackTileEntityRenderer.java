package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.ShieldModel;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ItemStackTileEntityRenderer {
   private static final ShulkerBoxTileEntity[] SHULKER_BOXES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxTileEntity::new).toArray((p_199929_0_) -> {
      return new ShulkerBoxTileEntity[p_199929_0_];
   });
   private static final ShulkerBoxTileEntity SHULKER_BOX = new ShulkerBoxTileEntity((DyeColor)null);
   public static final ItemStackTileEntityRenderer instance = new ItemStackTileEntityRenderer();
   private final ChestTileEntity chestBasic = new ChestTileEntity();
   private final ChestTileEntity chestTrap = new TrappedChestTileEntity();
   private final EnderChestTileEntity enderChest = new EnderChestTileEntity();
   private final BannerTileEntity banner = new BannerTileEntity();
   private final BedTileEntity bed = new BedTileEntity();
   private final ConduitTileEntity conduit = new ConduitTileEntity();
   private final ShieldModel modelShield = new ShieldModel();
   private final TridentModel trident = new TridentModel();

   public void render(ItemStack itemStackIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
      Item item = itemStackIn.getItem();
      if (item instanceof BlockItem) {
         Block block = ((BlockItem)item).getBlock();
         if (block instanceof AbstractSkullBlock) {
            GameProfile gameprofile = null;
            if (itemStackIn.hasTag()) {
               CompoundNBT compoundnbt = itemStackIn.getTag();
               if (compoundnbt.contains("SkullOwner", 10)) {
                  gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
               } else if (compoundnbt.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundnbt.getString("SkullOwner"))) {
                  GameProfile gameprofile1 = new GameProfile((UUID)null, compoundnbt.getString("SkullOwner"));
                  gameprofile = SkullTileEntity.updateGameProfile(gameprofile1);
                  compoundnbt.remove("SkullOwner");
                  compoundnbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
               }
            }

            SkullTileEntityRenderer.render((Direction)null, 180.0F, ((AbstractSkullBlock)block).getSkullType(), gameprofile, 0.0F, matrixStackIn, bufferIn, combinedLightIn);
         } else {
            TileEntity tileentity;
            if (block instanceof AbstractBannerBlock) {
               this.banner.loadFromItemStack(itemStackIn, ((AbstractBannerBlock)block).getColor());
               tileentity = this.banner;
            } else if (block instanceof BedBlock) {
               this.bed.setColor(((BedBlock)block).getColor());
               tileentity = this.bed;
            } else if (block == Blocks.CONDUIT) {
               tileentity = this.conduit;
            } else if (block == Blocks.CHEST) {
               tileentity = this.chestBasic;
            } else if (block == Blocks.ENDER_CHEST) {
               tileentity = this.enderChest;
            } else if (block == Blocks.TRAPPED_CHEST) {
               tileentity = this.chestTrap;
            } else {
               if (!(block instanceof ShulkerBoxBlock)) {
                  return;
               }

               DyeColor dyecolor = ShulkerBoxBlock.getColorFromItem(item);
               if (dyecolor == null) {
                  tileentity = SHULKER_BOX;
               } else {
                  tileentity = SHULKER_BOXES[dyecolor.getId()];
               }
            }

            TileEntityRendererDispatcher.instance.renderItem(tileentity, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
         }
      } else {
         if (item == Items.SHIELD) {
            boolean flag = itemStackIn.getChildTag("BlockEntityTag") != null;
            matrixStackIn.push();
            matrixStackIn.scale(1.0F, -1.0F, -1.0F);
            Material material = flag ? ModelBakery.LOCATION_SHIELD_BASE : ModelBakery.LOCATION_SHIELD_NO_PATTERN;
            IVertexBuilder ivertexbuilder = material.getSprite().wrapBuffer(ItemRenderer.getBuffer(bufferIn, this.modelShield.getRenderType(material.getAtlasLocation()), false, itemStackIn.hasEffect()));
            this.modelShield.func_228294_b_().render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            if (flag) {
               List<Pair<BannerPattern, DyeColor>> list = BannerTileEntity.func_230138_a_(ShieldItem.getColor(itemStackIn), BannerTileEntity.func_230139_a_(itemStackIn));
               BannerTileEntityRenderer.func_230180_a_(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, this.modelShield.func_228293_a_(), material, false, list);
            } else {
               this.modelShield.func_228293_a_().render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            matrixStackIn.pop();
         } else if (item == Items.TRIDENT) {
            matrixStackIn.push();
            matrixStackIn.scale(1.0F, -1.0F, -1.0F);
            IVertexBuilder ivertexbuilder1 = ItemRenderer.getBuffer(bufferIn, this.trident.getRenderType(TridentModel.TEXTURE_LOCATION), false, itemStackIn.hasEffect());
            this.trident.render(matrixStackIn, ivertexbuilder1, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.pop();
         }

      }
   }
}