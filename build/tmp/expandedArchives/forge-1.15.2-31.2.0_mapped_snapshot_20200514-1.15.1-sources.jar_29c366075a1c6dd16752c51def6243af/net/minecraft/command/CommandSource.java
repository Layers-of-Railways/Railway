package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class CommandSource implements ISuggestionProvider {
   public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION_TYPE = new SimpleCommandExceptionType(new TranslationTextComponent("permissions.requires.player"));
   public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION_TYPE = new SimpleCommandExceptionType(new TranslationTextComponent("permissions.requires.entity"));
   private final ICommandSource source;
   private final Vec3d pos;
   private final ServerWorld world;
   private final int permissionLevel;
   private final String name;
   private final ITextComponent displayName;
   private final MinecraftServer server;
   private final boolean feedbackDisabled;
   @Nullable
   private final Entity entity;
   private final ResultConsumer<CommandSource> resultConsumer;
   private final EntityAnchorArgument.Type entityAnchorType;
   private final Vec2f rotation;

   public CommandSource(ICommandSource sourceIn, Vec3d posIn, Vec2f rotationIn, ServerWorld worldIn, int permissionLevelIn, String nameIn, ITextComponent displayNameIn, MinecraftServer serverIn, @Nullable Entity entityIn) {
      this(sourceIn, posIn, rotationIn, worldIn, permissionLevelIn, nameIn, displayNameIn, serverIn, entityIn, false, (p_197032_0_, p_197032_1_, p_197032_2_) -> {
      }, EntityAnchorArgument.Type.FEET);
   }

   protected CommandSource(ICommandSource sourceIn, Vec3d posIn, Vec2f rotationIn, ServerWorld worldIn, int permissionLevelIn, String nameIn, ITextComponent displayNameIn, MinecraftServer serverIn, @Nullable Entity entityIn, boolean feedbackDisabledIn, ResultConsumer<CommandSource> resultConsumerIn, EntityAnchorArgument.Type entityAnchorTypeIn) {
      this.source = sourceIn;
      this.pos = posIn;
      this.world = worldIn;
      this.feedbackDisabled = feedbackDisabledIn;
      this.entity = entityIn;
      this.permissionLevel = permissionLevelIn;
      this.name = nameIn;
      this.displayName = displayNameIn;
      this.server = serverIn;
      this.resultConsumer = resultConsumerIn;
      this.entityAnchorType = entityAnchorTypeIn;
      this.rotation = rotationIn;
   }

   public CommandSource withEntity(Entity entityIn) {
      return this.entity == entityIn ? this : new CommandSource(this.source, this.pos, this.rotation, this.world, this.permissionLevel, entityIn.getName().getString(), entityIn.getDisplayName(), this.server, entityIn, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withPos(Vec3d posIn) {
      return this.pos.equals(posIn) ? this : new CommandSource(this.source, posIn, this.rotation, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withRotation(Vec2f pitchYawIn) {
      return this.rotation.equals(pitchYawIn) ? this : new CommandSource(this.source, this.pos, pitchYawIn, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withResultConsumer(ResultConsumer<CommandSource> resultConsumerIn) {
      return this.resultConsumer.equals(resultConsumerIn) ? this : new CommandSource(this.source, this.pos, this.rotation, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, resultConsumerIn, this.entityAnchorType);
   }

   public CommandSource withResultConsumer(ResultConsumer<CommandSource> resultConsumerIn, BinaryOperator<ResultConsumer<CommandSource>> resultConsumerSelector) {
      ResultConsumer<CommandSource> resultconsumer = resultConsumerSelector.apply(this.resultConsumer, resultConsumerIn);
      return this.withResultConsumer(resultconsumer);
   }

   public CommandSource withFeedbackDisabled() {
      return this.feedbackDisabled ? this : new CommandSource(this.source, this.pos, this.rotation, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, true, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withPermissionLevel(int level) {
      return level == this.permissionLevel ? this : new CommandSource(this.source, this.pos, this.rotation, this.world, level, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withMinPermissionLevel(int level) {
      return level <= this.permissionLevel ? this : new CommandSource(this.source, this.pos, this.rotation, this.world, level, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withEntityAnchorType(EntityAnchorArgument.Type entityAnchorTypeIn) {
      return entityAnchorTypeIn == this.entityAnchorType ? this : new CommandSource(this.source, this.pos, this.rotation, this.world, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, entityAnchorTypeIn);
   }

   public CommandSource withWorld(ServerWorld worldIn) {
      return worldIn == this.world ? this : new CommandSource(this.source, this.pos, this.rotation, worldIn, this.permissionLevel, this.name, this.displayName, this.server, this.entity, this.feedbackDisabled, this.resultConsumer, this.entityAnchorType);
   }

   public CommandSource withRotation(Entity entityIn, EntityAnchorArgument.Type anchorType) throws CommandSyntaxException {
      return this.withRotation(anchorType.apply(entityIn));
   }

   public CommandSource withRotation(Vec3d lookPos) throws CommandSyntaxException {
      Vec3d vec3d = this.entityAnchorType.apply(this);
      double d0 = lookPos.x - vec3d.x;
      double d1 = lookPos.y - vec3d.y;
      double d2 = lookPos.z - vec3d.z;
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      float f = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
      float f1 = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
      return this.withRotation(new Vec2f(f, f1));
   }

   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   public String getName() {
      return this.name;
   }

   public boolean hasPermissionLevel(int level) {
      return this.permissionLevel >= level;
   }

   public Vec3d getPos() {
      return this.pos;
   }

   public ServerWorld getWorld() {
      return this.world;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public Entity assertIsEntity() throws CommandSyntaxException {
      if (this.entity == null) {
         throw REQUIRES_ENTITY_EXCEPTION_TYPE.create();
      } else {
         return this.entity;
      }
   }

   public ServerPlayerEntity asPlayer() throws CommandSyntaxException {
      if (!(this.entity instanceof ServerPlayerEntity)) {
         throw REQUIRES_PLAYER_EXCEPTION_TYPE.create();
      } else {
         return (ServerPlayerEntity)this.entity;
      }
   }

   public Vec2f getRotation() {
      return this.rotation;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityAnchorArgument.Type getEntityAnchorType() {
      return this.entityAnchorType;
   }

   public void sendFeedback(ITextComponent message, boolean allowLogging) {
      if (this.source.shouldReceiveFeedback() && !this.feedbackDisabled) {
         this.source.sendMessage(message);
      }

      if (allowLogging && this.source.allowLogging() && !this.feedbackDisabled) {
         this.logFeedback(message);
      }

   }

   private void logFeedback(ITextComponent message) {
      ITextComponent itextcomponent = (new TranslationTextComponent("chat.type.admin", this.getDisplayName(), message)).applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC});
      if (this.server.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
         for(ServerPlayerEntity serverplayerentity : this.server.getPlayerList().getPlayers()) {
            if (serverplayerentity != this.source && this.server.getPlayerList().canSendCommands(serverplayerentity.getGameProfile())) {
               serverplayerentity.sendMessage(itextcomponent);
            }
         }
      }

      if (this.source != this.server && this.server.getGameRules().getBoolean(GameRules.LOG_ADMIN_COMMANDS)) {
         this.server.sendMessage(itextcomponent);
      }

   }

   public void sendErrorMessage(ITextComponent message) {
      if (this.source.shouldReceiveErrors() && !this.feedbackDisabled) {
         this.source.sendMessage((new StringTextComponent("")).appendSibling(message).applyTextStyle(TextFormatting.RED));
      }

   }

   public void onCommandComplete(CommandContext<CommandSource> context, boolean success, int result) {
      if (this.resultConsumer != null) {
         this.resultConsumer.onCommandComplete(context, success, result);
      }

   }

   public Collection<String> getPlayerNames() {
      return Lists.newArrayList(this.server.getOnlinePlayerNames());
   }

   public Collection<String> getTeamNames() {
      return this.server.getScoreboard().getTeamNames();
   }

   public Collection<ResourceLocation> getSoundResourceLocations() {
      return Registry.SOUND_EVENT.keySet();
   }

   public Stream<ResourceLocation> getRecipeResourceLocations() {
      return this.server.getRecipeManager().getKeys();
   }

   public CompletableFuture<Suggestions> getSuggestionsFromServer(CommandContext<ISuggestionProvider> context, SuggestionsBuilder suggestionsBuilder) {
      return null;
   }
}