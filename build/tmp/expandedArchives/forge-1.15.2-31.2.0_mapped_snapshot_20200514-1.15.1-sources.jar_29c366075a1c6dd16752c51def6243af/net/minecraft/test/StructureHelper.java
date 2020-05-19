package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;

public class StructureHelper {
   public static String field_229590_a_ = "gameteststructures";

   public static AxisAlignedBB func_229594_a_(StructureBlockTileEntity p_229594_0_) {
      BlockPos blockpos = p_229594_0_.getPos().add(p_229594_0_.getPosition());
      return new AxisAlignedBB(blockpos, blockpos.add(p_229594_0_.getStructureSize()));
   }

   public static void func_229600_a_(BlockPos p_229600_0_, ServerWorld p_229600_1_) {
      p_229600_1_.setBlockState(p_229600_0_, Blocks.COMMAND_BLOCK.getDefaultState());
      CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)p_229600_1_.getTileEntity(p_229600_0_);
      commandblocktileentity.getCommandBlockLogic().setCommand("test runthis");
      p_229600_1_.setBlockState(p_229600_0_.add(0, 0, -1), Blocks.STONE_BUTTON.getDefaultState());
   }

   public static void func_229603_a_(String p_229603_0_, BlockPos p_229603_1_, BlockPos p_229603_2_, int p_229603_3_, ServerWorld p_229603_4_) {
      MutableBoundingBox mutableboundingbox = func_229598_a_(p_229603_1_, p_229603_2_, p_229603_3_);
      func_229595_a_(mutableboundingbox, p_229603_1_.getY(), p_229603_4_);
      p_229603_4_.setBlockState(p_229603_1_, Blocks.STRUCTURE_BLOCK.getDefaultState());
      StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)p_229603_4_.getTileEntity(p_229603_1_);
      structureblocktileentity.setIgnoresEntities(false);
      structureblocktileentity.setName(new ResourceLocation(p_229603_0_));
      structureblocktileentity.setSize(p_229603_2_);
      structureblocktileentity.setMode(StructureMode.SAVE);
      structureblocktileentity.setShowBoundingBox(true);
   }

   public static StructureBlockTileEntity func_229602_a_(String p_229602_0_, BlockPos p_229602_1_, int p_229602_2_, ServerWorld p_229602_3_, boolean p_229602_4_) {
      MutableBoundingBox mutableboundingbox = func_229598_a_(p_229602_1_, func_229605_a_(p_229602_0_, p_229602_3_).getSize(), p_229602_2_);
      func_229608_b_(p_229602_1_, p_229602_3_);
      func_229595_a_(mutableboundingbox, p_229602_1_.getY(), p_229602_3_);
      StructureBlockTileEntity structureblocktileentity = func_229604_a_(p_229602_0_, p_229602_1_, p_229602_3_, p_229602_4_);
      p_229602_3_.getPendingBlockTicks().getPending(mutableboundingbox, true, false);
      p_229602_3_.clearBlockEvents(mutableboundingbox);
      return structureblocktileentity;
   }

   private static void func_229608_b_(BlockPos p_229608_0_, ServerWorld p_229608_1_) {
      ChunkPos chunkpos = new ChunkPos(p_229608_0_);

      for(int i = -1; i < 4; ++i) {
         for(int j = -1; j < 4; ++j) {
            int k = chunkpos.x + i;
            int l = chunkpos.z + j;
            p_229608_1_.forceChunk(k, l, true);
         }
      }

   }

   public static void func_229595_a_(MutableBoundingBox p_229595_0_, int p_229595_1_, ServerWorld p_229595_2_) {
      BlockPos.getAllInBox(p_229595_0_).forEach((p_229592_2_) -> {
         func_229591_a_(p_229595_1_, p_229592_2_, p_229595_2_);
      });
      p_229595_2_.getPendingBlockTicks().getPending(p_229595_0_, true, false);
      p_229595_2_.clearBlockEvents(p_229595_0_);
      AxisAlignedBB axisalignedbb = new AxisAlignedBB((double)p_229595_0_.minX, (double)p_229595_0_.minY, (double)p_229595_0_.minZ, (double)p_229595_0_.maxX, (double)p_229595_0_.maxY, (double)p_229595_0_.maxZ);
      List<Entity> list = p_229595_2_.getEntitiesWithinAABB(Entity.class, axisalignedbb, (p_229593_0_) -> {
         return !(p_229593_0_ instanceof PlayerEntity);
      });
      list.forEach(Entity::remove);
   }

   public static MutableBoundingBox func_229598_a_(BlockPos p_229598_0_, BlockPos p_229598_1_, int p_229598_2_) {
      BlockPos blockpos = p_229598_0_.add(-p_229598_2_, -3, -p_229598_2_);
      BlockPos blockpos1 = p_229598_0_.add(p_229598_1_).add(p_229598_2_ - 1, 30, p_229598_2_ - 1);
      return MutableBoundingBox.createProper(blockpos.getX(), blockpos.getY(), blockpos.getZ(), blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
   }

   public static Optional<BlockPos> func_229596_a_(BlockPos p_229596_0_, int p_229596_1_, ServerWorld p_229596_2_) {
      return func_229609_c_(p_229596_0_, p_229596_1_, p_229596_2_).stream().filter((p_229601_2_) -> {
         return func_229599_a_(p_229601_2_, p_229596_0_, p_229596_2_);
      }).findFirst();
   }

   @Nullable
   public static BlockPos func_229607_b_(BlockPos p_229607_0_, int p_229607_1_, ServerWorld p_229607_2_) {
      Comparator<BlockPos> comparator = Comparator.comparingInt((p_229597_1_) -> {
         return p_229597_1_.manhattanDistance(p_229607_0_);
      });
      Collection<BlockPos> collection = func_229609_c_(p_229607_0_, p_229607_1_, p_229607_2_);
      Optional<BlockPos> optional = collection.stream().min(comparator);
      return optional.orElse((BlockPos)null);
   }

   public static Collection<BlockPos> func_229609_c_(BlockPos p_229609_0_, int p_229609_1_, ServerWorld p_229609_2_) {
      Collection<BlockPos> collection = Lists.newArrayList();
      AxisAlignedBB axisalignedbb = new AxisAlignedBB(p_229609_0_);
      axisalignedbb = axisalignedbb.grow((double)p_229609_1_);

      for(int i = (int)axisalignedbb.minX; i <= (int)axisalignedbb.maxX; ++i) {
         for(int j = (int)axisalignedbb.minY; j <= (int)axisalignedbb.maxY; ++j) {
            for(int k = (int)axisalignedbb.minZ; k <= (int)axisalignedbb.maxZ; ++k) {
               BlockPos blockpos = new BlockPos(i, j, k);
               BlockState blockstate = p_229609_2_.getBlockState(blockpos);
               if (blockstate.getBlock() == Blocks.STRUCTURE_BLOCK) {
                  collection.add(blockpos);
               }
            }
         }
      }

      return collection;
   }

   private static Template func_229605_a_(String p_229605_0_, ServerWorld p_229605_1_) {
      TemplateManager templatemanager = p_229605_1_.getStructureTemplateManager();
      Template template = templatemanager.getTemplate(new ResourceLocation(p_229605_0_));
      if (template != null) {
         return template;
      } else {
         String s = p_229605_0_ + ".snbt";
         Path path = Paths.get(field_229590_a_, s);
         CompoundNBT compoundnbt = func_229606_a_(path);
         if (compoundnbt == null) {
            throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
         } else {
            return templatemanager.func_227458_a_(compoundnbt);
         }
      }
   }

   private static StructureBlockTileEntity func_229604_a_(String p_229604_0_, BlockPos p_229604_1_, ServerWorld p_229604_2_, boolean p_229604_3_) {
      p_229604_2_.setBlockState(p_229604_1_, Blocks.STRUCTURE_BLOCK.getDefaultState());
      StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)p_229604_2_.getTileEntity(p_229604_1_);
      structureblocktileentity.setMode(StructureMode.LOAD);
      structureblocktileentity.setIgnoresEntities(false);
      structureblocktileentity.setName(new ResourceLocation(p_229604_0_));
      structureblocktileentity.load(p_229604_3_);
      if (structureblocktileentity.getStructureSize() != BlockPos.ZERO) {
         return structureblocktileentity;
      } else {
         Template template = func_229605_a_(p_229604_0_, p_229604_2_);
         structureblocktileentity.load(p_229604_3_, template);
         if (structureblocktileentity.getStructureSize() == BlockPos.ZERO) {
            throw new RuntimeException("Failed to load structure " + p_229604_0_);
         } else {
            return structureblocktileentity;
         }
      }
   }

   @Nullable
   private static CompoundNBT func_229606_a_(Path p_229606_0_) {
      try {
         BufferedReader bufferedreader = Files.newBufferedReader(p_229606_0_);
         String s = IOUtils.toString((Reader)bufferedreader);
         return JsonToNBT.getTagFromJson(s);
      } catch (IOException var3) {
         return null;
      } catch (CommandSyntaxException commandsyntaxexception) {
         throw new RuntimeException("Error while trying to load structure " + p_229606_0_, commandsyntaxexception);
      }
   }

   private static void func_229591_a_(int p_229591_0_, BlockPos p_229591_1_, ServerWorld p_229591_2_) {
      GenerationSettings generationsettings = p_229591_2_.getChunkProvider().getChunkGenerator().getSettings();
      BlockState blockstate;
      if (generationsettings instanceof FlatGenerationSettings) {
         BlockState[] ablockstate = ((FlatGenerationSettings)generationsettings).getStates();
         if (p_229591_1_.getY() < p_229591_0_) {
            blockstate = ablockstate[p_229591_1_.getY() - 1];
         } else {
            blockstate = Blocks.AIR.getDefaultState();
         }
      } else if (p_229591_1_.getY() == p_229591_0_ - 1) {
         blockstate = p_229591_2_.getBiome(p_229591_1_).getSurfaceBuilderConfig().getTop();
      } else if (p_229591_1_.getY() < p_229591_0_ - 1) {
         blockstate = p_229591_2_.getBiome(p_229591_1_).getSurfaceBuilderConfig().getUnder();
      } else {
         blockstate = Blocks.AIR.getDefaultState();
      }

      BlockStateInput blockstateinput = new BlockStateInput(blockstate, Collections.emptySet(), (CompoundNBT)null);
      blockstateinput.place(p_229591_2_, p_229591_1_, 2);
      p_229591_2_.notifyNeighbors(p_229591_1_, blockstate.getBlock());
   }

   private static boolean func_229599_a_(BlockPos p_229599_0_, BlockPos p_229599_1_, ServerWorld p_229599_2_) {
      StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)p_229599_2_.getTileEntity(p_229599_0_);
      AxisAlignedBB axisalignedbb = func_229594_a_(structureblocktileentity);
      return axisalignedbb.contains(new Vec3d(p_229599_1_));
   }
}