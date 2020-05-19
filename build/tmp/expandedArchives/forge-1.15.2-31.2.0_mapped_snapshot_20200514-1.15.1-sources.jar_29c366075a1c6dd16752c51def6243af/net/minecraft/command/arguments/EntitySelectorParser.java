package net.minecraft.command.arguments;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.MinMaxBoundsWrapped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;

public class EntitySelectorParser {
   public static final SimpleCommandExceptionType INVALID_ENTITY_NAME_OR_UUID = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.invalid"));
   public static final DynamicCommandExceptionType UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType((p_208703_0_) -> {
      return new TranslationTextComponent("argument.entity.selector.unknown", p_208703_0_);
   });
   public static final SimpleCommandExceptionType SELECTOR_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.selector.not_allowed"));
   public static final SimpleCommandExceptionType SELECTOR_TYPE_MISSING = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.selector.missing"));
   public static final SimpleCommandExceptionType EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.unterminated"));
   public static final DynamicCommandExceptionType EXPECTED_VALUE_FOR_OPTION = new DynamicCommandExceptionType((p_208711_0_) -> {
      return new TranslationTextComponent("argument.entity.options.valueless", p_208711_0_);
   });
   public static final BiConsumer<Vec3d, List<? extends Entity>> ARBITRARY = (p_197402_0_, p_197402_1_) -> {
   };
   public static final BiConsumer<Vec3d, List<? extends Entity>> NEAREST = (p_197392_0_, p_197392_1_) -> {
      p_197392_1_.sort((p_197393_1_, p_197393_2_) -> {
         return Doubles.compare(p_197393_1_.getDistanceSq(p_197392_0_), p_197393_2_.getDistanceSq(p_197392_0_));
      });
   };
   public static final BiConsumer<Vec3d, List<? extends Entity>> FURTHEST = (p_197383_0_, p_197383_1_) -> {
      p_197383_1_.sort((p_197369_1_, p_197369_2_) -> {
         return Doubles.compare(p_197369_2_.getDistanceSq(p_197383_0_), p_197369_1_.getDistanceSq(p_197383_0_));
      });
   };
   public static final BiConsumer<Vec3d, List<? extends Entity>> RANDOM = (p_197368_0_, p_197368_1_) -> {
      Collections.shuffle(p_197368_1_);
   };
   public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NONE = (p_201342_0_, p_201342_1_) -> {
      return p_201342_0_.buildFuture();
   };
   private final StringReader reader;
   private final boolean hasPermission;
   private int limit;
   private boolean includeNonPlayers;
   private boolean currentWorldOnly;
   private MinMaxBounds.FloatBound distance = MinMaxBounds.FloatBound.UNBOUNDED;
   private MinMaxBounds.IntBound level = MinMaxBounds.IntBound.UNBOUNDED;
   @Nullable
   private Double x;
   @Nullable
   private Double y;
   @Nullable
   private Double z;
   @Nullable
   private Double dx;
   @Nullable
   private Double dy;
   @Nullable
   private Double dz;
   private MinMaxBoundsWrapped xRotation = MinMaxBoundsWrapped.UNBOUNDED;
   private MinMaxBoundsWrapped yRotation = MinMaxBoundsWrapped.UNBOUNDED;
   private Predicate<Entity> filter = (p_197375_0_) -> {
      return true;
   };
   private BiConsumer<Vec3d, List<? extends Entity>> sorter = ARBITRARY;
   private boolean self;
   @Nullable
   private String username;
   private int cursorStart;
   @Nullable
   private UUID uuid;
   private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionHandler = SUGGEST_NONE;
   private boolean hasNameEquals;
   private boolean hasNameNotEquals;
   private boolean isLimited;
   private boolean isSorted;
   private boolean hasGamemodeEquals;
   private boolean hasGamemodeNotEquals;
   private boolean hasTeamEquals;
   private boolean hasTeamNotEquals;
   @Nullable
   private EntityType<?> type;
   private boolean typeInverse;
   private boolean hasScores;
   private boolean hasAdvancements;
   private boolean checkPermission;

   public EntitySelectorParser(StringReader readerIn) {
      this(readerIn, true);
   }

   public EntitySelectorParser(StringReader readerIn, boolean hasPermissionIn) {
      this.reader = readerIn;
      this.hasPermission = hasPermissionIn;
   }

