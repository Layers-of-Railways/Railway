package net.minecraft.world.gen.feature.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TemplateStructurePiece extends StructurePiece {
   private static final Logger LOGGER = LogManager.getLogger();
   protected Template template;
   protected PlacementSettings placeSettings;
   protected BlockPos templatePosition;

   public TemplateStructurePiece(IStructurePieceType structurePieceTypeIn, int componentTypeIn) {
      super(structurePieceTypeIn, componentTypeIn);
   }

   public TemplateStructurePiece(IStructurePieceType structurePieceTypeIn, CompoundNBT nbt) {
      super(structurePieceTypeIn, nbt);
      this.templatePosition = new BlockPos(nbt.getInt("TPX"), nbt.getInt("TPY"), nbt.getInt("TPZ"));
   }

   protected void setup(Template templateIn, BlockPos pos, PlacementSettings settings) {
      this.template = templateIn;
      this.setCoordBaseMode(Direction.NORTH);
      this.templatePosition = pos;
      this.placeSettings = settings;
      this.boundingBox = templateIn.getMutableBoundingBox(settings, pos);
   }

   /**
    * (abstract) Helper method to read subclass data from NBT
    */
   protected void readAdditional(CompoundNBT tagCompound) {
      tagCompound.putInt("TPX", this.templatePosition.getX());
      tagCompound.putInt("TPY", this.templatePosition.getY());
      tagCompound.putInt("TPZ", this.templatePosition.getZ());
   }

   /**
    * Create Structure Piece
    *  
    * @param worldIn world
    * @param chunkGeneratorIn chunkGenerator
    * @param randomIn random
    * @param mutableBoundingBoxIn mutableBoundingBox
    * @param chunkPosIn chunkPos
    */
   public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn, MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn) {
      this.placeSettings.setBoundingBox(mutableBoundingBoxIn);
      this.boundingBox = this.template.getMutableBoundingBox(this.placeSettings, this.templatePosition);
      if (this.template.addBlocksToWorld(worldIn, this.templatePosition, this.placeSettings, 2)) {
         for(Template.BlockInfo template$blockinfo : this.template.func_215381_a(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK)) {
            if (template$blockinfo.nbt != null) {
               StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt.getString("mode"));
               if (structuremode == StructureMode.DATA) {
                  this.handleDataMarker(template$blockinfo.nbt.getString("metadata"), template$blockinfo.pos, worldIn, randomIn, mutableBoundingBoxIn);
               }
            }
         }

         for(Template.BlockInfo template$blockinfo1 : this.template.func_215381_a(this.templatePosition, this.placeSettings, Blocks.JIGSAW)) {
            if (template$blockinfo1.nbt != null) {
               String s = template$blockinfo1.nbt.getString("final_state");
               BlockStateParser blockstateparser = new BlockStateParser(new StringReader(s), false);
               BlockState blockstate = Blocks.AIR.getDefaultState();

               try {
                  blockstateparser.parse(true);
                  BlockState blockstate1 = blockstateparser.getState();
                  if (blockstate1 != null) {
                     blockstate = blockstate1;
                  } else {
                     LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, template$blockinfo1.pos);
                  }
               } catch (CommandSyntaxException var14) {
                  LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, template$blockinfo1.pos);
               }

               worldIn.setBlockState(template$blockinfo1.pos, blockstate, 3);
            }
         }
      }

      return true;
   }

   protected abstract void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb);

   public void offset(int x, int y, int z) {
      super.offset(x, y, z);
      this.templatePosition = this.templatePosition.add(x, y, z);
   }

   public Rotation getRotation() {
      return this.placeSettings.getRotation();
   }
}