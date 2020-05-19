package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrappedChestTileEntitySplit extends DataFix {
   private static final Logger LOGGER = LogManager.getLogger();

   public TrappedChestTileEntitySplit(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Type<?> type = this.getOutputSchema().getType(TypeReferences.CHUNK);
      Type<?> type1 = type.findFieldType("Level");
      Type<?> type2 = type1.findFieldType("TileEntities");
      if (!(type2 instanceof ListType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         ListType<?> listtype = (ListType)type2;
         OpticFinder<? extends List<?>> opticfinder = DSL.fieldFinder("TileEntities", listtype);
         Type<?> type3 = this.getInputSchema().getType(TypeReferences.CHUNK);
         OpticFinder<?> opticfinder1 = type3.findField("Level");
         OpticFinder<?> opticfinder2 = opticfinder1.type().findField("Sections");
         Type<?> type4 = opticfinder2.type();
         if (!(type4 instanceof ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
         } else {
            Type<?> type5 = ((ListType)type4).getElement();
            OpticFinder<?> opticfinder3 = DSL.typeFinder(type5);
            return TypeRewriteRule.seq((new AddNewChoices(this.getOutputSchema(), "AddTrappedChestFix", TypeReferences.BLOCK_ENTITY)).makeRule(), this.fixTypeEverywhereTyped("Trapped Chest fix", type3, (p_212533_5_) -> {
               return p_212533_5_.updateTyped(opticfinder1, (p_212531_4_) -> {
                  Optional<? extends Typed<?>> optional = p_212531_4_.getOptionalTyped(opticfinder2);
                  if (!optional.isPresent()) {
                     return p_212531_4_;
                  } else {
                     List<? extends Typed<?>> list = optional.get().getAllTyped(opticfinder3);
                     IntSet intset = new IntOpenHashSet();

                     for(Typed<?> typed : list) {
                        TrappedChestTileEntitySplit.Section trappedchesttileentitysplit$section = new TrappedChestTileEntitySplit.Section(typed, this.getInputSchema());
                        if (!trappedchesttileentitysplit$section.isSkippable()) {
                           for(int i = 0; i < 4096; ++i) {
                              int j = trappedchesttileentitysplit$section.getBlock(i);
                              if (trappedchesttileentitysplit$section.func_212511_a(j)) {
                                 intset.add(trappedchesttileentitysplit$section.getIndex() << 12 | i);
                              }
                           }
                        }
                     }

                     Dynamic<?> dynamic = p_212531_4_.get(DSL.remainderFinder());
                     int k = dynamic.get("xPos").asInt(0);
                     int l = dynamic.get("zPos").asInt(0);
                     TaggedChoiceType<String> taggedchoicetype = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
                     return p_212531_4_.updateTyped(opticfinder, (p_212532_4_) -> {
                        return p_212532_4_.updateTyped(taggedchoicetype.finder(), (p_212530_4_) -> {
                           Dynamic<?> dynamic1 = p_212530_4_.getOrCreate(DSL.remainderFinder());
                           int i1 = dynamic1.get("x").asInt(0) - (k << 4);
                           int j1 = dynamic1.get("y").asInt(0);
                           int k1 = dynamic1.get("z").asInt(0) - (l << 4);
                           return intset.contains(LeavesFix.getIndex(i1, j1, k1)) ? p_212530_4_.update(taggedchoicetype.finder(), (p_212534_0_) -> {
                              return p_212534_0_.mapFirst((p_212535_0_) -> {
                                 if (!Objects.equals(p_212535_0_, "minecraft:chest")) {
                                    LOGGER.warn("Block Entity was expected to be a chest");
                                 }

                                 return "minecraft:trapped_chest";
                              });
                           }) : p_212530_4_;
                        });
                     });
                  }
               });
            }));
         }
      }
   }

   public static final class Section extends LeavesFix.Section {
      @Nullable
      private IntSet field_212512_f;

      public Section(Typed<?> p_i49831_1_, Schema p_i49831_2_) {
         super(p_i49831_1_, p_i49831_2_);
      }

      protected boolean func_212508_a() {
         this.field_212512_f = new IntOpenHashSet();

         for(int i = 0; i < this.palette.size(); ++i) {
            Dynamic<?> dynamic = this.palette.get(i);
            String s = dynamic.get("Name").asString("");
            if (Objects.equals(s, "minecraft:trapped_chest")) {
               this.field_212512_f.add(i);
            }
         }

         return this.field_212512_f.isEmpty();
      }

      public boolean func_212511_a(int p_212511_1_) {
         return this.field_212512_f.contains(p_212511_1_);
      }
   }
}