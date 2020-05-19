package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.EmptyJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class AbstractVillagePiece extends StructurePiece {
   protected final JigsawPiece jigsawPiece;
   protected BlockPos pos;
   private final int groundLevelDelta;
   protected final Rotation rotation;
   private final List<JigsawJunction> junctions = Lists.newArrayList();
   private final TemplateManager templateManager;

   public AbstractVillagePiece(IStructurePieceType structurePieceTypeIn, TemplateManager templateManagerIn, JigsawPiece jigsawPieceIn, BlockPos posIn, int groundLevelDelta, Rotation rotation, MutableBoundingBox p_i51346_7_) {
      super(structurePieceTypeIn, 0);
      this.templateManager = templateManagerIn;
      this.jigsawPiece = jigsawPieceIn;
      this.pos = posIn;
      this.groundLevelDelta = groundLevelDelta;
      this.rotation = rotation;
      this.boundingBox = p_i51346_7_;
   }

   public AbstractVillagePiece(TemplateManager templateManagerIn, CompoundNBT p_i51347_2_, IStructurePieceType structurePieceTypeIn) {
      super(structurePieceTypeIn, p_i51347_2_);
      this.templateManager = templateManagerIn;
      this.pos = new BlockPos(p_i51347_2_.getInt("PosX"), p_i51347_2_.getInt("PosY"), p_i51347_2_.getInt("PosZ"));
      this.groundLevelDelta = p_i51347_2_.getInt("ground_level_delta");
      this.jigsawPiece = IDynamicDeserializer.func_214907_a(new Dynamic<>(NBTDynamicOps.INSTANCE, p_i51347_2_.getCompound("pool_element")), Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyJigsawPiece.INSTANCE);
      this.rotation = Rotation.valueOf(p_i51347_2_.getString("rotation"));
      this.boundingBox = this.jigsawPiece.getBoundingBox(templateManagerIn, this.pos, this.rotation);
      ListNBT listnbt = p_i51347_2_.getList("junctions", 10);
      this.junctions.clear();
      listnbt.forEach((p_214827_1_) -> {
         this.junctions.add(JigsawJunction.deserialize(new Dynamic<>(NBTDynamicOps.INSTANCE, p_214827_1_)));
      });
   }

   /**
    * (abstract) Helper method to read subclass data from NBT
    */
   protected void readAdditional(CompoundNBT tagCompound) {
      tagCompound.putInt("PosX", this.pos.getX());
      tagCompound.putInt("PosY", this.pos.getY());
      tagCompound.putInt("PosZ", this.pos.getZ());
      tagCompound.putInt("ground_level_delta", this.groundLevelDelta);
      tagCompound.put("pool_element", this.jigsawPiece.serialize(NBTDynamicOps.INSTANCE).getValue());
      tagCompound.putString("rotation", this.rotation.name());
      ListNBT listnbt = new ListNBT();

      for(JigsawJunction jigsawjunction : this.junctions) {
         listnbt.add(jigsawjunction.serialize(NBTDynamicOps.INSTANCE).getValue());
      }

      tagCompound.put("junctions", listnbt);
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
      return this.jigsawPiece.place(this.templateManager, worldIn, chunkGeneratorIn, this.pos, this.rotation, mutableBoundingBoxIn, randomIn);
   }

   public void offset(int x, int y, int z) {
      super.offset(x, y, z);
      this.pos = this.pos.add(x, y, z);
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public String toString() {
      return String.format("<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.pos, this.rotation, this.jigsawPiece);
   }

   public JigsawPiece getJigsawPiece() {
      return this.jigsawPiece;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getGroundLevelDelta() {
      return this.groundLevelDelta;
   }

   public void addJunction(JigsawJunction junction) {
      this.junctions.add(junction);
   }

   public List<JigsawJunction> getJunctions() {
      return this.junctions;
   }
}