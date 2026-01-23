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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
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
    }

    public static final String MODID = "googleballs";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final RegistryObject<SoundEvent> BALL_CLICK = SOUND_EVENTS.register("ball_click",
        () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "ball_click")));

    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Block> GOOGLEBALLS_BLOCK = BLOCKS.register("googleballs_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noOcclusion()));
    public static final RegistryObject<Item> GOOGLEBALLS_BLOCK_ITEM = ITEMS.register("googleballs_block", () -> new BlockItem(GOOGLEBALLS_BLOCK.get(), new Item.Properties()));
    
    public static final RegistryObject<Block> BLUEGOOGLE_BALL = BLOCKS.register("blue_google_ball", () -> new GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Item> BLUEGOOGLE_BALL_ITEM = ITEMS.register("blue_google_ball", () -> new BlockItem(BLUEGOOGLE_BALL.get(), new Item.Properties()));
    public static final RegistryObject<Block> YELLOWGOOGLE_BALL = BLOCKS.register("yellow_google_ball", () -> new GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Item> YELLOWGOOGLE_BALL_ITEM = ITEMS.register("yellow_google_ball", () -> new BlockItem(YELLOWGOOGLE_BALL.get(), new Item.Properties()));
    public static final RegistryObject<Block> REDGOOGLE_BALL = BLOCKS.register("red_google_ball", () -> new GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Item> REDGOOGLE_BALL_ITEM = ITEMS.register("red_google_ball", () -> new BlockItem(REDGOOGLE_BALL.get(), new Item.Properties()));
    public static final RegistryObject<Block> GREENGOOGLE_BALL = BLOCKS.register("green_google_ball", () -> new GoogleBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Item> GREENGOOGLE_BALL_ITEM = ITEMS.register("green_google_ball", () -> new BlockItem(GREENGOOGLE_BALL.get(), new Item.Properties()));

    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("googleballs_mod_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> BLUEGOOGLE_BALL_ITEM.get().getDefaultInstance())
            .title(Component.translatable("item_group.googleballs.googleballs_mod_tab"))
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get());
                output.accept(EXAMPLE_BLOCK.get());
                output.accept(GOOGLEBALLS_BLOCK.get());
                output.accept(BLUEGOOGLE_BALL.get());
                output.accept(YELLOWGOOGLE_BALL.get());
                output.accept(REDGOOGLE_BALL.get());
                output.accept(GREENGOOGLE_BALL.get());
            }).build());

    public GoogleBalls(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);

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
}
