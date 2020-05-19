package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class ListJigsawPiece extends JigsawPiece {
   private final List<JigsawPiece> elements;

   @Deprecated
   public ListJigsawPiece(List<JigsawPiece> p_i51404_1_) {
      this(p_i51404_1_, JigsawPattern.PlacementBehaviour.RIGID);
   }

   public ListJigsawPiece(List<JigsawPiece> p_i51405_1_, JigsawPattern.PlacementBehaviour p_i51405_2_) {
      super(p_i51405_2_);
      if (p_i51405_1_.isEmpty()) {
         throw new IllegalArgumentException("Elements are empty");
      } else {
         this.elements = p_i51405_1_;
         this.setProjectionOnEachElement(p_i51405_2_);
      }
   }

   public ListJigsawPiece(Dynamic<?> p_i51406_1_) {
      super(p_i51406_1_);
      List<JigsawPiece> list = p_i51406_1_.get("elements").asList((p_214866_0_) -> {
         return IDynamicDeserializer.func_214907_a(p_214866_0_, Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyJigsawPiece.INSTANCE);
      });
      if (list.isEmpty()) {
         throw new IllegalArgumentException("Elements are empty");
      } else {
         this.elements = list;
      }
   }

   public List<Template.BlockInfo> getJigsawBlocks(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn, Random rand) {
      return this.elements.get(0).getJigsawBlocks(templateManagerIn, pos, rotationIn, rand);
   }

   public MutableBoundingBox getBoundingBox(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn) {
      MutableBoundingBox mutableboundingbox = MutableBoundingBox.getNewBoundingBox();

      for(JigsawPiece jigsawpiece : this.elements) {
         MutableBoundingBox mutableboundingbox1 = jigsawpiece.getBoundingBox(templateManagerIn, pos, rotationIn);
         mutableboundingbox.expandTo(mutableboundingbox1);
      }

      return mutableboundingbox;
   }

   public boolean place(TemplateManager p_225575_1_, IWorld p_225575_2_, ChunkGenerator<?> p_225575_3_, BlockPos p_225575_4_, Rotation p_225575_5_, MutableBoundingBox p_225575_6_, Random p_225575_7_) {
      for(JigsawPiece jigsawpiece : this.elements) {
         if (!jigsawpiece.place(p_225575_1_, p_225575_2_, p_225575_3_, p_225575_4_, p_225575_5_, p_225575_6_, p_225575_7_)) {
            return false;
         }
      }

      return true;
   }

   public IJigsawDeserializer getType() {
      return IJigsawDeserializer.LIST_POOL_ELEMENT;
   }

   public JigsawPiece setPlacementBehaviour(JigsawPattern.PlacementBehaviour placementBehaviour) {
      super.setPlacementBehaviour(placementBehaviour);
      this.setProjectionOnEachElement(placementBehaviour);
      return this;
   }

   public <T> Dynamic<T> serialize0(DynamicOps<T> ops) {
      T t = ops.createList(this.elements.stream().map((p_214865_1_) -> {
         return p_214865_1_.serialize(ops).getValue();
      }));
      return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("elements"), t)));
   }

   public String toString() {
      return "List[" + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
   }

   private void setProjectionOnEachElement(JigsawPattern.PlacementBehaviour p_214864_1_) {
      this.elements.forEach((p_214863_1_) -> {
         p_214863_1_.setPlacementBehaviour(p_214864_1_);
      });
   }
}