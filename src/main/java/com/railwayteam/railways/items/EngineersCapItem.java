package com.railwayteam.railways.items;

import com.railwayteam.railways.entities.conductor.ConductorEntity;
import com.simibubi.create.AllBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EngineersCapItem extends ArmorItem {
    static class EngineerCapArmorMaterial implements IArmorMaterial {
        @Override
        public int getDurability(EquipmentSlotType p_200896_1_) {
            return 0;
        }

        @Override
        public int getDamageReductionAmount(EquipmentSlotType p_200902_1_) {
            return 0;
        }

        @Override
        public int getEnchantability() {
            return 0;
        }

        @Override
        public SoundEvent getSoundEvent() {
            return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairMaterial() {
            return null;
        }

        @Override
        public String getName() {
            return "engineer_cap";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    }

    @Nullable
    @Override
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        return (A) new EngineersCapModel();
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return "railways:textures/models/armor/" + color + "_golem_hat.png";
    }

    public static final String name = "engineers_cap";
    public final DyeColor color;

    public EngineersCapItem(IArmorMaterial p_i48534_1_, EquipmentSlotType p_i48534_2_, Properties p_i48534_3_,  DyeColor color) {
        super(p_i48534_1_, p_i48534_2_, p_i48534_3_);
        this.color = color;
    }

    public EngineersCapItem(Properties p, DyeColor color) {
        this(new EngineerCapArmorMaterial(), EquipmentSlotType.HEAD, p, color);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> text, ITooltipFlag p_77624_4_) {
        text.add(new StringTextComponent("Color: " + color.getTranslationKey())); // TODO: Turn this into a translatable text
        super.addInformation(p_77624_1_, p_77624_2_, text, p_77624_4_);
    }

    static Block AndesiteCasing = AllBlocks.ANDESITE_CASING.get();

    static boolean isCasing(Block block) {
        return block.equals(AndesiteCasing);
    }

    static boolean isCasing(BlockState block) {
        return isCasing(block.getBlock());
    }

    static boolean isCasing(World world, BlockPos pos) {
        return isCasing(world.getBlockState(pos));
    }

    public static BlockPos[] getBlocksToRemove(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if(!isCasing(state)) return new BlockPos[0];
        BlockPos otherBlock = pos.up();
        if(!isCasing(world, otherBlock)) otherBlock = pos.down();
        if(!isCasing(world, otherBlock)) return new BlockPos[0];
        return new BlockPos[]{pos, otherBlock};
    }

    public static BlockPos getLowest(BlockPos[] pos) {
        BlockPos lowest = pos[0];
        for(BlockPos pos1 : pos) {
            if(pos1.getY() < lowest.getY()) lowest = pos1;
        }
        return lowest;
    }

    @Override // overridden so that minecraft doesnt equip the item when right clicked, mainly so my engineer factory doesnt stop working lol
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity plr, Hand hand) {
        return ActionResult.pass(plr.getHeldItem(hand));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        World world = ctx.getWorld();
        if(!world.isRemote) {
            PlayerEntity player = ctx.getPlayer();
            Hand hand = ctx.getHand();
            ItemStack stack = ctx.getItem();

            BlockPos pos = ctx.getPos();
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            BlockPos[] blocksToRemove = getBlocksToRemove(world, pos);
            if(blocksToRemove.length > 0) {
                for(BlockPos pos1 : blocksToRemove) world.breakBlock(pos1, false, player);
//            EngineerGolemEntity golem = new EngineerGolemEntity(ModSetup.R_ENTITY_ENGINEER.get(), world);
//            golem.setPos(pos.getX(), pos.getY(), pos.getZ());
//            world.addEntity(golem);
                ConductorEntity.spawn(world, getLowest(blocksToRemove));
                if(!player.isCreative()) {
                    stack.setCount(stack.getCount() - 1);
                    return ActionResultType.CONSUME;
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onItemUse(ctx);
    }

    //    @Override
//    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
//        Vec3d from = player.getEyePosition(0);
//
//        BlockRayTraceResult result = world.rayTraceBlocks(
//                new RayTraceContext(
//                        from,
//                        from.add(player.getLookVec().scale(4)),
//                        RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, null
//                )
//        );
//
//        if (result.getType() == RayTraceResult.Type.BLOCK){
//            BlockPos pos = result.getPos();
//            BlockState blockState = world.getBlockState(pos);
//            Block block = blockState.getBlock();
//            if(block.equals(AndesiteCasing)) {
//                world.breakBlock(pos, false, null);
//                EngineerGolemEntity golem = new EngineerGolemEntity(ModSetup.R_ENTITY_ENGINEER.get(), world);
//                golem.setPos(pos.getX(), pos.getY(), pos.getZ());
//                world.addEntity(golem);
//                SpawnEggItem
//                return ActionResult.success(player.getHeldItem(hand));
//            }
//        }
//        return super.onItemRightClick(world, player, hand);
//    }
}

//class EngineersCapModel extends BipedModel<LivingEntity> {
//    private final ModelRenderer bb_main;
//    private final ModelRenderer Hat_r1;
//
//    public EngineersCapModel() {
//        super(0,0,0,0);
//        textureWidth = 64;
//        textureHeight = 64;
//
//        bb_main = new ModelRenderer(this);
//        bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
//        bb_main.setTextureOffset(39, 33).addCuboid(-12.0F, -23.0F, 12.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
//        bb_main.setTextureOffset(34, 48).addCuboid(-13.0F, -23.0F, 3.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
//        bb_main.setTextureOffset(34, 36).addCuboid(-4.0F, -23.0F, 3.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
//        bb_main.setTextureOffset(39, 30).addCuboid(-12.0F, -23.0F, 3.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
//        bb_main.setTextureOffset(32, 22).addCuboid(-12.0F, -23.0F, 4.0F, 8.0F, 1.0F, 8.0F, 0.0F, false);
//
//        Hat_r1 = new ModelRenderer(this);
//        Hat_r1.setRotationPoint(0.0F, -20.4362F, 2.096F);
////        bb_main.addChild(Hat_r1);
//        bipedHead.addChild(Hat_r1);
//        setRotationAngle(Hat_r1, 0.3927F, 0.0F, 0.0F);
//        Hat_r1.setTextureOffset(12, 46).addCuboid(-13.0F, -0.0978F, -1.5612F, 10.0F, 1.0F, 3.0F, 0.0F, false);
//    }
//
//    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
//        modelRenderer.rotateAngleX = x;
//        modelRenderer.rotateAngleY = y;
//        modelRenderer.rotateAngleZ = z;
//    }
//}

class EngineersCapModel extends BipedModel<LivingEntity> {
    private final ModelRenderer Hat;
    private final ModelRenderer Hat_r1;

    public EngineersCapModel() {
        super(0,0,0,0);
        textureWidth = 64;
        textureHeight = 64;

        Hat = new ModelRenderer(this);
        Hat.setRotationPoint(0.0F, 6.0F, 0.0F);
        Hat.setTextureOffset(39, 33).addCuboid(-4.0F, -15.0F, 4.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
        Hat.setTextureOffset(34, 48).addCuboid(-5.0F, -15.0F, -5.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
        Hat.setTextureOffset(34, 36).addCuboid(4.0F, -15.0F, -5.0F, 1.0F, 3.0F, 10.0F, 0.0F, false);
        Hat.setTextureOffset(39, 30).addCuboid(-4.0F, -15.0F, -5.0F, 8.0F, 3.0F, 1.0F, 0.0F, false);
        Hat.setTextureOffset(32, 22).addCuboid(-4.0F, -15.0F, -4.0F, 8.0F, 1.0F, 8.0F, 0.0F, false);

        Hat_r1 = new ModelRenderer(this);
        Hat_r1.setRotationPoint(8.0F, -12.4362F, -5.904F);
        Hat.addChild(Hat_r1);
        setRotationAngle(Hat_r1, 0.3927F, 0.0F, 0.0F);
        Hat_r1.setTextureOffset(12, 46).addCuboid(-13.0F, -0.0978F, -1.5612F, 10.0F, 1.0F, 3.0F, 0.0F, false);
        bipedHead.addChild(Hat);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}