   public EntitySelector build() {
      AxisAlignedBB axisalignedbb;
      if (this.dx == null && this.dy == null && this.dz == null) {
         if (this.distance.getMax() != null) {
            float f = this.distance.getMax();
            axisalignedbb = new AxisAlignedBB((double)(-f), (double)(-f), (double)(-f), (double)(f + 1.0F), (double)(f + 1.0F), (double)(f + 1.0F));
         } else {
            axisalignedbb = null;
         }
      } else {
         axisalignedbb = this.createAABB(this.dx == null ? 0.0D : this.dx, this.dy == null ? 0.0D : this.dy, this.dz == null ? 0.0D : this.dz);
      }

      Function<Vec3d, Vec3d> function;
      if (this.x == null && this.y == null && this.z == null) {
         function = (p_197379_0_) -> {
            return p_197379_0_;
         };
      } else {
         function = (p_197367_1_) -> {
            return new Vec3d(this.x == null ? p_197367_1_.x : this.x, this.y == null ? p_197367_1_.y : this.y, this.z == null ? p_197367_1_.z : this.z);
         };
      }

      return new EntitySelector(this.limit, this.includeNonPlayers, this.currentWorldOnly, this.filter, this.distance, function, axisalignedbb, this.sorter, this.self, this.username, this.uuid, this.type, this.checkPermission);
   }

