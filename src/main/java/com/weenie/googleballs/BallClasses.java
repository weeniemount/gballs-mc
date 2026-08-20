package com.weenie.googleballs;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BallClasses {
    
    public static class BouncyEffect extends MobEffect {
        public BouncyEffect() {
            super(MobEffectCategory.BENEFICIAL, 0x4285F4);
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplifier) {
            // do nothing
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            return true;
        }
    }

    public static class GoogleBallBlock extends Block {
        public GoogleBallBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
            return Shapes.empty();
        }

        @Override
        public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
            return true;
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (!level.isClientSide()) {
                level.playSound(null, pos, GoogleBalls.BALL_CLICK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
            super.appendHoverText(stack, level, tooltip, flag);
        }

        @Override
        public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
            return Collections.singletonList(new ItemStack(this));
        }
    }

    public static class RegularBallBlock extends Block {
        public RegularBallBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }
        
        public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
            super.appendHoverText(stack, level, tooltip, flag);
        }

        @Override
        public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
            return Collections.singletonList(new ItemStack(this));
        }
    }

    public static class BowlOfGoogleBallsItem extends Item {
        public BowlOfGoogleBallsItem(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable("item.googleballs.bowl_of_google_balls.desc").withStyle(ChatFormatting.GRAY));
            super.appendHoverText(stack, level, tooltip, flag);
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
            ItemStack result = super.finishUsingItem(stack, level, entity);
            
            if (entity instanceof Player player) {
                player.addEffect(new MobEffectInstance(GoogleBalls.BOUNCY_EFFECT.get(), 6000, 0));
                
                if (!player.getAbilities().instabuild) {
                    if (stack.isEmpty()) {
                        return new ItemStack(Items.BOWL);
                    } else {
                        if (!player.getInventory().add(new ItemStack(Items.BOWL))) {
                            player.drop(new ItemStack(Items.BOWL), false);
                        }
                    }
                }
            }
            
            return result;
        }
    }

    public static class CookedGoogleBallItem extends Item {
        public CookedGoogleBallItem(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    }

    public static class ThrownGoogleBall extends net.minecraft.world.entity.projectile.ThrowableItemProjectile {
        private Block blockToPlace;
        
        public ThrownGoogleBall(net.minecraft.world.entity.EntityType<? extends ThrownGoogleBall> type, Level level) {
            super(type, level);
            this.blockToPlace = null;
        }
        
        public ThrownGoogleBall(Level level, LivingEntity shooter, Block blockToPlace) {
            super(GoogleBalls.THROWN_GOOGLE_BALL_TYPE.get(), shooter, level);
            this.blockToPlace = blockToPlace;
        }
        
        @Override
        protected Item getDefaultItem() {
            return Items.SNOWBALL;
        }
        
        @Override
        protected void onHit(net.minecraft.world.phys.HitResult result) {
            super.onHit(result);
            if (!this.level().isClientSide) {
                this.level().broadcastEntityEvent(this, (byte)3);
                this.level().playSound(null, this.blockPosition(), 
                    GoogleBalls.BALL_CLICK.get(), SoundSource.NEUTRAL, 0.5F, 0.8F + (float)(Math.random() * 0.4F));
                this.discard();
            }
        }
        
        @Override
        protected void onHitEntity(net.minecraft.world.phys.EntityHitResult result) {
            super.onHitEntity(result);
            if (!this.level().isClientSide) {
                net.minecraft.world.entity.Entity hitEntity = result.getEntity();
                
                hitEntity.hurt(this.damageSources().thrown(this, this.getOwner()), 1.0F);
                
                if (blockToPlace != null) {
                    this.spawnAtLocation(blockToPlace.asItem());
                }
                
                this.level().playSound(null, this.blockPosition(), 
                    GoogleBalls.BALL_CLICK.get(), SoundSource.NEUTRAL, 0.7F, 1.2F + (float)(Math.random() * 0.3F));
            }
        }
        
        @Override
        protected void onHitBlock(net.minecraft.world.phys.BlockHitResult result) {
            super.onHitBlock(result);
            if (!this.level().isClientSide && blockToPlace != null) {
                BlockPos pos = result.getBlockPos().relative(result.getDirection());
                if (this.level().getBlockState(pos).isAir()) {
                    this.level().setBlock(pos, blockToPlace.defaultBlockState(), 3);
                } else {
                    this.spawnAtLocation(blockToPlace.asItem());
                }
            }
        }
    }

    public static class GoogleBallCropBlock extends net.minecraft.world.level.block.CropBlock {
        private final Block ballBlock;
        
        public GoogleBallCropBlock(BlockBehaviour.Properties properties, Block ballBlock) {
            super(properties);
            this.ballBlock = ballBlock;
        }
        
        @Override
        protected net.minecraft.world.level.ItemLike getBaseSeedId() {
            if (ballBlock == GoogleBalls.BLUEGOOGLE_BALL.get()) {
                return GoogleBalls.BLUE_GOOGLE_BALL_SEEDS.get();
            } else if (ballBlock == GoogleBalls.YELLOWGOOGLE_BALL.get()) {
                return GoogleBalls.YELLOW_GOOGLE_BALL_SEEDS.get();
            } else if (ballBlock == GoogleBalls.REDGOOGLE_BALL.get()) {
                return GoogleBalls.RED_GOOGLE_BALL_SEEDS.get();
            } else if (ballBlock == GoogleBalls.GREENGOOGLE_BALL.get()) {
                return GoogleBalls.GREEN_GOOGLE_BALL_SEEDS.get();
            }
            return Items.WHEAT_SEEDS;
        }
        
        @Override
        public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
            List<ItemStack> drops = new java.util.ArrayList<>();
            
            if (this.isMaxAge(state)) {
                drops.add(new ItemStack(ballBlock.asItem(), 2));
                drops.add(new ItemStack(this.getBaseSeedId(), 2));
            } else {
                drops.add(new ItemStack(this.getBaseSeedId()));
            }
            
            return drops;
        }
    }

    public static class ThrowableGoogleBallItem extends BlockItem {
        public ThrowableGoogleBallItem(Block block, Properties properties) {
            super(block, properties);
        }
        
        @Override
        public InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context) {
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
                return super.useOn(context);
            }
            return InteractionResult.PASS;
        }
        
        @Override
        public net.minecraft.world.InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack itemstack = player.getItemInHand(hand);
            
            if (player.isShiftKeyDown()) {
                return net.minecraft.world.InteractionResultHolder.pass(itemstack);
            }
            
            if (!level.isClientSide) {
                ThrownGoogleBall thrownBall = new ThrownGoogleBall(level, player, this.getBlock());
                thrownBall.setItem(itemstack);
                thrownBall.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(thrownBall);
                level.playSound(null, player.blockPosition(), 
                    net.minecraft.sounds.SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.8F);
            }
            
            player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            
            return net.minecraft.world.InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }
    }

    public static class GoogleBallsOreBlock extends Block {
        public GoogleBallsOreBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }
        
        @Override
        public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
            List<ItemStack> drops = new java.util.ArrayList<>();
            
            java.util.Random rand = new java.util.Random();
            int ballType = rand.nextInt(4);
            
            Block ballToDrop;
            switch(ballType) {
                case 0: ballToDrop = GoogleBalls.BLUEGOOGLE_BALL.get(); break;
                case 1: ballToDrop = GoogleBalls.YELLOWGOOGLE_BALL.get(); break;
                case 2: ballToDrop = GoogleBalls.REDGOOGLE_BALL.get(); break;
                default: ballToDrop = GoogleBalls.GREENGOOGLE_BALL.get(); break;
            }
            
            drops.add(new ItemStack(ballToDrop.asItem(), 1));
            return drops;
        }
    }
}