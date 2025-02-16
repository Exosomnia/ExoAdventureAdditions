package com.exosomnia.exoadvadditions;

import com.exosomnia.exoadvadditions.blocks.TeleporterPlateBlock;
import com.exosomnia.exoadvadditions.capabilities.daytimedilation.DaytimeDilationProvider;
import com.exosomnia.exoadvadditions.capabilities.daytimedilation.IDaytimeDilationStorage;
import com.exosomnia.exoadvadditions.commands.AdventureStart;
import com.exosomnia.exoadvadditions.commands.LowerDifficulty;
import com.exosomnia.exoadvadditions.effects.CheatedDeathEffect;
import com.exosomnia.exoadvadditions.effects.GroundedEffect;
import com.exosomnia.exoadvadditions.effects.WakefulnessEffect;
import com.exosomnia.exoadvadditions.items.*;
import com.exosomnia.exoadvadditions.loot.ChestsLootModifier;
import com.exosomnia.exoadvadditions.loot.DepthsLootModifier;
import com.exosomnia.exoadvadditions.loot.MacGuffinLootModifier;
import com.exosomnia.exoadvadditions.networking.PacketHandler;
import com.exosomnia.exoadvadditions.networking.packets.SimpleMusicPacket;
import com.exosomnia.exoadvadditions.recipes.AdventureMapRecipe;
import com.exosomnia.exoadvadditions.recipes.MysteriousPackageRecipe;
import com.exosomnia.exoadvadditions.recipes.tome.AdvancedShapedTomeRecipe;
import com.exosomnia.exoadvadditions.recipes.tome.ShapedTomeRecipe;
import com.exosomnia.exoadvadditions.recipes.tome.TomeRecipe.*;
import com.exosomnia.exoadvadditions.recipes.tome.TomeRecipeManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Registry {

    public static Class<?> FLYING_MOUNT_CLASS;
    public static Field FLYING_ALLOWED_FIELD;

    static {
        try {
            FLYING_MOUNT_CLASS = Class.forName("com.yahoo.chirpycricket.mythicmounts.entity.FlyingMountEntity");
            FLYING_ALLOWED_FIELD = FLYING_MOUNT_CLASS.getDeclaredField("flyingAllowed");
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            FLYING_MOUNT_CLASS = null;
            FLYING_ALLOWED_FIELD = null;
            LogUtils.getLogger().info("Uhhh guys?!");
        }
    }

    public final static TomeRecipeManager TOME_RECIPE_MANAGER = new TomeRecipeManager();

    public final static ResourceKey<Level> DEPTHS_DIMENSION = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("exoadvadditions:the_depths"));
    public final static ResourceKey<Structure> MYSTERIOUS_TOME_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation("exoadvadditions:the_depths_shrine"));
    public final static ResourceKey<Structure> MYSTERIOUS_TOME_UNLEASH_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation("mes:manuscript_shrine"));

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ExoAdventureAdditions.MODID);

    public static final RegistryObject<Codec<MacGuffinLootModifier>> LOOT_MOD_MACGUFFIN = GLOBAL_LOOT_MODS.register("loot_mod_macguffin", MacGuffinLootModifier.CODEC);
    public static final RegistryObject<Codec<DepthsLootModifier>> LOOT_DEPTHS = GLOBAL_LOOT_MODS.register("loot_mod_depths", DepthsLootModifier.CODEC);
    public static final RegistryObject<Codec<ChestsLootModifier>> LOOT_CHESTS = GLOBAL_LOOT_MODS.register("loot_mod_chests", ChestsLootModifier.CODEC);


    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,
            ExoAdventureAdditions.MODID);

    public static final RegistryObject<RecipeSerializer<AdventureMapRecipe>> RECIPE_ADVENTURE_MAP = RECIPE_SERIALIZERS.register("adventure_map_crafting",
            AdventureMapRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<MysteriousPackageRecipe>> RECIPE_MYSTERIOUS_PACKAGE = RECIPE_SERIALIZERS.register("mysterious_package_crafting",
            MysteriousPackageRecipe.Serializer::new);


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

    public static final RegistryObject<Block> BLOCK_NATURAL_TIN_ORE = BLOCKS.register("natural_tin_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_IRON_ORE.get())));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_TIN_ORE = BLOCKS.register("natural_deepslate_tin_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_DEEPSLATE_IRON_ORE.get())));
    public static final RegistryObject<Block> BLOCK_NATURAL_SILVER_ORE = BLOCKS.register("natural_silver_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_IRON_ORE.get())));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_SILVER_ORE = BLOCKS.register("natural_deepslate_silver_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_DEEPSLATE_IRON_ORE.get())));
    public static final RegistryObject<Block> BLOCK_NATURAL_NICKEL_ORE = BLOCKS.register("natural_nickel_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_IRON_ORE.get())));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_NICKEL_ORE = BLOCKS.register("natural_deepslate_nickel_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_DEEPSLATE_IRON_ORE.get())));
    public static final RegistryObject<Block> BLOCK_NATURAL_NITER_ORE = BLOCKS.register("natural_niter_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_IRON_ORE.get())));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_NITER_ORE = BLOCKS.register("natural_deepslate_niter_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_DEEPSLATE_IRON_ORE.get())));
    public static final RegistryObject<Block> BLOCK_NATURAL_ZINC_ORE = BLOCKS.register("natural_zinc_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_IRON_ORE.get())));
    public static final RegistryObject<Block> BLOCK_NATURAL_DEEPSLATE_ZINC_ORE = BLOCKS.register("natural_deepslate_zinc_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(BLOCK_NATURAL_DEEPSLATE_IRON_ORE.get())));

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
    public static final RegistryObject<Item> ITEM_TOME_OF_FLIGHT = ITEMS.register("tome_of_flight", () -> new TomeOfFlight(new Item.Properties().stacksTo(16), 1, false));
    public static final RegistryObject<Item> ITEM_ASCENDED_ITEM_TOME_OF_FLIGHT = ITEMS.register("ascended_tome_of_flight", () -> new TomeOfFlight(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), 2, true));
    public static final RegistryObject<Item> ITEM_ETERNA_CRYSTALIS = ITEMS.register("eterna_crystalis", () -> new SimpleFoiledItem(new Item.Properties().stacksTo(64).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ITEM_NETHERITE_INGOT_STACK = ITEMS.register("netherite_ingot_stack", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_CRUSHED_ANCIENT_DEBRIS = ITEMS.register("crushed_ancient_debris", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_ELDER_GUARDIAN_EYE = ITEMS.register("elder_guardian_eye", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_OLD_MANUSCRIPT = ITEMS.register("old_manuscript", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_INFERNAL_MANUSCRIPT = ITEMS.register("infernal_manuscript", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_ENDER_MANUSCRIPT = ITEMS.register("ender_manuscript", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_ANCIENT_MANUSCRIPT = ITEMS.register("ancient_manuscript", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_MAGICKED_FEATHER = ITEMS.register("magicked_feather", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_INFERNAL_FEATHER = ITEMS.register("infernal_feather", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_ENDER_FEATHER = ITEMS.register("ender_feather", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_ANCIENT_FEATHER = ITEMS.register("ancient_feather", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_CURIOUS_CARD = ITEMS.register("curious_card", () -> new CuriousCardItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_GILDED_FISH = ITEMS.register("gilded_fish", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_FIERY_INGOT = ITEMS.register("fiery_ingot", () -> new FieryIngotItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MAGICAL_RUNES = ITEMS.register("magical_runes", () -> new MagicalRunesItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MAGICAL_VOID_RUNES = ITEMS.register("magical_void_runes", () -> new MagicalRunesItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_SUSPICIOUS_MATTER = ITEMS.register("suspicious_matter", () -> new SuspiciousMatterItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MYSTERIOUS_TOME = ITEMS.register("mysterious_tome_dormant", MysteriousTomeDormantItem::new);
    public static final RegistryObject<Item> ITEM_MYSTERIOUS_TOME_ACTIVE = ITEMS.register("mysterious_tome_active", () -> new MysteriousTomeItem(false));
    public static final RegistryObject<Item> ITEM_MYSTERIOUS_TOME_UNLEASHED = ITEMS.register("mysterious_tome_unleashed", () -> new MysteriousTomeItem(true));
    public static final RegistryObject<Item> ITEM_CIRCUIT_BOARD = ITEMS.register("circuit_board", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_CHARGED_CIRCUIT_BOARD = ITEMS.register("charged_circuit_board", () -> new SimpleFoiledItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_UNFINISHED_ELYTRA = ITEMS.register("unfinished_elytra", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_MYSTERIOUS_BLUEPRINT_SHAPES = ITEMS.register("mysterious_blueprint_shapes", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MYSTERIOUS_BLUEPRINT_DIAGRAMS = ITEMS.register("mysterious_blueprint_diagrams", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MYSTERIOUS_PACKAGE = ITEMS.register("mysterious_package", () -> new MysteriousPackageItem(new Item.Properties()));
    public static final RegistryObject<Item> ITEM_GILDED_ANCIENT_TOOTH = ITEMS.register("gilded_ancient_tooth", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_OMINOUS_BAR = ITEMS.register("ominous_bar", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_VOID_ORB = ITEMS.register("void_orb", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_DEAD_KINGS_GEM = ITEMS.register("dead_kings_gem", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_BURNING_SCROLL = ITEMS.register("burning_scroll", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_SCROLL_OF_GROWTH = ITEMS.register("scroll_of_growth", () -> new GrowthScrollItem(0));
    public static final RegistryObject<Item> ITEM_INFERNAL_SCROLL_OF_GROWTH = ITEMS.register("infernal_scroll_of_growth", () -> new GrowthScrollItem(1));
    public static final RegistryObject<Item> ITEM_ENDER_SCROLL_OF_GROWTH = ITEMS.register("ender_scroll_of_growth", () -> new GrowthScrollItem(2));
    public static final RegistryObject<Item> ITEM_ANCIENT_SCROLL_OF_GROWTH = ITEMS.register("ancient_scroll_of_growth", () -> new GrowthScrollItem(3));

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
    public static final RegistryObject<Item> ITEM_MACGUFFIN_13 = ITEMS.register("macguffin_13", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_14 = ITEMS.register("macguffin_14", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_15 = ITEMS.register("macguffin_15", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_16 = ITEMS.register("macguffin_16", () -> new Item(new Item.Properties().stacksTo(16)));
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

    public static final RegistryObject<CreativeModeTab> TAB_ADVENTURE_MAPS = CREATIVE_TABS.register("adventure_maps", () -> CreativeModeTab.builder()
            .title(Component.translatable("tab.exoadvadditions.maps"))
            .icon(() -> new ItemStack(ITEM_UNLOCATED_MAP.get()))
            .build());

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS,
            ExoAdventureAdditions.MODID);

    public static final RegistryObject<MobEffect> EFFECT_WAKEFULNESS = MOB_EFFECTS.register("wakefulness",
            () -> new WakefulnessEffect(MobEffectCategory.BENEFICIAL, 0x7AF5B1) );
    public static final RegistryObject<MobEffect> EFFECT_CHEATED_DEATH = MOB_EFFECTS.register("cheated_death",
            () -> new CheatedDeathEffect(MobEffectCategory.NEUTRAL, 0x42191F) );
    public static final RegistryObject<MobEffect> EFFECT_GROUNDED = MOB_EFFECTS.register("grounded",
            () -> new GroundedEffect(MobEffectCategory.NEUTRAL, 0x3B4530) );
    public static final RegistryObject<MobEffect> EFFECT_FLIGHT_READY = MOB_EFFECTS.register("flight_ready",
            () -> new GroundedEffect(MobEffectCategory.NEUTRAL, 0xB2E0ED) );


    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
            ExoAdventureAdditions.MODID);
    public static final RegistryObject<SoundEvent> MUSIC_THE_DEPTHS = SOUND_EVENTS.register("music.the_depths", () -> SoundEvent.createVariableRangeEvent(
            new ResourceLocation(ExoAdventureAdditions.MODID, "music.the_depths")));
    public static final RegistryObject<SoundEvent> SOUND_TOME_CRAFT_SOUND_P = SOUND_EVENTS.register("tome_craft_sound_p", () -> SoundEvent.createVariableRangeEvent(
            new ResourceLocation(ExoAdventureAdditions.MODID, "tome_craft_sound_p")));
    public static final RegistryObject<SoundEvent> SOUND_TOME_CRAFT_SOUND_B = SOUND_EVENTS.register("tome_craft_sound_b", () -> SoundEvent.createVariableRangeEvent(
            new ResourceLocation(ExoAdventureAdditions.MODID, "tome_craft_sound_b")));


    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ExoAdventureAdditions.MODID);
    public static final RegistryObject<Attribute> ATTRIBUTE_SKILL_EXP_BONUS = ATTRIBUTES.register("skills_xp_bonus",
            () -> new RangedAttribute("attribute.exoadvadditions.skills_xp_bonus",
                    1.0,
                    0.0,
                    127.0));


    public static Pair<ResourceKey<Block>, ResourceKey<Block>>[] NATURAL_ORE_PAIRS;
    public static Rarity MYSTERIOUS_RARITY;

    //PULLING REGISTRY ITEMS FROM OTHER MODS
    public static EntityType<?> ENTITY_COLELYTRA;
    public static EntityType<?> ENTITY_DRAGON;
    public static EntityType<?> ENTITY_FIREBIRD;
    public static EntityType<?> ENTITY_GRIFFON;
    public static EntityType<?> ENTITY_MOTH;
    public static EntityType<?> ENTITY_NETHER_BAT;
    public static EntityType<?> ENTITY_NUDIBRANCH;

    public static void registerCommon() {
        PacketHandler.register();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(Registry::attributeModifyEvent);
        modBus.addListener(Registry::registerCapabilities);

        RECIPE_SERIALIZERS.register(modBus);
        GLOBAL_LOOT_MODS.register(modBus);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        CREATIVE_TABS.register(modBus);
        MOB_EFFECTS.register(modBus);
        SOUND_EVENTS.register(modBus);
        ATTRIBUTES.register(modBus);
    }

    public static void setupOres() {
        IForgeRegistry<Block> forgeRegistry = ForgeRegistries.BLOCKS;

        Block TIN_ORE = forgeRegistry.getValue(new ResourceLocation("thermal", "tin_ore"));
        Block DEEPSLATE_TIN_ORE = forgeRegistry.getValue(new ResourceLocation("thermal", "deepslate_tin_ore"));
        Block SILVER_ORE = forgeRegistry.getValue(new ResourceLocation("thermal", "silver_ore"));
        Block DEEPSLATE_SILVER_ORE = forgeRegistry.getValue(new ResourceLocation("thermal", "deepslate_silver_ore"));
        Block NICKEL_ORE =  forgeRegistry.getValue(new ResourceLocation("thermal", "nickel_ore"));
        Block DEEPSLATE_NICKEL_ORE = forgeRegistry.getValue(new ResourceLocation("thermal", "deepslate_nickel_ore"));
        Block NITER_ORE = forgeRegistry.getValue(new ResourceLocation("thermal", "niter_ore"));
        Block DEEPSLATE_NITER_ORE = forgeRegistry.getValue(new ResourceLocation("thermal", "deepslate_niter_ore"));
        Block ZINC_ORE = forgeRegistry.getValue(new ResourceLocation("create", "zinc_ore"));
        Block DEEPSLATE_ZINC_ORE = forgeRegistry.getValue(new ResourceLocation("create", "deepslate_zinc_ore"));

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
                orePair(forgeRegistry, Blocks.ANCIENT_DEBRIS, BLOCK_NATURAL_ANCIENT_DEBRIS.get()),

                orePair(forgeRegistry, TIN_ORE, BLOCK_NATURAL_TIN_ORE.get()),
                orePair(forgeRegistry, DEEPSLATE_TIN_ORE, BLOCK_NATURAL_DEEPSLATE_TIN_ORE.get()),
                orePair(forgeRegistry, SILVER_ORE, BLOCK_NATURAL_SILVER_ORE.get()),
                orePair(forgeRegistry, DEEPSLATE_SILVER_ORE, BLOCK_NATURAL_DEEPSLATE_SILVER_ORE.get()),
                orePair(forgeRegistry, NICKEL_ORE, BLOCK_NATURAL_NICKEL_ORE.get()),
                orePair(forgeRegistry, DEEPSLATE_NICKEL_ORE, BLOCK_NATURAL_DEEPSLATE_NICKEL_ORE.get()),
                orePair(forgeRegistry, NITER_ORE, BLOCK_NATURAL_NITER_ORE.get()),
                orePair(forgeRegistry, DEEPSLATE_NITER_ORE, BLOCK_NATURAL_DEEPSLATE_NITER_ORE.get()),

                orePair(forgeRegistry, ZINC_ORE, BLOCK_NATURAL_ZINC_ORE.get()),
                orePair(forgeRegistry, DEEPSLATE_ZINC_ORE, BLOCK_NATURAL_DEEPSLATE_ZINC_ORE.get())
        };
    }

    private static ObjectObjectImmutablePair<ResourceKey<Block>, ResourceKey<Block>> orePair(IForgeRegistry<Block> registry, Block left, Block right) {
        return new ObjectObjectImmutablePair(registry.getResourceKey(left).get(), registry.getResourceKey(right).get());
    }

    public static void attributeModifyEvent(final EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ATTRIBUTE_SKILL_EXP_BONUS.get());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IDaytimeDilationStorage.class);
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

    @SubscribeEvent
    public static void buildCreative(BuildCreativeModeTabContentsEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null || mc.level == null) { return; }

        ResourceLocation tabLocation = event.getTabKey().location();
        if (tabLocation.equals(new ResourceLocation("exoadvadditions", "adventure_maps"))) {
            RegistryAccess registryAccess = mc.level.registryAccess();
            RecipeManager recipes = Minecraft.getInstance().getConnection().getRecipeManager();
            recipes.getRecipes().stream().filter(recipe -> recipe instanceof AdventureMapRecipe).forEach(recipe -> {
                event.accept(recipe.getResultItem(registryAccess));
            });
        }
        else if (tabLocation.getNamespace().equals("tacz")) {
            event.getEntries().iterator().forEachRemaining(entry -> {
                entry.setValue(CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
            });
        }
    }

    public static void registerTomeRecipes() {
        ENTITY_COLELYTRA = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("mythicmounts", "colelytra"));
        ENTITY_DRAGON = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("mythicmounts", "dragon"));
        ENTITY_FIREBIRD = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("mythicmounts", "firebird"));
        ENTITY_GRIFFON = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("mythicmounts", "griffon"));
        ENTITY_MOTH = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("mythicmounts", "moth"));
        ENTITY_NETHER_BAT = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("mythicmounts", "netherbat"));
        ENTITY_NUDIBRANCH = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("mythicmounts", "nudibranch"));

        final Item itemCrypticEye = ForgeRegistries.ITEMS.getValue(new ResourceLocation("endrem", "cryptic_eye"));
        final Item itemExperienceBlock = ForgeRegistries.ITEMS.getValue(new ResourceLocation("create", "experience_block"));
        final TagKey<Item> tagEndRemEyes = TagKey.create(Registries.ITEM, new ResourceLocation("exoadventure", "end_rem_eyes"));

        TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"AAA", "AQA", "AAA"})
                .midLayer(new String[]{"AQA", "Q Q", "AQA"})
                .lowLayer(new String[]{"AAA", "AQA", "AAA"})
                .blockMappings(ImmutableMap.of('A', BlockMapping.of(Blocks.AMETHYST_BLOCK), 'Q', BlockMapping.of(Blocks.QUARTZ_BLOCK)))
                .itemMappings(ImmutableList.of(ItemMapping.of(Ingredient.of(Blocks.LAPIS_BLOCK.asItem()), 2)))
                .result(new ItemStack(ITEM_MAGICAL_RUNES.get()), 1)
                .build());

        TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"AQA", "QGQ", "AQA"})
                .midLayer(new String[]{"QGQ", "G G", "QGQ"})
                .lowLayer(new String[]{"AQA", "QGQ", "AQA"})
                .blockMappings(ImmutableMap.of('A', BlockMapping.of(Blocks.AMETHYST_BLOCK), 'Q', BlockMapping.of(Blocks.QUARTZ_BLOCK), 'G', BlockMapping.of(Blocks.GLOWSTONE)))
                .itemMappings(ImmutableList.of(ItemMapping.of(Ingredient.of(tagEndRemEyes), 1), ItemMapping.of(Ingredient.of(tagEndRemEyes), 1),
                        ItemMapping.of(Ingredient.of(tagEndRemEyes), 1), ItemMapping.of(Ingredient.of(itemExperienceBlock), 1)))
                .result(new ItemStack(itemCrypticEye), 1)
                .build());

        TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"AIA", "III", "AIA"})
                .midLayer(new String[]{"BBB", "B B", "BBB"})
                .lowLayer(new String[]{"AIA", "III", "AIA"})
                .blockMappings(ImmutableMap.of('A', BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'I', BlockMapping.of(TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "storage_blocks/iron"))),
                        'B', BlockMapping.of(TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(ItemMapping.of(Ingredient.of(ITEM_INFERNAL_MANUSCRIPT.get()), 1),
                        ItemMapping.of(Ingredient.of(ITEM_SCROLL_OF_GROWTH.get()), 1)))
                .result(new ItemStack(ITEM_INFERNAL_SCROLL_OF_GROWTH.get()), 1)
                .build());

        TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"AIIIA", "IAAAI", "IAAAI", "IAAAI", "AIIIA"})
                .withLayer(new String[]{"IBBBI", "B   B", "B   B", "B   B", "IBBBI"})
                .withLayer(new String[]{"IBBBI", "B   B", "B   B", "B   B", "IBBBI"})
                .withLayer(new String[]{"IBBBI", "B   B", "B   B", "B   B", "IBBBI"})
                .withLayer(new String[]{"AIIIA", "IAAAI", "IAAAI", "IAAAI", "AIIIA"})
                .blockMappings(ImmutableMap.of('A', BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'I', BlockMapping.of(TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "storage_blocks/gold"))),
                        'B', BlockMapping.of(TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(ItemMapping.of(Ingredient.of(ITEM_ENDER_MANUSCRIPT.get()), 1),
                        ItemMapping.of(Ingredient.of(ITEM_SCROLL_OF_GROWTH.get()), 1)))
                .result(new ItemStack(ITEM_ENDER_SCROLL_OF_GROWTH.get()))
                .build());

        TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"AIA", "III", "AIA"})
                .midLayer(new String[]{"BBB", "B B", "BBB"})
                .lowLayer(new String[]{"AIA", "III", "AIA"})
                .blockMappings(ImmutableMap.of('A', BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'I', BlockMapping.of(TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "storage_blocks/diamond"))),
                        'B', BlockMapping.of(TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(ItemMapping.of(Ingredient.of(ITEM_ANCIENT_MANUSCRIPT.get()), 1),
                        ItemMapping.of(Ingredient.of(ITEM_SCROLL_OF_GROWTH.get()), 1)))
                .result(new ItemStack(ITEM_ANCIENT_SCROLL_OF_GROWTH.get()))
                .build());

        TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"OOO", "OOO", "OOO"})
                .midLayer(new String[]{"WWW", "W W", "WWW"})
                .lowLayer(new String[]{"WWW", "WWW", "WWW"})
                .blockMappings(ImmutableMap.of('O', BlockMapping.of(Blocks.ORANGE_WOOL), 'W', BlockMapping.of(Blocks.WHITE_WOOL)))
                .itemMappings(ImmutableList.of(ItemMapping.of(Ingredient.of(Items.CARROT), 2), ItemMapping.of(Ingredient.of(Items.DIAMOND_BLOCK), 1)))
                .result(new ItemStack(Items.GOLDEN_CARROT, 32))
                .execute((player, position) -> {
                    ServerLevel level = player.serverLevel();
                    Rabbit p = new Rabbit(EntityType.RABBIT, level);
                    p.setPos(position.getCenter());
                    p.setVariant(Rabbit.Variant.EVIL);
                    p.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
                    p.setHealth(20.0F);
                    level.addFreshEntity(p);
                    for (ServerPlayer serverPlayers : level.getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(16.0))) {
                        PacketHandler.sendToPlayer(new SimpleMusicPacket(Registry.SOUND_TOME_CRAFT_SOUND_P.getId()), serverPlayers);
                    }
                })
                .build());

        TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"BBB", "BBB", "BBB"})
                .midLayer(new String[]{"QQQ", "Q Q", "QQQ"})
                .lowLayer(new String[]{"PPP", "PPP", "PPP"})
                .blockMappings(ImmutableMap.of('B', BlockMapping.of(Blocks.BLUE_WOOL), 'P', BlockMapping.of(Blocks.PINK_WOOL), 'Q', BlockMapping.of(Blocks.QUARTZ_BLOCK)))
                .itemMappings(ImmutableList.of(ItemMapping.of(Ingredient.of(Items.BONE), 2), ItemMapping.of(Ingredient.of(Items.DIAMOND_BLOCK), 1)))
                .execute((player, position) -> {
                    ServerLevel level = player.serverLevel();
                    Wolf f = new Wolf(EntityType.WOLF, level);
                    f.setPos(position.getCenter());
                    f.setTame(true);
                    f.setOwnerUUID(player.getUUID());
                    f.setCollarColor(DyeColor.LIGHT_BLUE);
                    f.getAttribute(Attributes.MAX_HEALTH).setBaseValue(25.0);
                    f.setHealth(25.0F);
                    Wolf m = new Wolf(EntityType.WOLF, level);
                    m.setPos(position.getCenter());
                    m.setTame(true);
                    m.setOwnerUUID(player.getUUID());
                    m.setCollarColor(DyeColor.PINK);
                    m.getAttribute(Attributes.MAX_HEALTH).setBaseValue(25.0);
                    m.setHealth(25.0F);
                    level.addFreshEntity(f);
                    level.addFreshEntity(m);
                    for (ServerPlayer serverPlayers : level.getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(16.0))) {
                        PacketHandler.sendToPlayer(new SimpleMusicPacket(Registry.SOUND_TOME_CRAFT_SOUND_B.getId()), serverPlayers);
                    }
                })
                .build());
    }
}
