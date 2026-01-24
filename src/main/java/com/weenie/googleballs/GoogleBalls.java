package com.weenie.googleballs;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
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

@Mod(GoogleBalls.MODID)
public class GoogleBalls
{
    public static final String MODID = "googleballs";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final DeferredRegister<net.minecraft.world.entity.EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<net.minecraft.world.level.block.entity.BannerPattern> BANNER_PATTERNS = DeferredRegister.create(Registries.BANNER_PATTERN, MODID);

    public static final RegistryObject<MobEffect> BOUNCY_EFFECT = MOB_EFFECTS.register("bouncy", BallClasses.BouncyEffect::new);

    public static final RegistryObject<net.minecraft.world.level.block.entity.BannerPattern> GOOGLE_BALLS_PATTERN = 
        BANNER_PATTERNS.register("google_balls", () -> new net.minecraft.world.level.block.entity.BannerPattern("gbl"));
    public static final RegistryObject<net.minecraft.world.level.block.entity.BannerPattern> BLUE_BALL_PATTERN = 
        BANNER_PATTERNS.register("blue_ball", () -> new net.minecraft.world.level.block.entity.BannerPattern("bbl"));
    public static final RegistryObject<net.minecraft.world.level.block.entity.BannerPattern> YELLOW_BALL_PATTERN = 
        BANNER_PATTERNS.register("yellow_ball", () -> new net.minecraft.world.level.block.entity.BannerPattern("ybl"));
    public static final RegistryObject<net.minecraft.world.level.block.entity.BannerPattern> RED_BALL_PATTERN = 
        BANNER_PATTERNS.register("red_ball", () -> new net.minecraft.world.level.block.entity.BannerPattern("rbl"));
    public static final RegistryObject<net.minecraft.world.level.block.entity.BannerPattern> GREEN_BALL_PATTERN = 
        BANNER_PATTERNS.register("green_ball", () -> new net.minecraft.world.level.block.entity.BannerPattern("gnbl"));

