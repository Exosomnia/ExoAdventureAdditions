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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
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
