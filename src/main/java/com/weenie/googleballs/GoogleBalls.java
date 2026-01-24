package com.weenie.googleballs;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;
import javax.annotation.Nullable;
import java.util.Collections;

@Mod(GoogleBalls.MODID)
public class GoogleBalls
{
    // classes and stuff
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

    // regular blocks in the mod but without that funny custom model stuff the google balls block has
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

        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable("item.googleballs.bowl_of_google_balls.desc").withStyle(ChatFormatting.GRAY));
            super.appendHoverText(stack, level, tooltip, flag);
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
            ItemStack result = super.finishUsingItem(stack, level, entity);
            
            if (entity instanceof Player player) {
                player.addEffect(new MobEffectInstance(BOUNCY_EFFECT.get(), 6000, 0));
                
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

    // end of class stuff

    public static final String MODID = "googleballs";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    public static final RegistryObject<MobEffect> BOUNCY_EFFECT = MOB_EFFECTS.register("bouncy", BouncyEffect::new);

    public static final RegistryObject<SoundEvent> BALL_CLICK = SOUND_EVENTS.register("ball_click",
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "ball_click")));

    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new RegularBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Block> GOOGLEBALLS_BLOCK = BLOCKS.register("googleballs_block", () -> new RegularBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noOcclusion().strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> GOOGLEBALLS_BLOCK_ITEM = ITEMS.register("googleballs_block", () -> new BlockItem(GOOGLEBALLS_BLOCK.get(), new Item.Properties()));
    
    public static final RegistryObject<Block> BLUEGOOGLE_BALL = BLOCKS.register("blue_google_ball", () -> new GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> BLUEGOOGLE_BALL_ITEM = ITEMS.register("blue_google_ball", () -> new BlockItem(BLUEGOOGLE_BALL.get(), new Item.Properties()));
    public static final RegistryObject<Block> YELLOWGOOGLE_BALL = BLOCKS.register("yellow_google_ball", () -> new GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> YELLOWGOOGLE_BALL_ITEM = ITEMS.register("yellow_google_ball", () -> new BlockItem(YELLOWGOOGLE_BALL.get(), new Item.Properties()));
    public static final RegistryObject<Block> REDGOOGLE_BALL = BLOCKS.register("red_google_ball", () -> new GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> REDGOOGLE_BALL_ITEM = ITEMS.register("red_google_ball", () -> new BlockItem(REDGOOGLE_BALL.get(), new Item.Properties()));
    public static final RegistryObject<Block> GREENGOOGLE_BALL = BLOCKS.register("green_google_ball", () -> new GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> GREENGOOGLE_BALL_ITEM = ITEMS.register("green_google_ball", () -> new BlockItem(GREENGOOGLE_BALL.get(), new Item.Properties()));
    // the purple google ball. intentionally unobtainable without /give.
    public static final RegistryObject<Block> PURPLEGOOGLE_BALL = BLOCKS.register("purple_google_ball", () -> new GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> PURPLEGOOGLE_BALL_ITEM = ITEMS.register("purple_google_ball", () -> new BlockItem(PURPLEGOOGLE_BALL.get(), new Item.Properties()));

    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    public static final RegistryObject<Item> BALLS_BOWL = ITEMS.register("bowl_of_google_balls", () -> new BowlOfGoogleBallsItem(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(6).saturationMod(4f).build()).craftRemainder(Items.BOWL)));

    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("googleballs_mod_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> BLUEGOOGLE_BALL_ITEM.get().getDefaultInstance())
            .title(Component.translatable("item_group.googleballs.googleballs_mod_tab"))
            .displayItems((parameters, output) -> {
                output.accept(GOOGLEBALLS_BLOCK.get());
                output.accept(BLUEGOOGLE_BALL.get());
                output.accept(YELLOWGOOGLE_BALL.get());
                output.accept(REDGOOGLE_BALL.get());
                output.accept(GREENGOOGLE_BALL.get());
                output.accept(BALLS_BOWL.get());
            }).build());

    public GoogleBalls(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        MOB_EFFECTS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("[GOOGLEBALLS] google balling on the client");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("[GOOGLEBALLS] google balling on the server");
    }

    private static final java.util.Map<java.util.UUID, Boolean> wasOnGround = new java.util.HashMap<>();
    private static final java.util.Map<java.util.UUID, Double> storedFallVelocity = new java.util.HashMap<>();
    private static final java.util.Map<java.util.UUID, net.minecraft.world.phys.Vec3> storedHorizontalVelocity = new java.util.HashMap<>();
    private static final double MIN_BOUNCE_VELOCITY = -0.5;

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        
        if (entity.hasEffect(BOUNCY_EFFECT.get())) {
            java.util.UUID uuid = entity.getUUID();
            boolean wasOnGroundBefore = wasOnGround.getOrDefault(uuid, true);
            double currentYVel = entity.getDeltaMovement().y;
            
            if (!entity.onGround() && currentYVel < MIN_BOUNCE_VELOCITY) {
                storedFallVelocity.put(uuid, currentYVel);
                storedHorizontalVelocity.put(uuid, new net.minecraft.world.phys.Vec3(
                    entity.getDeltaMovement().x, 
                    0, 
                    entity.getDeltaMovement().z
                ));
            }
            
            if (!wasOnGroundBefore && entity.onGround()) {
                Double fallVel = storedFallVelocity.get(uuid);
                net.minecraft.world.phys.Vec3 horizontalVel = storedHorizontalVelocity.get(uuid);
                
                if (fallVel != null && fallVel < MIN_BOUNCE_VELOCITY) {
                    double bounceStrength = Math.abs(fallVel) * 0.8;
                    
                    double xVel = horizontalVel != null ? horizontalVel.x : entity.getDeltaMovement().x;
                    double zVel = horizontalVel != null ? horizontalVel.z : entity.getDeltaMovement().z;
                    
                    entity.setDeltaMovement(xVel, bounceStrength, zVel);
                    entity.hurtMarked = true;
                    
                    entity.level().playSound(null, entity.blockPosition(), 
                        BALL_CLICK.get(), SoundSource.PLAYERS, 0.8F, 1.0F + (float)(Math.random() * 0.5F));
                }
                
                storedFallVelocity.remove(uuid);
                storedHorizontalVelocity.remove(uuid);
            }
            
            wasOnGround.put(uuid, entity.onGround());
            
            entity.fallDistance = 0;
            
        } else {
            java.util.UUID uuid = entity.getUUID();
            wasOnGround.remove(uuid);
            storedFallVelocity.remove(uuid);
            storedHorizontalVelocity.remove(uuid);
        }
    }
}