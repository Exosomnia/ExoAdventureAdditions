package com.exosomnia.exoadvadditions;

import com.exosomnia.exoadvadditions.blocks.TeleporterPlateBlock;
import com.exosomnia.exoadvadditions.capabilities.daytimedilation.DaytimeDilationProvider;
import com.exosomnia.exoadvadditions.commands.AdventureStart;
import com.exosomnia.exoadvadditions.commands.LowerDifficulty;
import com.exosomnia.exoadvadditions.effects.CheatedDeathEffect;
import com.exosomnia.exoadvadditions.effects.WakefulnessEffect;
import com.exosomnia.exoadvadditions.items.*;
import com.exosomnia.exoadvadditions.loot.DepthsMacGuffinLootModifier;
import com.exosomnia.exoadvadditions.recipes.AdventureMapRecipe;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Registry {

    public final static ResourceKey<Level> DEPTHS_DIMENSION = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("exoadvadditions:the_depths"));


    //public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ExoAdventureAdditions.MODID);

    //public static final RegistryObject<Codec<DepthsMacGuffinLootModifier>> LOOT_MOD_DEPTHS_MACGUFFIN = GLOBAL_LOOT_MODS.register("loot_depths_macguffin", DepthsMacGuffinLootModifier.CODEC);


    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,
            ExoAdventureAdditions.MODID);

    public static final RegistryObject<RecipeSerializer<AdventureMapRecipe>> RECIPE_ADVENTURE_MAP = RECIPE_SERIALIZERS.register("adventure_map_crafting",
            AdventureMapRecipe.Serializer::new);


    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExoAdventureAdditions.MODID);
    public static final RegistryObject<Block> BLOCK_TELEPORTER_PLATE = BLOCKS.register("teleporter_plate", TeleporterPlateBlock::new);

    public static final RegistryObject<Block> BLOCK_NATURAL_COAL_ORE = BLOCKS.register("natural_coal_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(0, 2)));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_COAL_ORE = BLOCKS.register("natural_deepslate_coal_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_COAL_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(0, 2)));
    public static final RegistryObject<Block> BLOCK_NATURAL_IRON_ORE = BLOCKS.register("natural_iron_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_IRON_ORE = BLOCKS.register("natural_deepslate_iron_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_IRON_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final RegistryObject<Block> BLOCK_NATURAL_COPPER_ORE = BLOCKS.register("natural_copper_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_IRON_ORE.get())));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_COPPER_ORE = BLOCKS.register("natural_deepslate_copper_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_COPPER_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final RegistryObject<Block> BLOCK_NATURAL_GOLD_ORE = BLOCKS.register("natural_gold_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_GOLD_ORE = BLOCKS.register("natural_deepslate_gold_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_GOLD_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final RegistryObject<Block> BLOCK_NATURAL_LAPIS_ORE = BLOCKS.register("natural_lapis_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(2, 5)));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_LAPIS_ORE = BLOCKS.register("natural_deepslate_lapis_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_LAPIS_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(2, 5)));
    public static final RegistryObject<Block> BLOCK_NATURAL_REDSTONE_ORE = BLOCKS.register("natural_redstone_ore", () -> new RedStoneOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().randomTicks().lightLevel((state) -> state.getValue(BlockStateProperties.LIT) ? 9 : 0).strength(3.0F, 3.0F)));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_REDSTONE_ORE = BLOCKS.register("natural_deepslate_redstone_ore", () -> new RedStoneOreBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_REDSTONE_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final RegistryObject<Block> BLOCK_NATURAL_DIAMOND_ORE = BLOCKS.register("natural_diamond_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(3, 7)));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_DIAMOND_ORE = BLOCKS.register("natural_deepslate_diamond_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_DIAMOND_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)));
    public static final RegistryObject<Block> BLOCK_NATURAL_EMERALD_ORE = BLOCKS.register("natural_emerald_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F), UniformInt.of(3, 7)));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_EMERALD_ORE = BLOCKS.register("natural_deepslate_emerald_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_EMERALD_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE), UniformInt.of(3, 7)));
    public static final RegistryObject<Block> BLOCK_NATURAL_ANCIENT_DEBRIS = BLOCKS.register("natural_ancient_debris", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(30.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS)));

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExoAdventureAdditions.MODID);
    public static final RegistryObject<Item> ITEM_UNLOCATED_MAP = ITEMS.register("unlocated_map", UnlocatedMapItem::new);
    public static final RegistryObject<Item> ITEM_TOME_OF_SUNRISE = ITEMS.register("tome_of_sunrise", () -> new TomeOfSunriseItem(new Item.Properties().stacksTo(16), 1, false));
    public static final RegistryObject<Item> ITEM_ENHANCED_TOME_OF_SUNRISE = ITEMS.register("enhanced_tome_of_sunrise", () -> new TomeOfSunriseItem(new Item.Properties().stacksTo(16), 2, false));
    public static final RegistryObject<Item> ITEM_ASCENDED_TOME_OF_SUNRISE = ITEMS.register("ascended_tome_of_sunrise", () -> new TomeOfSunriseItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), 3, true));
    public static final RegistryObject<Item> ITEM_TOME_OF_WAKEFULNESS = ITEMS.register("tome_of_wakefulness", () -> new TomeOfWakefulness(new Item.Properties().stacksTo(16), 1, false));
    public static final RegistryObject<Item> ITEM_ENHANCED_TOME_OF_WAKEFULNESS = ITEMS.register("enhanced_tome_of_wakefulness", () -> new TomeOfWakefulness(new Item.Properties().stacksTo(16), 2, false));
    public static final RegistryObject<Item> ITEM_ASCENDED_TOME_OF_WAKEFULNESS = ITEMS.register("ascended_tome_of_wakefulness", () -> new TomeOfWakefulness(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), 3, true));
    public static final RegistryObject<Item> ITEM_TOME_OF_AMNESIA = ITEMS.register("tome_of_amnesia", () -> new TomeOfAmnesia(new Item.Properties().stacksTo(16), 1, false));
    public static final RegistryObject<Item> ITEM_ASCENDED_TOME_OF_AMNESIA = ITEMS.register("ascended_tome_of_amnesia", () -> new TomeOfAmnesia(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), 2, true));
    public static final RegistryObject<Item> ITEM_ETERNA_CRYSTALIS = ITEMS.register("eterna_crystalis", () -> new SimpleFoiledItem(new Item.Properties().stacksTo(64).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ITEM_NETHERITE_INGOT_STACK = ITEMS.register("netherite_ingot_stack", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_CRUSHED_ANCIENT_DEBRIS = ITEMS.register("crushed_ancient_debris", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_FIERY_INGOT = ITEMS.register("fiery_ingot", () -> new FieryIngotItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MAGICAL_RUNES = ITEMS.register("magical_runes", () -> new MagicalRunesItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> ITEM_MACGUFFIN_1 = ITEMS.register("macguffin_1", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_2 = ITEMS.register("macguffin_2", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_3 = ITEMS.register("macguffin_3", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_4 = ITEMS.register("macguffin_4", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_5 = ITEMS.register("macguffin_5", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_6 = ITEMS.register("macguffin_6", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_7 = ITEMS.register("macguffin_7", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_8 = ITEMS.register("macguffin_8", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_9 = ITEMS.register("macguffin_9", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_10 = ITEMS.register("macguffin_10", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_11 = ITEMS.register("macguffin_11", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_12 = ITEMS.register("macguffin_12", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<BlockItem> ITEM_TELEPORTER_PLATE_BLOCK = ITEMS.register("teleporter_plate", () -> new BlockItem(BLOCK_TELEPORTER_PLATE.get(), new Item.Properties()));


    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExoAdventureAdditions.MODID);
    public static final RegistryObject<CreativeModeTab> TAB_ADVENTURE_ITEMS = CREATIVE_TABS.register("adventure_items", () -> CreativeModeTab.builder()
            .title(Component.translatable("tab.exoadvadditions.items"))
            .icon(() -> new ItemStack(ITEM_ETERNA_CRYSTALIS.get()))
            .displayItems((parameters, output) -> {
                for (RegistryObject<Item> item : ITEMS.getEntries()) {
                    output.accept(item.get());
                }
            })
            .build());


    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS,
            ExoAdventureAdditions.MODID);

    public static final RegistryObject<MobEffect> EFFECT_WAKEFULNESS = MOB_EFFECTS.register("wakefulness",
            () -> new WakefulnessEffect(MobEffectCategory.BENEFICIAL, 0x7AF5B1) );
    public static final RegistryObject<MobEffect> EFFECT_CHEATED_DEATH = MOB_EFFECTS.register("cheated_death",
            () -> new CheatedDeathEffect(MobEffectCategory.HARMFUL, 0x42191F) );


    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
            ExoAdventureAdditions.MODID);
    public static final RegistryObject<SoundEvent> MUSIC_THE_DEPTHS = SOUND_EVENTS.register("music.the_depths", () -> SoundEvent.createVariableRangeEvent(
            new ResourceLocation(ExoAdventureAdditions.MODID, "music.the_depths")));


    public static Pair<ResourceKey<Block>, ResourceKey<Block>>[] NATURAL_ORE_PAIRS;

    public static void registerCommon() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        RECIPE_SERIALIZERS.register(modBus);
        //GLOBAL_LOOT_MODS.register(modBus);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        CREATIVE_TABS.register(modBus);
        MOB_EFFECTS.register(modBus);
        SOUND_EVENTS.register(modBus);
    }

    public static void setupOres() {
        IForgeRegistry<Block> forgeRegistry = ForgeRegistries.BLOCKS;
        NATURAL_ORE_PAIRS = new Pair[] {
                orePair(forgeRegistry, Blocks.COAL_ORE, BLOCK_NATURAL_COAL_ORE.get()),
                orePair(forgeRegistry, Blocks.DEEPSLATE_COAL_ORE, BLOCK_NATURAL_DEEPSLATE_COAL_ORE.get()),
                orePair(forgeRegistry, Blocks.IRON_ORE, BLOCK_NATURAL_IRON_ORE.get()),
                orePair(forgeRegistry, Blocks.DEEPSLATE_IRON_ORE, BLOCK_NATURAL_DEEPSLATE_IRON_ORE.get()),
                orePair(forgeRegistry, Blocks.COPPER_ORE, BLOCK_NATURAL_COPPER_ORE.get()),
                orePair(forgeRegistry, Blocks.DEEPSLATE_COPPER_ORE, BLOCK_NATURAL_DEEPSLATE_COPPER_ORE.get()),
                orePair(forgeRegistry, Blocks.GOLD_ORE, BLOCK_NATURAL_GOLD_ORE.get()),
                orePair(forgeRegistry, Blocks.DEEPSLATE_GOLD_ORE, BLOCK_NATURAL_DEEPSLATE_GOLD_ORE.get()),
                orePair(forgeRegistry, Blocks.LAPIS_ORE, BLOCK_NATURAL_LAPIS_ORE.get()),
                orePair(forgeRegistry, Blocks.DEEPSLATE_LAPIS_ORE, BLOCK_NATURAL_DEEPSLATE_LAPIS_ORE.get()),
                orePair(forgeRegistry, Blocks.REDSTONE_ORE, BLOCK_NATURAL_REDSTONE_ORE.get()),
                orePair(forgeRegistry, Blocks.DEEPSLATE_REDSTONE_ORE, BLOCK_NATURAL_DEEPSLATE_REDSTONE_ORE.get()),
                orePair(forgeRegistry, Blocks.DIAMOND_ORE, BLOCK_NATURAL_DIAMOND_ORE.get()),
                orePair(forgeRegistry, Blocks.DEEPSLATE_DIAMOND_ORE, BLOCK_NATURAL_DEEPSLATE_DIAMOND_ORE.get()),
                orePair(forgeRegistry, Blocks.EMERALD_ORE, BLOCK_NATURAL_EMERALD_ORE.get()),
                orePair(forgeRegistry, Blocks.DEEPSLATE_EMERALD_ORE, BLOCK_NATURAL_DEEPSLATE_EMERALD_ORE.get()),
        };
    }

    private static ObjectObjectImmutablePair<ResourceKey<Block>, ResourceKey<Block>> orePair(IForgeRegistry<Block> registry, Block left, Block right) {
        return new ObjectObjectImmutablePair(registry.getResourceKey(left).get(), registry.getResourceKey(right).get());
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Level> event) {
        event.addCapability(new ResourceLocation(ExoAdventureAdditions.MODID, "daytime_dilation"), new DaytimeDilationProvider());
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        AdventureStart.register(event.getDispatcher());
        LowerDifficulty.register(event.getDispatcher());
    }
}