    public static final RegistryObject<SoundEvent> BALL_CLICK = SOUND_EVENTS.register("ball_click",
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "ball_click")));

    public static final RegistryObject<net.minecraft.world.entity.EntityType<BallClasses.ThrownGoogleBall>> THROWN_GOOGLE_BALL_TYPE = 
        ENTITY_TYPES.register("thrown_google_ball", () -> 
            net.minecraft.world.entity.EntityType.Builder.<BallClasses.ThrownGoogleBall>of(
                (type, level) -> new BallClasses.ThrownGoogleBall(type, level), 
                net.minecraft.world.entity.MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build("thrown_google_ball"));

    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new BallClasses.RegularBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Block> GOOGLEBALLS_BLOCK = BLOCKS.register("googleballs_block", () -> new BallClasses.RegularBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noOcclusion().strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> GOOGLEBALLS_BLOCK_ITEM = ITEMS.register("googleballs_block", () -> new BlockItem(GOOGLEBALLS_BLOCK.get(), new Item.Properties()));
    
    public static final RegistryObject<Block> BLUEGOOGLE_BALL = BLOCKS.register("blue_google_ball", () -> new BallClasses.GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> BLUEGOOGLE_BALL_ITEM = ITEMS.register("blue_google_ball", () -> new BallClasses.ThrowableGoogleBallItem(BLUEGOOGLE_BALL.get(), new Item.Properties()));
    public static final RegistryObject<Block> YELLOWGOOGLE_BALL = BLOCKS.register("yellow_google_ball", () -> new BallClasses.GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> YELLOWGOOGLE_BALL_ITEM = ITEMS.register("yellow_google_ball", () -> new BallClasses.ThrowableGoogleBallItem(YELLOWGOOGLE_BALL.get(), new Item.Properties()));
    public static final RegistryObject<Block> REDGOOGLE_BALL = BLOCKS.register("red_google_ball", () -> new BallClasses.GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> REDGOOGLE_BALL_ITEM = ITEMS.register("red_google_ball", () -> new BallClasses.ThrowableGoogleBallItem(REDGOOGLE_BALL.get(), new Item.Properties()));
    public static final RegistryObject<Block> GREENGOOGLE_BALL = BLOCKS.register("green_google_ball", () -> new BallClasses.GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> GREENGOOGLE_BALL_ITEM = ITEMS.register("green_google_ball", () -> new BallClasses.ThrowableGoogleBallItem(GREENGOOGLE_BALL.get(), new Item.Properties()));
    // the purple google ball. intentionally unobtainable without /give.
    public static final RegistryObject<Block> PURPLEGOOGLE_BALL = BLOCKS.register("purple_google_ball", () -> new BallClasses.GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 6.0f)));
    public static final RegistryObject<Item> PURPLEGOOGLE_BALL_ITEM = ITEMS.register("purple_google_ball", () -> new BlockItem(PURPLEGOOGLE_BALL.get(), new Item.Properties()));

    public static final RegistryObject<Block> BLUE_GOOGLE_BALL_CROP = BLOCKS.register("blue_google_ball_crop", 
        () -> new BallClasses.GoogleBallCropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(net.minecraft.world.level.block.SoundType.CROP), BLUEGOOGLE_BALL.get()));
    public static final RegistryObject<Block> YELLOW_GOOGLE_BALL_CROP = BLOCKS.register("yellow_google_ball_crop", 
        () -> new BallClasses.GoogleBallCropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(net.minecraft.world.level.block.SoundType.CROP), YELLOWGOOGLE_BALL.get()));
    public static final RegistryObject<Block> RED_GOOGLE_BALL_CROP = BLOCKS.register("red_google_ball_crop", 
        () -> new BallClasses.GoogleBallCropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(net.minecraft.world.level.block.SoundType.CROP), REDGOOGLE_BALL.get()));
    public static final RegistryObject<Block> GREEN_GOOGLE_BALL_CROP = BLOCKS.register("green_google_ball_crop", 
        () -> new BallClasses.GoogleBallCropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(net.minecraft.world.level.block.SoundType.CROP), GREENGOOGLE_BALL.get()));

    public static final RegistryObject<Item> BLUE_GOOGLE_BALL_SEEDS = ITEMS.register("blue_google_ball_seeds", 
        () -> new ItemNameBlockItem(BLUE_GOOGLE_BALL_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_GOOGLE_BALL_SEEDS = ITEMS.register("yellow_google_ball_seeds", 
        () -> new ItemNameBlockItem(YELLOW_GOOGLE_BALL_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> RED_GOOGLE_BALL_SEEDS = ITEMS.register("red_google_ball_seeds", 
        () -> new ItemNameBlockItem(RED_GOOGLE_BALL_CROP.get(), new Item.Properties()));
    public static final RegistryObject<Item> GREEN_GOOGLE_BALL_SEEDS = ITEMS.register("green_google_ball_seeds", 
        () -> new ItemNameBlockItem(GREEN_GOOGLE_BALL_CROP.get(), new Item.Properties()));

    public static final RegistryObject<Item> GOOGLE_BALLS_BANNER_PATTERN = ITEMS.register("google_balls_banner_pattern",
        () -> new net.minecraft.world.item.BannerPatternItem(net.minecraft.tags.TagKey.create(Registries.BANNER_PATTERN, 
            new ResourceLocation(MODID, "pattern_item/google_balls")), new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLUE_BALL_BANNER_PATTERN = ITEMS.register("blue_ball_banner_pattern",
        () -> new net.minecraft.world.item.BannerPatternItem(net.minecraft.tags.TagKey.create(Registries.BANNER_PATTERN, 
            new ResourceLocation(MODID, "pattern_item/blue_ball")), new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> YELLOW_BALL_BANNER_PATTERN = ITEMS.register("yellow_ball_banner_pattern",
        () -> new net.minecraft.world.item.BannerPatternItem(net.minecraft.tags.TagKey.create(Registries.BANNER_PATTERN, 
            new ResourceLocation(MODID, "pattern_item/yellow_ball")), new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RED_BALL_BANNER_PATTERN = ITEMS.register("red_ball_banner_pattern",
        () -> new net.minecraft.world.item.BannerPatternItem(net.minecraft.tags.TagKey.create(Registries.BANNER_PATTERN, 
            new ResourceLocation(MODID, "pattern_item/red_ball")), new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GREEN_BALL_BANNER_PATTERN = ITEMS.register("green_ball_banner_pattern",
        () -> new net.minecraft.world.item.BannerPatternItem(net.minecraft.tags.TagKey.create(Registries.BANNER_PATTERN, 
            new ResourceLocation(MODID, "pattern_item/green_ball")), new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    public static final RegistryObject<Item> BALLS_BOWL = ITEMS.register("bowl_of_google_balls", () -> new BallClasses.BowlOfGoogleBallsItem(new Item.Properties().food(new FoodProperties.Builder()
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
                output.accept(BLUE_GOOGLE_BALL_SEEDS.get());
                output.accept(YELLOW_GOOGLE_BALL_SEEDS.get());
                output.accept(RED_GOOGLE_BALL_SEEDS.get());
                output.accept(GREEN_GOOGLE_BALL_SEEDS.get());
                output.accept(GOOGLE_BALLS_BANNER_PATTERN.get());
                output.accept(BLUE_BALL_BANNER_PATTERN.get());
                output.accept(YELLOW_BALL_BANNER_PATTERN.get());
                output.accept(RED_BALL_BANNER_PATTERN.get());
                output.accept(GREEN_BALL_BANNER_PATTERN.get());
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
        ENTITY_TYPES.register(modEventBus);
        BANNER_PATTERNS.register(modEventBus);

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

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                net.minecraft.client.renderer.entity.EntityRenderers.register(
                    THROWN_GOOGLE_BALL_TYPE.get(), 
                    net.minecraft.client.renderer.entity.ThrownItemRenderer::new
                );
                
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                    BLUE_GOOGLE_BALL_CROP.get(), 
                    net.minecraft.client.renderer.RenderType.cutout()
                );
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                    YELLOW_GOOGLE_BALL_CROP.get(), 
                    net.minecraft.client.renderer.RenderType.cutout()
                );
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                    RED_GOOGLE_BALL_CROP.get(), 
                    net.minecraft.client.renderer.RenderType.cutout()
                );
                net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                    GREEN_GOOGLE_BALL_CROP.get(), 
                    net.minecraft.client.renderer.RenderType.cutout()
                );
            });
        }
    }

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