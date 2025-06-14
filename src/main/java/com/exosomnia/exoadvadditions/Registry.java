package com.exosomnia.exoadvadditions;

import com.exosomnia.exoadvadditions.blocks.TeleporterPlateBlock;
import com.exosomnia.exoadvadditions.capabilities.daytimedilation.DaytimeDilationProvider;
import com.exosomnia.exoadvadditions.capabilities.daytimedilation.IDaytimeDilationStorage;
import com.exosomnia.exoadvadditions.commands.AdventureStart;
import com.exosomnia.exoadvadditions.commands.LowerDifficulty;
import com.exosomnia.exoadvadditions.effects.CheatedDeathEffect;
import com.exosomnia.exoadvadditions.effects.FatiguedEffect;
import com.exosomnia.exoadvadditions.effects.GroundedEffect;
import com.exosomnia.exoadvadditions.effects.WakefulnessEffect;
import com.exosomnia.exoadvadditions.items.*;
import com.exosomnia.exoadvadditions.loot.ChestsLootModifier;
import com.exosomnia.exoadvadditions.loot.DepthsLootModifier;
import com.exosomnia.exoadvadditions.loot.conditions.DifficultyStageCondition;
import com.exosomnia.exoadvadditions.networking.PacketHandler;
import com.exosomnia.exoadvadditions.recipes.*;
import com.exosomnia.exoadvadditions.recipes.tome.TomeRecipeManager;
import com.exosomnia.exoarmory.ExoArmory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
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
import vazkii.botania.api.BotaniaRegistries;
import vazkii.botania.api.brew.Brew;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Registry {

    public static Class<?> FLYING_MOUNT_CLASS;
    public static Field FLYING_ALLOWED_FIELD;

    public final static ImmutableList<String> VALID_PANDORA;
    public static ImmutableMap<String, AttributeTemplateItem> ATTRIBUTE_TEMPLATES;

    static {
        try {
            FLYING_MOUNT_CLASS = Class.forName("com.yahoo.chirpycricket.mythicmounts.entity.FlyingMountEntity");
            FLYING_ALLOWED_FIELD = FLYING_MOUNT_CLASS.getDeclaredField("flyingAllowed");
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            FLYING_MOUNT_CLASS = null;
            FLYING_ALLOWED_FIELD = null;
            LogUtils.getLogger().info("Uhhh guys?!");
        }

        VALID_PANDORA = ImmutableList.<String>builder().add("mobs", "mob_towers", "megaton", "block_grave", "block_shower", "transform", "replace", "dryness", "tnt_splosion", "dirty_trick", "water_pool", "lava_pool", "height_noise", "mad_geometry", "madder_geometry", "lava_cage", "water_cage", "classic", "lightning", "sand_for_dessert", "in_the_end", "hell_on_earth_nether_wastes", "hell_on_earth_soul_sand_valley", "hell_on_earth_basalt_deltas", "hell_on_earth_warped_forest", "hell_on_earth_crimson_forest", "lifeless", "trapped_tribe", "buffed_down", "frozen_in_place", "flying_forest", "crush", "bomberman", "pitch_black", "void", "are_these_mine", "time_lord", "explo_creatures", "explo_mobs", "aquarium", "aquaridoom", "world_snake", "double_snake", "tunnel_bore", "creeper_soul", "bomb_pack", "make_thin", "cover", "target", "armored_army", "army", "boss", "ice_age", "tele_random", "crazy_port", "thing_go_boom", "danger_call", "animals", "animal_towers", "tamer", "items", "epic_armor", "epic_tool", "epic_thing", "enchanted_book", "resources", "equipment_set", "item_rain", "dead_creatures", "dead_mobs", "experience", "sudden_forest", "sudden_jungle", "odd_jungle", "buffed_up", "snow_age", "normal_land", "happy_fun_times", "shroomify", "rainbows", "all_rainbow", "farm", "heavenly", "halloween", "christmas", "new_year", "block_tower", "terrarium", "animal_farm").build();
    }

    public final static TomeRecipeManager TOME_RECIPE_MANAGER = new TomeRecipeManager();

    public final static ResourceKey<Level> DEPTHS_DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.bySeparator("exoadvadditions:the_depths", ':'));
    public final static ResourceKey<Level> BEGINNING_DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.bySeparator("exoadvadditions:the_beginning", ':'));
    public final static ResourceKey<Structure> MYSTERIOUS_TOME_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.bySeparator("exoadvadditions:the_depths_shrine", ':'));
    public final static ResourceKey<Structure> MYSTERIOUS_TOME_UNLEASH_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.bySeparator("mes:manuscript_shrine", ':'));

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ExoAdventureAdditions.MODID);

    public static final RegistryObject<Codec<DepthsLootModifier>> LOOT_DEPTHS = GLOBAL_LOOT_MODS.register("loot_mod_depths", DepthsLootModifier.CODEC);
    public static final RegistryObject<Codec<ChestsLootModifier>> LOOT_CHESTS = GLOBAL_LOOT_MODS.register("loot_mod_chests", ChestsLootModifier.CODEC);


    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,
            ExoAdventureAdditions.MODID);

    public static final RegistryObject<RecipeSerializer<AdventureMapRecipe>> RECIPE_ADVENTURE_MAP = RECIPE_SERIALIZERS.register("adventure_map_crafting",
            AdventureMapRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<MysteriousPackageRecipe>> RECIPE_MYSTERIOUS_PACKAGE = RECIPE_SERIALIZERS.register("mysterious_package_crafting",
            MysteriousPackageRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<AttuneFeatherRecipe>> RECIPE_ATTUNE_FEATHER = RECIPE_SERIALIZERS.register("attune_feather_crafting",
            AttuneFeatherRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<ShapedNBTOptionalRecipe>> RECIPE_SHAPED_NBT_OPTIONAL = RECIPE_SERIALIZERS.register("shaped_nbt_optional_crafting",
            ShapedNBTOptionalRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<AttributeTemplateSmithingRecipe>> RECIPE_ATTRIBUTE_TEMPLATE_SMITHING = RECIPE_SERIALIZERS.register("attribute_template_smithing",
            AttributeTemplateSmithingRecipe.Serializer::new);


    public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ExoAdventureAdditions.MODID);
    public static final RegistryObject<LootItemConditionType> DIFFICULTY_STAGE_CONDITION = LOOT_ITEM_CONDITIONS.register("difficulty_stage_condition", () -> new LootItemConditionType(new DifficultyStageCondition.Serializer()));


    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExoAdventureAdditions.MODID);
    public static final RegistryObject<Block> BLOCK_ENCHANTED_DEEPSLATE = BLOCKS.register("enchanted_deepslate", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COBBLED_DEEPSLATE)));
    public static final RegistryObject<Block> BLOCK_ERROR = BLOCKS.register("error", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)));
    public static final RegistryObject<Block> BLOCK_BLANK_OUTLIINE = BLOCKS.register("blank_block", () -> new GlassBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)));
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
    public static final RegistryObject<Item> ITEM_TOME_OF_FLIGHT = ITEMS.register("tome_of_flight", () -> new TomeOfFlightItem(new Item.Properties().stacksTo(16), 1, false));
    public static final RegistryObject<Item> ITEM_ASCENDED_TOME_OF_FLIGHT = ITEMS.register("ascended_tome_of_flight", () -> new TomeOfFlightItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), 2, true));
    public static final RegistryObject<Item> ITEM_TOME_OF_LUCK = ITEMS.register("tome_of_luck", () -> new TomeOfLuckItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON), 1, false));
    public static final RegistryObject<Item> ITEM_TOME_OF_STAMINA = ITEMS.register("tome_of_stamina", () -> new TomeOfStaminaItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON), 1, false));
    public static final RegistryObject<Item> ITEM_TOME_OF_BALLISTICS = ITEMS.register("tome_of_ballistics", () -> new TomeOfBallisticsItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON), 1, false));
    public static final RegistryObject<Item> ITEM_ETERNA_CRYSTALIS = ITEMS.register("eterna_crystalis", () -> new SimpleFoiledItem(new Item.Properties().stacksTo(64).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ITEM_FLAWLESS_ONYX = ITEMS.register("flawless_onyx", () -> new SimpleFoiledItem(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ITEM_NETHERITE_INGOT_STACK = ITEMS.register("netherite_ingot_stack", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_CRUSHED_ANCIENT_DEBRIS = ITEMS.register("crushed_ancient_debris", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_ELDER_GUARDIAN_EYE = ITEMS.register("elder_guardian_eye", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_OLD_MANUSCRIPT = ITEMS.register("old_manuscript", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_INFERNAL_MANUSCRIPT = ITEMS.register("infernal_manuscript", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_ENDER_MANUSCRIPT = ITEMS.register("ender_manuscript", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_ANCIENT_MANUSCRIPT = ITEMS.register("ancient_manuscript", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_MAGICKED_FEATHER = ITEMS.register("magicked_feather", () -> new MagickFeatherItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_INFERNAL_FEATHER = ITEMS.register("infernal_feather", () -> new MagickFeatherItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_ENDER_FEATHER = ITEMS.register("ender_feather", () -> new MagickFeatherItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_CURIOUS_CARD = ITEMS.register("curious_card", () -> new CuriousCardItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_ANCIENT_FEATHER = ITEMS.register("ancient_feather", () -> new MagickFeatherItem(new Item.Properties().stacksTo(64)));
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
    public static final RegistryObject<Item> ITEM_DIAMOND_CARROT = ITEMS.register("diamond_carrot", () -> new Item(new Item.Properties().stacksTo(64).food((new FoodProperties.Builder()).nutrition(8).saturationMod(1.2F).alwaysEat().effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 0), 1.0F).build())));
    public static final RegistryObject<Item> ITEM_DEAD_KINGS_GEM = ITEMS.register("dead_kings_gem", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_BURNING_SCROLL = ITEMS.register("burning_scroll", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_MESSAGE_IN_BOTTLE = ITEMS.register("message_in_a_bottle", BottledMessageItem::new);
    public static final RegistryObject<Item> ITEM_SCROLL_OF_WEATHER = ITEMS.register("scroll_of_weather", WeatherScrollItem::new);
    public static final RegistryObject<Item> ITEM_SCROLL_OF_GROWTH = ITEMS.register("scroll_of_growth", () -> new GrowthScrollItem(0));
    public static final RegistryObject<Item> ITEM_INFERNAL_SCROLL_OF_GROWTH = ITEMS.register("infernal_scroll_of_growth", () -> new GrowthScrollItem(1));
    public static final RegistryObject<Item> ITEM_ENDER_SCROLL_OF_GROWTH = ITEMS.register("ender_scroll_of_growth", () -> new GrowthScrollItem(2));
    public static final RegistryObject<Item> ITEM_ANCIENT_SCROLL_OF_GROWTH = ITEMS.register("ancient_scroll_of_growth", () -> new GrowthScrollItem(3));
    public static final RegistryObject<Item> ITEM_ESSENCE_OF_KNOWLEDGE = ITEMS.register("essence_of_knowledge", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ITEM_SCROLL_OF_TENACITY = ITEMS.register("scroll_of_tenacity", TenacityScrollItem::new);
    public static final RegistryObject<Item> ITEM_SCROLL_OF_SKILL_XP_COMBAT = ITEMS.register("scroll_of_skill_xp_combat", () -> new SkillXPScrollItem(SkillXPScrollItem.Skill.COMBAT));
    public static final RegistryObject<Item> ITEM_SCROLL_OF_SKILL_XP_MINING = ITEMS.register("scroll_of_skill_xp_mining", () -> new SkillXPScrollItem(SkillXPScrollItem.Skill.MINING));
    public static final RegistryObject<Item> ITEM_SCROLL_OF_SKILL_XP_FISHING = ITEMS.register("scroll_of_skill_xp_fishing", () -> new SkillXPScrollItem(SkillXPScrollItem.Skill.FISHING));
    public static final RegistryObject<Item> ITEM_SCROLL_OF_SKILL_XP_HUSBANDRY = ITEMS.register("scroll_of_skill_xp_husbandry", () -> new SkillXPScrollItem(SkillXPScrollItem.Skill.HUSBANDRY));
    public static final RegistryObject<Item> ITEM_SCROLL_OF_SKILL_XP_OCCULT = ITEMS.register("scroll_of_skill_xp_occult", () -> new SkillXPScrollItem(SkillXPScrollItem.Skill.OCCULT));
    public static final RegistryObject<Item> ITEM_SCROLL_OF_SKILL_XP_EXPLORATION = ITEMS.register("scroll_of_skill_xp_exploration", () -> new SkillXPScrollItem(SkillXPScrollItem.Skill.EXPLORATION));
    public static final RegistryObject<Item> ITEM_SCROLL_OF_SKILL_XP_RANDOM = ITEMS.register("scroll_of_skill_xp_random", SkillXPScrollRandomItem::new);

    public static final RegistryObject<Item> ITEM_ETHERIUM_UPGRADE_TEMPLATE = ITEMS.register("etherium_upgrade_template", EtheriumSmithingTemplateItem::new);
    public static final RegistryObject<AttributeTemplateItem> ITEM_MAGE_TEMPLATE = ITEMS.register("mage_template", () -> new AttributeTemplateItem("mage", AttributeTemplateItem::isEtherium, "item.exoadvadditions.attribute_template.help.alt.1"));
    public static final RegistryObject<AttributeTemplateItem> ITEM_FIGHTER_TEMPLATE = ITEMS.register("fighter_template", () -> new AttributeTemplateItem("fighter", AttributeTemplateItem::isEtherium, "item.exoadvadditions.attribute_template.help.alt.1"));
    public static final RegistryObject<AttributeTemplateItem> ITEM_RANGER_TEMPLATE = ITEMS.register("ranger_template", () -> new AttributeTemplateItem("ranger", AttributeTemplateItem::isEtherium, "item.exoadvadditions.attribute_template.help.alt.1"));
    public static final RegistryObject<AttributeTemplateItem> ITEM_ARMOR_REINFORCEMENT_TEMPLATE = ITEMS.register("armor_reinforcement_template", () -> new AttributeTemplateItem("reinforcement", AttributeTemplateItem::isReinforceable, "item.exoadvadditions.attribute_template.help.alt.2"));
    public static final RegistryObject<Item> ITEM_BLANK_SMITHING_TEMPLATE = ITEMS.register("blank_smithing_template", () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> ITEM_MACGUFFIN_1 = ITEMS.register("macguffin_1", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_1_HALF_1 = ITEMS.register("macguffin_1_half_1", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_1_HALF_2 = ITEMS.register("macguffin_1_half_2", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_2 = ITEMS.register("macguffin_2", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_2_HALF_1 = ITEMS.register("macguffin_2_half_1", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_2_HALF_2 = ITEMS.register("macguffin_2_half_2", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_3 = ITEMS.register("macguffin_3", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_3_HALF_1 = ITEMS.register("macguffin_3_half_1", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_3_HALF_2 = ITEMS.register("macguffin_3_half_2", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_4 = ITEMS.register("macguffin_4", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_4_HALF_1 = ITEMS.register("macguffin_4_half_1", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_4_HALF_2 = ITEMS.register("macguffin_4_half_2", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_5 = ITEMS.register("macguffin_5", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_5_HALF_1 = ITEMS.register("macguffin_5_half_1", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_5_HALF_2 = ITEMS.register("macguffin_5_half_2", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_6 = ITEMS.register("macguffin_6", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_6_HALF_1 = ITEMS.register("macguffin_6_half_1", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_6_HALF_2 = ITEMS.register("macguffin_6_half_2", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_7 = ITEMS.register("macguffin_7", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_7_HALF_1 = ITEMS.register("macguffin_7_half_1", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_7_HALF_2 = ITEMS.register("macguffin_7_half_2", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_8 = ITEMS.register("macguffin_8", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_8_HALF_1 = ITEMS.register("macguffin_8_half_1", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_8_HALF_2 = ITEMS.register("macguffin_8_half_2", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_9 = ITEMS.register("macguffin_9", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_10 = ITEMS.register("macguffin_10", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_11 = ITEMS.register("macguffin_11", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_12 = ITEMS.register("macguffin_12", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_13 = ITEMS.register("macguffin_13", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_14 = ITEMS.register("macguffin_14", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_15_UNFINISHED_1 = ITEMS.register("macguffin_15_unfinished_1", () -> new UnfinishedMacguffinItem(1));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_15_UNFINISHED_2 = ITEMS.register("macguffin_15_unfinished_2", () -> new UnfinishedMacguffinItem(2));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_15_UNFINISHED_3 = ITEMS.register("macguffin_15_unfinished_3", () -> new UnfinishedMacguffinItem(3));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_15_UNFINISHED_4 = ITEMS.register("macguffin_15_unfinished_4", () -> new UnfinishedMacguffinItem(4));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_15 = ITEMS.register("macguffin_15", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_16 = ITEMS.register("macguffin_16", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> ITEM_YOU_DID_IT_STAR_1 = ITEMS.register("you_did_it_star_1", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ITEM_YOU_DID_IT_STAR_2 = ITEMS.register("you_did_it_star_2", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ITEM_MACGUFFIN_ULTIMATE = ITEMS.register("macguffin_ultimate", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<BlockItem> ITEM_TELEPORTER_PLATE_BLOCK = ITEMS.register("teleporter_plate", () -> new BlockItem(BLOCK_TELEPORTER_PLATE.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> ITEM_ENCHANTED_DEEPSLATE_BLOCK = ITEMS.register("enchanted_deepslate", () -> new BlockItem(BLOCK_ENCHANTED_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> ITEM_ERROR_BLOCK = ITEMS.register("error", () -> new BlockItem(BLOCK_ERROR.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> ITEM_BLANK_OUTLINE_BLOCK = ITEMS.register("blank_block", () -> new BlockItem(BLOCK_BLANK_OUTLIINE.get(), new Item.Properties()));


    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExoAdventureAdditions.MODID);
    public static final RegistryObject<CreativeModeTab> TAB_ADVENTURE_ITEMS = CREATIVE_TABS.register("adventure_items", () -> CreativeModeTab.builder()
            .title(Component.translatable("tab.exoadvadditions.items"))
            .icon(() -> new ItemStack(ITEM_ETERNA_CRYSTALIS.get()))
            .displayItems((parameters, output) -> {
                for (RegistryObject<Item> item : ITEMS.getEntries()) {
                    if (item.get() != ITEM_ERROR_BLOCK.get().asItem()) output.accept(item.get());
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
            () -> new WakefulnessEffect(MobEffectCategory.BENEFICIAL, 0x7AF5B1));
    public static final RegistryObject<MobEffect> EFFECT_CHEATED_DEATH = MOB_EFFECTS.register("cheated_death",
            () -> new CheatedDeathEffect(MobEffectCategory.NEUTRAL, 0x42191F));
    public static final RegistryObject<MobEffect> EFFECT_GROUNDED = MOB_EFFECTS.register("grounded",
            () -> new GroundedEffect(MobEffectCategory.NEUTRAL, 0x3B4530));
    public static final RegistryObject<MobEffect> EFFECT_FLIGHT_READY = MOB_EFFECTS.register("flight_ready",
            () -> new GroundedEffect(MobEffectCategory.NEUTRAL, 0xB2E0ED));
    public static final RegistryObject<MobEffect> EFFECT_FATIGUED = MOB_EFFECTS.register("fatigued",
            () -> new FatiguedEffect(MobEffectCategory.HARMFUL, 0x542305));


    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
            ExoAdventureAdditions.MODID);
    public static final RegistryObject<SoundEvent> MUSIC_THE_DEPTHS = SOUND_EVENTS.register("music.the_depths", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(ExoAdventureAdditions.MODID, "music.the_depths")));
    public static final RegistryObject<SoundEvent> SOUND_TOME_CRAFT_SOUND_P = SOUND_EVENTS.register("tome_craft_sound_p", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(ExoAdventureAdditions.MODID, "tome_craft_sound_p")));
    public static final RegistryObject<SoundEvent> SOUND_TOME_CRAFT_SOUND_B = SOUND_EVENTS.register("tome_craft_sound_b", () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(ExoAdventureAdditions.MODID, "tome_craft_sound_b")));


    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ExoAdventureAdditions.MODID);
    public static final RegistryObject<Attribute> ATTRIBUTE_SKILL_EXP_BONUS = ATTRIBUTES.register("skills_xp_bonus",
            () -> new RangedAttribute("attribute.exoadvadditions.skills_xp_bonus",
                    1.0,
                    0.0,
                    127.0));

    public static final RegistryObject<Attribute> ATTRIBUTE_BALLISTICS_DAMAGE = ATTRIBUTES.register("ballistic_damage",
            () -> new RangedAttribute("attribute.exoadvadditions.ballistic_damage",
                    1.0,
                    0.0,
                    127.0));

    public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(Registries.CUSTOM_STAT, ExoAdventureAdditions.MODID);
    public static final RegistryObject<ResourceLocation> STAT_TOME_CRAFTS = STATS.register("tome_crafts", () -> ResourceLocation.fromNamespaceAndPath(ExoAdventureAdditions.MODID, "tome_crafts"));
    public static final RegistryObject<ResourceLocation> STAT_ADVANCED_TOME_CRAFTS = STATS.register("advanced_tome_crafts", () -> ResourceLocation.fromNamespaceAndPath(ExoAdventureAdditions.MODID, "advanced_tome_crafts"));

    public static final DeferredRegister<Brew> BOTANIA_BREWS = DeferredRegister.create(BotaniaRegistries.BREWS, ExoAdventureAdditions.MODID);
    public static final RegistryObject<Brew> WEAK_RESISTANCE = BOTANIA_BREWS.register("weak_resistance", () -> new Brew(0xB44E17, 4000, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600, 0)));
    public static final RegistryObject<Brew> EAGLE_EYE = BOTANIA_BREWS.register("eagle_eye", () -> new Brew(0x81AB0F, 4000, new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_EAGLE_EYE.get(), 1800, 1)));

    public static Item[] xpScrolls;

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

    public static Item ITEM_ETHERIUM_HELMET;
    public static Item ITEM_ETHERIUM_CHESTPLATE;
    public static Item ITEM_ETHERIUM_LEGGINGS;
    public static Item ITEM_ETHERIUM_BOOTS;

    public static Item ITEM_SPELLBREAKER;
    public static Item ITEM_AMETHYST_RAPIER;
    public static Item ITEM_KEEPER_FLAMBERGE;

    public static Item ITEM_THUNDERCALLER;
    public static Item ITEM_STARCALLER;

    public static Item ITEM_ANCIENT_TOME;

    public static Item ITEM_PONDIE;
    public static Item ITEM_WILDSPLASH;
    public static Item ITEM_SPLASHTAIL;
    public static Item ITEM_ISLEHOPPER;
    public static Item ITEM_ANCIENTSCALE;
    public static Item ITEM_BATTLEGILL;
    public static Item ITEM_WRECKER;
    public static Item ITEM_STORMFISH;
    public static Item ITEM_DEVILFISH;
    public static Item ITEM_PLENTIFIN;

    public static Attribute ATTRIBUTE_STAMINA;
    public static Attribute ATTRIBUTE_MAX_MANA;

    public static MobEffect RAISE_DEAD_TIMER;
    public static MobEffect POLAR_BEAR_TIMER;
    public static MobEffect VEX_TIMER;
    public static MobEffect SUMMON_HORSE_TIMER;
    public static ImmutableSet<MobEffect> SUMMON_EFFECTS;
    public static MobEffect OAKSKIN;

    public static TagKey<DamageType> GUN_DAMAGE;
    public static TagKey<Item> AQUATIC_WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "fisher_weapons"));

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
        LOOT_ITEM_CONDITIONS.register(modBus);
        STATS.register(modBus);
        BOTANIA_BREWS.register(modBus);
    }

    public static void setupOres() {
        IForgeRegistry<Block> forgeRegistry = ForgeRegistries.BLOCKS;

        Block TIN_ORE = forgeRegistry.getValue(ResourceLocation.fromNamespaceAndPath("thermal", "tin_ore"));
        Block DEEPSLATE_TIN_ORE = forgeRegistry.getValue(ResourceLocation.fromNamespaceAndPath("thermal", "deepslate_tin_ore"));
        Block SILVER_ORE = forgeRegistry.getValue(ResourceLocation.fromNamespaceAndPath("thermal", "silver_ore"));
        Block DEEPSLATE_SILVER_ORE = forgeRegistry.getValue(ResourceLocation.fromNamespaceAndPath("thermal", "deepslate_silver_ore"));
        Block NICKEL_ORE =  forgeRegistry.getValue(ResourceLocation.fromNamespaceAndPath("thermal", "nickel_ore"));
        Block DEEPSLATE_NICKEL_ORE = forgeRegistry.getValue(ResourceLocation.fromNamespaceAndPath("thermal", "deepslate_nickel_ore"));
        Block NITER_ORE = forgeRegistry.getValue(ResourceLocation.fromNamespaceAndPath("thermal", "niter_ore"));
        Block DEEPSLATE_NITER_ORE = forgeRegistry.getValue(ResourceLocation.fromNamespaceAndPath("thermal", "deepslate_niter_ore"));
        Block ZINC_ORE = forgeRegistry.getValue(ResourceLocation.fromNamespaceAndPath("create", "zinc_ore"));
        Block DEEPSLATE_ZINC_ORE = forgeRegistry.getValue(ResourceLocation.fromNamespaceAndPath("create", "deepslate_zinc_ore"));

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
        event.add(EntityType.PLAYER, ATTRIBUTE_BALLISTICS_DAMAGE.get());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IDaytimeDilationStorage.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Level> event) {
        event.addCapability(ResourceLocation.fromNamespaceAndPath(ExoAdventureAdditions.MODID, "daytime_dilation"), new DaytimeDilationProvider());
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
        if (tabLocation.equals(ResourceLocation.fromNamespaceAndPath("exoadvadditions", "adventure_maps"))) {
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

    public static void fillInAfterRegistry() {

        GUN_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("tacz", "bullets"));

        ENTITY_COLELYTRA = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("mythicmounts", "colelytra"));
        ENTITY_DRAGON = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("mythicmounts", "dragon"));
        ENTITY_FIREBIRD = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("mythicmounts", "firebird"));
        ENTITY_GRIFFON = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("mythicmounts", "griffon"));
        ENTITY_MOTH = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("mythicmounts", "moth"));
        ENTITY_NETHER_BAT = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("mythicmounts", "netherbat"));
        ENTITY_NUDIBRANCH = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("mythicmounts", "nudibranch"));

        ITEM_SPELLBREAKER = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "spellbreaker"));
        ITEM_AMETHYST_RAPIER = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "amethyst_rapier"));
        ITEM_KEEPER_FLAMBERGE = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "keeper_flamberge"));

        ITEM_THUNDERCALLER = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("botania", "thunder_sword"));
        ITEM_STARCALLER = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("botania", "star_sword"));

        ITEM_ANCIENT_TOME = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("quark", "ancient_tome"));

        ITEM_ETHERIUM_HELMET = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("enigmaticlegacy", "etherium_helmet"));
        ITEM_ETHERIUM_CHESTPLATE = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("enigmaticlegacy", "etherium_chestplate"));
        ITEM_ETHERIUM_LEGGINGS = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("enigmaticlegacy", "etherium_leggings"));
        ITEM_ETHERIUM_BOOTS = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("enigmaticlegacy", "etherium_boots"));

        ITEM_ETHERIUM_LEGGINGS = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("enigmaticlegacy", "etherium_leggings"));

        ITEM_PONDIE = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("fishofthieves", "pondie"));
        ITEM_WILDSPLASH = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("fishofthieves", "wildsplash"));
        ITEM_SPLASHTAIL = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("fishofthieves", "splashtail"));
        ITEM_ISLEHOPPER = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("fishofthieves", "islehopper"));
        ITEM_ANCIENTSCALE = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("fishofthieves", "ancientscale"));
        ITEM_BATTLEGILL = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("fishofthieves", "battlegill"));
        ITEM_WRECKER = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("fishofthieves", "wrecker"));
        ITEM_STORMFISH = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("fishofthieves", "stormfish"));
        ITEM_DEVILFISH = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("fishofthieves", "devilfish"));
        ITEM_PLENTIFIN = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("fishofthieves", "plentifin"));

        ATTRIBUTE_STAMINA = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.bySeparator("puffish_attributes:stamina", ':'));
        ATTRIBUTE_MAX_MANA = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.bySeparator("irons_spellbooks:max_mana", ':'));

        RAISE_DEAD_TIMER = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "raise_dead_timer"));
        POLAR_BEAR_TIMER = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "polar_bear_timer"));
        VEX_TIMER = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "vex_timer"));
        SUMMON_HORSE_TIMER = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "summon_horse_timer"));

        SUMMON_EFFECTS = ImmutableSet.of(RAISE_DEAD_TIMER, POLAR_BEAR_TIMER, VEX_TIMER, SUMMON_HORSE_TIMER);

        OAKSKIN = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "oakskin"));

        xpScrolls = new Item[]{ ITEM_SCROLL_OF_SKILL_XP_COMBAT.get(), ITEM_SCROLL_OF_SKILL_XP_MINING.get(), ITEM_SCROLL_OF_SKILL_XP_HUSBANDRY.get(),
                ITEM_SCROLL_OF_SKILL_XP_FISHING.get(), ITEM_SCROLL_OF_SKILL_XP_OCCULT.get(), ITEM_SCROLL_OF_SKILL_XP_EXPLORATION.get() };

        ITEM_MAGE_TEMPLATE.get().setAttributes(Pair.of(ATTRIBUTE_MAX_MANA, new AttributeModifier("Template", 125.0, AttributeModifier.Operation.ADDITION)),
                Pair.of(com.exosomnia.exoskills.Registry.ATTRIBUTE_MAGIC_DAMAGE, new AttributeModifier("Template", 0.05, AttributeModifier.Operation.MULTIPLY_BASE)));

        ITEM_RANGER_TEMPLATE.get().setAttributes(Pair.of(ExoArmory.REGISTRY.ATTRIBUTE_ARROW_PIERCE.get(), new AttributeModifier("Template", 0.25, AttributeModifier.Operation.MULTIPLY_BASE)),
                Pair.of(ExoArmory.REGISTRY.ATTRIBUTE_RANGED_STRENGTH.get(), new AttributeModifier("Template", 0.05, AttributeModifier.Operation.MULTIPLY_BASE)));

        ITEM_FIGHTER_TEMPLATE.get().setAttributes(Pair.of(Attributes.MAX_HEALTH, new AttributeModifier("Template", 2.5, AttributeModifier.Operation.ADDITION)),
                Pair.of(Attributes.ATTACK_DAMAGE, new AttributeModifier("Template", 0.25, AttributeModifier.Operation.ADDITION)));

        ITEM_ARMOR_REINFORCEMENT_TEMPLATE.get().setAttributes(Pair.of(Attributes.ARMOR, new AttributeModifier("Template", 0.5, AttributeModifier.Operation.ADDITION)),
                Pair.of(Attributes.ARMOR_TOUGHNESS, new AttributeModifier("Template", 2.0, AttributeModifier.Operation.ADDITION)));

        ATTRIBUTE_TEMPLATES = ImmutableMap.of(ITEM_MAGE_TEMPLATE.get().getId(), ITEM_MAGE_TEMPLATE.get(),
                ITEM_RANGER_TEMPLATE.get().getId(), ITEM_RANGER_TEMPLATE.get(),
                ITEM_FIGHTER_TEMPLATE.get().getId(), ITEM_FIGHTER_TEMPLATE.get(),
                ITEM_ARMOR_REINFORCEMENT_TEMPLATE.get().getId(), ITEM_ARMOR_REINFORCEMENT_TEMPLATE.get());

        RegistryTomeRecipes.registerRecipes();

        FatiguedEffect.handleIntegrations(EFFECT_FATIGUED.get());
    }
}