   private AxisAlignedBB createAABB(double sizeX, double sizeY, double sizeZ) {
      boolean flag = sizeX < 0.0D;
      boolean flag1 = sizeY < 0.0D;
      boolean flag2 = sizeZ < 0.0D;
      double d0 = flag ? sizeX : 0.0D;
      double d1 = flag1 ? sizeY : 0.0D;
      double d2 = flag2 ? sizeZ : 0.0D;
      double d3 = (flag ? 0.0D : sizeX) + 1.0D;
      double d4 = (flag1 ? 0.0D : sizeY) + 1.0D;
      double d5 = (flag2 ? 0.0D : sizeZ) + 1.0D;
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public void updateFilter() {
      if (this.xRotation != MinMaxBoundsWrapped.UNBOUNDED) {
         this.filter = this.filter.and(this.createRotationPredicate(this.xRotation, (p_197386_0_) -> {
            return (double)p_197386_0_.rotationPitch;
         }));
      }

      if (this.yRotation != MinMaxBoundsWrapped.UNBOUNDED) {
         this.filter = this.filter.and(this.createRotationPredicate(this.yRotation, (p_197385_0_) -> {
            return (double)p_197385_0_.rotationYaw;
         }));
      }

      if (!this.level.isUnbounded()) {
         this.filter = this.filter.and((p_197371_1_) -> {
            return !(p_197371_1_ instanceof ServerPlayerEntity) ? false : this.level.test(((ServerPlayerEntity)p_197371_1_).experienceLevel);
         });
      }

   }

   private Predicate<Entity> createRotationPredicate(MinMaxBoundsWrapped angleBounds, ToDoubleFunction<Entity> angleFunc) {
      double d0 = (double)MathHelper.wrapDegrees(angleBounds.getMin() == null ? 0.0F : angleBounds.getMin());
      double d1 = (double)MathHelper.wrapDegrees(angleBounds.getMax() == null ? 359.0F : angleBounds.getMax());
      return (p_197374_5_) -> {
         double d2 = MathHelper.wrapDegrees(angleFunc.applyAsDouble(p_197374_5_));
         if (d0 > d1) {
            return d2 >= d0 || d2 <= d1;
         } else {
            return d2 >= d0 && d2 <= d1;
         }
      };
   }

   protected void parseSelector() throws CommandSyntaxException {
      this.checkPermission = true;
      this.suggestionHandler = this::suggestSelector;
      if (!this.reader.canRead()) {
         throw SELECTOR_TYPE_MISSING.createWithContext(this.reader);
      } else {
         int i = this.reader.getCursor();
         char c0 = this.reader.read();
         if (c0 == 'p') {
            this.limit = 1;
            this.includeNonPlayers = false;
            this.sorter = NEAREST;
            this.func_218114_a(EntityType.PLAYER);
         } else if (c0 == 'a') {
            this.limit = Integer.MAX_VALUE;
            this.includeNonPlayers = false;
            this.sorter = ARBITRARY;
            this.func_218114_a(EntityType.PLAYER);
         } else if (c0 == 'r') {
            this.limit = 1;
            this.includeNonPlayers = false;
            this.sorter = RANDOM;
            this.func_218114_a(EntityType.PLAYER);
         } else if (c0 == 's') {
            this.limit = 1;
            this.includeNonPlayers = true;
            this.self = true;
         } else {
            if (c0 != 'e') {
               this.reader.setCursor(i);
               throw UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, '@' + String.valueOf(c0));
            }

            this.limit = Integer.MAX_VALUE;
            this.includeNonPlayers = true;
            this.sorter = ARBITRARY;
            this.filter = Entity::isAlive;
         }

         this.suggestionHandler = this::suggestOpenBracket;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestionHandler = this::suggestOptionsOrEnd;
            this.parseArguments();
         }

      }
   }

   protected void parseSingleEntity() throws CommandSyntaxException {
      if (this.reader.canRead()) {
         this.suggestionHandler = this::suggestName;
      }

      int i = this.reader.getCursor();
      String s = this.reader.readString();

      try {
         this.uuid = UUID.fromString(s);
         this.includeNonPlayers = true;
      } catch (IllegalArgumentException var4) {
         if (s.isEmpty() || s.length() > 16) {
            this.reader.setCursor(i);
            throw INVALID_ENTITY_NAME_OR_UUID.createWithContext(this.reader);
         }

         this.includeNonPlayers = false;
         this.username = s;
      }

      this.limit = 1;
   }

   public void parseArguments() throws CommandSyntaxException {
      this.suggestionHandler = this::suggestOptions;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String s = this.reader.readString();
            EntityOptions.IFilter entityoptions$ifilter = EntityOptions.get(this, s, i);
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               this.reader.setCursor(i);
               throw EXPECTED_VALUE_FOR_OPTION.createWithContext(this.reader, s);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestionHandler = SUGGEST_NONE;
            entityoptions$ifilter.handle(this);
            this.reader.skipWhitespace();
            this.suggestionHandler = this::suggestCommaOrEnd;
            if (!this.reader.canRead()) {
               continue;
            }

            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestionHandler = this::suggestOptions;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            this.suggestionHandler = SUGGEST_NONE;
            return;
         }

         throw EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
      }
   }

   public boolean shouldInvertValue() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == '!') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public boolean func_218115_f() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public StringReader getReader() {
      return this.reader;
   }

   public void addFilter(Predicate<Entity> filterIn) {
      this.filter = this.filter.and(filterIn);
   }

   public void setCurrentWorldOnly() {
      this.currentWorldOnly = true;
   }

   public MinMaxBounds.FloatBound getDistance() {
      return this.distance;
   }

   public void setDistance(MinMaxBounds.FloatBound distanceIn) {
      this.distance = distanceIn;
   }

   public MinMaxBounds.IntBound getLevel() {
      return this.level;
   }

   public void setLevel(MinMaxBounds.IntBound levelIn) {
      this.level = levelIn;
   }

   public MinMaxBoundsWrapped getXRotation() {
      return this.xRotation;
   }

   public void setXRotation(MinMaxBoundsWrapped xRotationIn) {
      this.xRotation = xRotationIn;
   }

   public MinMaxBoundsWrapped getYRotation() {
      return this.yRotation;
   }

   public void setYRotation(MinMaxBoundsWrapped yRotationIn) {
      this.yRotation = yRotationIn;
   }

   @Nullable
   public Double getX() {
      return this.x;
   }

   @Nullable
   public Double getY() {
      return this.y;
   }

   @Nullable
   public Double getZ() {
      return this.z;
   }

   public void setX(double xIn) {
      this.x = xIn;
   }

   public void setY(double yIn) {
      this.y = yIn;
   }

   public void setZ(double zIn) {
      this.z = zIn;
   }

   public void setDx(double dxIn) {
      this.dx = dxIn;
   }

   public void setDy(double dyIn) {
      this.dy = dyIn;
   }

   public void setDz(double dzIn) {
      this.dz = dzIn;
   }

   @Nullable
   public Double getDx() {
      return this.dx;
   }

   @Nullable
   public Double getDy() {
      return this.dy;
   }

   @Nullable
   public Double getDz() {
      return this.dz;
   }

   public void setLimit(int limitIn) {
      this.limit = limitIn;
   }

   public void setIncludeNonPlayers(boolean includeNonPlayersIn) {
      this.includeNonPlayers = includeNonPlayersIn;
   }

   public void setSorter(BiConsumer<Vec3d, List<? extends Entity>> sorterIn) {
      this.sorter = sorterIn;
   }

   public EntitySelector parse() throws CommandSyntaxException {
      this.cursorStart = this.reader.getCursor();
      this.suggestionHandler = this::suggestNameOrSelector;
      if (this.reader.canRead() && this.reader.peek() == '@') {
         if (!this.hasPermission) {
            throw SELECTOR_NOT_ALLOWED.createWithContext(this.reader);
         }

         this.reader.skip();
         EntitySelector forgeSelector = net.minecraftforge.common.command.EntitySelectorManager.parseSelector(this);
         if (forgeSelector != null)
            return forgeSelector;
         this.parseSelector();
      } else {
         this.parseSingleEntity();
      }

      this.updateFilter();
      return this.build();
   }

   private static void fillSelectorSuggestions(SuggestionsBuilder suggestionBuilder) {
      suggestionBuilder.suggest("@p", new TranslationTextComponent("argument.entity.selector.nearestPlayer"));
      suggestionBuilder.suggest("@a", new TranslationTextComponent("argument.entity.selector.allPlayers"));
      suggestionBuilder.suggest("@r", new TranslationTextComponent("argument.entity.selector.randomPlayer"));
      suggestionBuilder.suggest("@s", new TranslationTextComponent("argument.entity.selector.self"));
      suggestionBuilder.suggest("@e", new TranslationTextComponent("argument.entity.selector.allEntities"));
      net.minecraftforge.common.command.EntitySelectorManager.fillSelectorSuggestions(suggestionBuilder);
   }

   private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder suggestionBuilder, Consumer<SuggestionsBuilder> consumer) {
      consumer.accept(suggestionBuilder);
      if (this.hasPermission) {
         fillSelectorSuggestions(suggestionBuilder);
      }

      return suggestionBuilder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
      SuggestionsBuilder suggestionsbuilder = builder.createOffset(this.cursorStart);
      consumer.accept(suggestionsbuilder);
      return builder.add(suggestionsbuilder).buildFuture();
   }

   private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
      SuggestionsBuilder suggestionsbuilder = builder.createOffset(builder.getStart() - 1);
      fillSelectorSuggestions(suggestionsbuilder);
      builder.add(suggestionsbuilder);
      return builder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenBracket(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
      builder.suggest(String.valueOf('['));
      return builder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsOrEnd(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
      builder.suggest(String.valueOf(']'));
      EntityOptions.suggestOptions(this, builder);
      return builder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
      EntityOptions.suggestOptions(this, builder);
      return builder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestCommaOrEnd(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
      builder.suggest(String.valueOf(','));
      builder.suggest(String.valueOf(']'));
      return builder.buildFuture();
   }

   public boolean isCurrentEntity() {
      return this.self;
   }

   public void setSuggestionHandler(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionHandlerIn) {
      this.suggestionHandler = suggestionHandlerIn;
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
      return this.suggestionHandler.apply(builder.createOffset(this.reader.getCursor()), consumer);
   }

   public boolean hasNameEquals() {
      return this.hasNameEquals;
   }

   public void setHasNameEquals(boolean value) {
      this.hasNameEquals = value;
   }

   public boolean hasNameNotEquals() {
      return this.hasNameNotEquals;
   }

   public void setHasNameNotEquals(boolean value) {
      this.hasNameNotEquals = value;
   }

   public boolean isLimited() {
      return this.isLimited;
   }

   public void setLimited(boolean value) {
      this.isLimited = value;
   }

   public boolean isSorted() {
      return this.isSorted;
   }

   public void setSorted(boolean value) {
      this.isSorted = value;
   }

   public boolean hasGamemodeEquals() {
      return this.hasGamemodeEquals;
   }

   public void setHasGamemodeEquals(boolean value) {
      this.hasGamemodeEquals = value;
   }

   public boolean hasGamemodeNotEquals() {
      return this.hasGamemodeNotEquals;
   }

   public void setHasGamemodeNotEquals(boolean value) {
      this.hasGamemodeNotEquals = value;
   }

   public boolean hasTeamEquals() {
      return this.hasTeamEquals;
   }

   public void setHasTeamEquals(boolean value) {
      this.hasTeamEquals = value;
   }

   public void setHasTeamNotEquals(boolean value) {
      this.hasTeamNotEquals = value;
   }

   public void func_218114_a(EntityType<?> p_218114_1_) {
      this.type = p_218114_1_;
   }

   public void setTypeLimitedInversely() {
      this.typeInverse = true;
   }

   public boolean isTypeLimited() {
      return this.type != null;
   }

   public boolean isTypeLimitedInversely() {
      return this.typeInverse;
   }

   public boolean hasScores() {
      return this.hasScores;
   }

   public void setHasScores(boolean value) {
      this.hasScores = value;
   }

   public boolean hasAdvancements() {
      return this.hasAdvancements;
   }

   public void setHasAdvancements(boolean value) {
      this.hasAdvancements = value;
   }
}