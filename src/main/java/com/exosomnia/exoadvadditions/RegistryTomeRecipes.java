package com.exosomnia.exoadvadditions;

import com.exosomnia.exoadvadditions.networking.PacketHandler;
import com.exosomnia.exoadvadditions.networking.packets.SimpleMusicPacket;
import com.exosomnia.exoadvadditions.recipes.tome.AdvancedShapedTomeRecipe;
import com.exosomnia.exoadvadditions.recipes.tome.ShapedTomeRecipe;
import com.exosomnia.exoadvadditions.recipes.tome.TomeRecipe;
import com.exosomnia.exoskills.ExoSkills;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class RegistryTomeRecipes {

    public static void registerRecipes() {
        final TagKey<Item> tagEndRemEyes = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadventure", "end_rem_eyes"));
        final TagKey<Item> tagOccultItems = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "occult_items"));
        final TagKey<Item> tagMacguffinItems = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "macguffins"));
        final TagKey<Item> tagWool = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "wool"));

        final Item itemCrypticEye = findItem("endrem", "cryptic_eye");
        final Item itemEnderAir = findItem("botania", "ender_air_bottle");
        final Item itemWarpStone = findItem("waystones", "warp_stone");
        final Item itemWarpDust = findItem("waystones", "warp_dust");
        final Item itemPortstone = findItem("waystones", "portstone");
        final Item itemWaystone = findItem("waystones", "waystone");
        final Item itemAnimalGuidebook = findItem("enigmaticlegacy", "animal_guidebook");
        final Item itemHunterGuidebook = findItem("enigmaticlegacy", "hunter_guidebook");
        final Item itemEnchantmentTransposer = findItem("enigmaticlegacy", "enchantment_transposer");
        final Item itemWaterRune = findItem("botania", "rune_water");
        final Item itemEarthRune = findItem("botania", "rune_earth");
        final Item itemManaRune = findItem("botania", "rune_mana");
        final Item itemAirRune = findItem("botania", "rune_air");
        final Item itemFireRune = findItem("botania", "rune_fire");
        final Item itemTerraPlate = findItem("botania", "terra_plate");
        final Item itemNaturesCompass = findItem("naturescompass", "naturescompass");
        final Item itemExplorersCompass = findItem("explorerscompass", "explorerscompass");
        final Item itemEarthHeart = findItem("enigmaticlegacy", "earth_heart");
        final Item itemInfinimeal = findItem("enigmaticlegacy", "infinimeal");
        final Item itemBottledCloud = findItem("quark", "bottled_cloud");
        final Item itemSkyStoneBlock = findItem("ae2", "sky_stone_block");
        final Item itemSiliconPress = findItem("ae2", "silicon_press");
        final Item itemLogicProcessorPress = findItem("ae2", "logic_processor_press");
        final Item itemEngineeringProcessorPress = findItem("ae2", "engineering_processor_press");
        final Item itemCalculationProcessorPress = findItem("ae2", "calculation_processor_press");
        final Item itemFlawlessBuddingQuartz = findItem("ae2", "flawless_budding_quartz");
        final Item itemFlawedBuddingQuartz = findItem("ae2", "flawed_budding_quartz");
        final Item itemCertusQuartz = findItem("ae2", "certus_quartz_crystal");
        final Item itemDiamondHeart = findItem("quark", "diamond_heart");

        final Item itemEnderiumShard = findItem("majruszsdifficulty", "enderium_shard");
        final Item itemEnderiumIngot = findItem("majruszsdifficulty", "enderium_ingot");
        final Item itemEnderiumUpgradeTemplate = findItem("majruszsdifficulty", "enderium_upgrade_smithing_template");

        final Item itemEtheriumIngot = findItem("enigmaticlegacy", "etherium_ingot");
        final Item itemEnderRod = findItem("enigmaticlegacy", "ender_rod");
        final Item itemEtheriumNugget = findItem("enigmaticlegacy", "etherium_nugget");

        final Item itemFirebirdEgg = findItem("mythicmounts", "firebird_spawn_egg");
        final Item itemGriffonEgg = findItem("mythicmounts", "griffon_spawn_egg");
        final Item itemColelytraEgg = findItem("mythicmounts", "colelytra_spawn_egg");
        final Item itemNetherBatEgg = findItem("mythicmounts", "netherbat_spawn_egg");

        final Item itemArcaneEssence = findItem("irons_spellbooks", "arcane_essence");
        final Item itemBlankRunestone = findItem("irons_spellbooks", "blank_rune");

        final Block blockLivingrock = findBlock("botania", "livingrock");
        final Block blockSkyStone = findBlock("ae2", "sky_stone_block");

        //region Fun & Quirky Crafts
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"OOO", "OOO", "OOO"})
                .midLayer(new String[]{"WWW", "W W", "WWW"})
                .lowLayer(new String[]{"WWW", "WWW", "WWW"})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(Blocks.ORANGE_WOOL), 'W', TomeRecipe.BlockMapping.of(Blocks.WHITE_WOOL)))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.CARROT), 1), TomeRecipe.ItemMapping.of(Ingredient.of(Items.DIAMOND_BLOCK), 1)))
                .result(new ItemStack(Registry.ITEM_DIAMOND_CARROT.get(), 1), 5120)
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

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"BBB", "BBB", "BBB"})
                .midLayer(new String[]{"QQQ", "Q Q", "QQQ"})
                .lowLayer(new String[]{"PPP", "PPP", "PPP"})
                .blockMappings(ImmutableMap.of('B', TomeRecipe.BlockMapping.of(Blocks.BLUE_WOOL), 'P', TomeRecipe.BlockMapping.of(Blocks.PINK_WOOL), 'Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK)))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.BONE), 2), TomeRecipe.ItemMapping.of(Ingredient.of(Items.DIAMOND_BLOCK), 1)))
                .result(new ItemStack(Items.WOLF_SPAWN_EGG), 5120, false)
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
        //endregion

        //region Permanent Buff Tomes
        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"RRRRR", "RIIIR", "RIRIR", "RIIIR", "RRRRR"})
                .withLayer(new String[]{"RIIIR", "I   I", "I   I", "I   I", "RIIIR"})
                .withLayer(new String[]{"RIRIR", "I   I", "R   R", "I   I", "RIRIR"})
                .withLayer(new String[]{"RIIIR", "I   I", "I   I", "I   I", "RIIIR"})
                .withLayer(new String[]{"RRRRR", "RIIIR", "RIRIR", "RIIIR", "RRRRR"})
                .blockMappings(ImmutableMap.of('I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron"))),
                        'R', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/redstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MYSTERIOUS_BLUEPRINT_SHAPES.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEnderiumIngot), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", "ingots/copper"))), 256),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.GUNPOWDER), 256),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENDER_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_TOME_OF_BALLISTICS.get()), 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"HHH", "HHH", "HHH"})
                .midLayer(new String[]{"HEH", "E E", "HEH"})
                .lowLayer(new String[]{"HHH", "HHH", "HHH"})
                .blockMappings(ImmutableMap.of('H', TomeRecipe.BlockMapping.of(Blocks.HAY_BLOCK),
                        'E', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/emerald")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "veggie_blocks"))), 64),
                        TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "veggie_blocks"))), 64),
                        TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "veggie_blocks"))), 64),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_DIAMOND_CARROT.get()), 2)))
                .result(new ItemStack(Registry.ITEM_TOME_OF_STAMINA.get(), 1), 5120)
                .withHelp(List.of("recipe.help.stamina_tome.1", "recipe.help.stamina_tome.2"))
                .build());
        //endregion

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"AQA", "QGQ", "AQA"})
                .midLayer(new String[]{"QGQ", "G G", "QGQ"})
                .lowLayer(new String[]{"AQA", "QGQ", "AQA"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/gold"))),
                        'Q', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/copper"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/emerald")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.ENCHANTED_BOOK), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_OLD_MANUSCRIPT.get()), 1)))
                .execute((player, position) -> {
                    ServerLevel level = player.serverLevel();
                    Vec3 center = position.getCenter();
                    if (level.random.nextDouble() < (1.0/6.0)) {
                        level.playSound(null, position, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 0.5F);
                        for(ServerPlayer connected : level.players()) {
                            level.sendParticles(connected, ParticleTypes.WITCH, false, center.x, center.y, center.z, 15, 0, 0, 0, 2.0);
                        }
                        ItemEntity drop = new ItemEntity(EntityType.ITEM, level);
                        drop.setItem(new ItemStack(Registry.ITEM_TOME_OF_LUCK.get()));
                        drop.setPos(center);
                        level.addFreshEntity(drop);
                    } else {
                        level.playSound(null, position, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
                        for(ServerPlayer connected : level.players()) {
                            level.sendParticles(connected, ParticleTypes.SMOKE, false, center.x, center.y, center.z, 15, 0, 0, 0, 0.025);
                        }
                    }
                })
                .result(new ItemStack(Registry.ITEM_TOME_OF_LUCK.get()), 5120, false)
                .withHelp(List.of("recipe.help.luck_tome.1", "recipe.help.luck_tome.2"))
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"GAG", "AEA", "GAG"})
                .midLayer(new String[]{" B ", "B B", " B "})
                .lowLayer(new String[]{"GAG", "AEA", "GAG"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/gold"))),
                        'E', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/emerald"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_OLD_MANUSCRIPT.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EXPERIENCE_BOTTLE), 1)))
                .result(new ItemStack(Registry.ITEM_SCROLL_OF_SKILL_XP_RANDOM.get()), 5120)
                .build());

        //region AE2 Crafts
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"SSS", "SIS", "SSS"})
                .midLayer(new String[]{"SIS", "I I", "SIS"})
                .lowLayer(new String[]{"SSS", "SIS", "SSS"})
                .blockMappings(ImmutableMap.of('S', TomeRecipe.BlockMapping.of(blockSkyStone),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", "gems/certus_quartz"))), 1)))
                .result(new ItemStack(itemSiliconPress), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"SSS", "SIS", "SSS"})
                .midLayer(new String[]{"SIS", "I I", "SIS"})
                .lowLayer(new String[]{"SSS", "SIS", "SSS"})
                .blockMappings(ImmutableMap.of('S', TomeRecipe.BlockMapping.of(blockSkyStone),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.IRON_INGOT), 1)))
                .result(new ItemStack(itemLogicProcessorPress), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"SSS", "SIS", "SSS"})
                .midLayer(new String[]{"SIS", "I I", "SIS"})
                .lowLayer(new String[]{"SSS", "SIS", "SSS"})
                .blockMappings(ImmutableMap.of('S', TomeRecipe.BlockMapping.of(blockSkyStone),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.GOLD_INGOT), 1)))
                .result(new ItemStack(itemEngineeringProcessorPress), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"SSS", "SIS", "SSS"})
                .midLayer(new String[]{"SIS", "I I", "SIS"})
                .lowLayer(new String[]{"SSS", "SIS", "SSS"})
                .blockMappings(ImmutableMap.of('S', TomeRecipe.BlockMapping.of(blockSkyStone),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.DIAMOND), 1)))
                .result(new ItemStack(itemCalculationProcessorPress), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"SSS", "SLS", "SSS"})
                .midLayer(new String[]{"SLS", "L L", "SLS"})
                .lowLayer(new String[]{"SSS", "SLS", "SSS"})
                .blockMappings(ImmutableMap.of('S', TomeRecipe.BlockMapping.of(blockSkyStone),
                        'L', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/lapis")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.QUARTZ), 16),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 16)))
                .result(new ItemStack(itemCertusQuartz, 32), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"SSS", "SSS", "SSS"})
                .midLayer(new String[]{"BEB", "E E", "BEB"})
                .lowLayer(new String[]{"SSS", "SSS", "SSS"})
                .blockMappings(ImmutableMap.of('S', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "stone"))),
                        'E', TomeRecipe.BlockMapping.of(Blocks.END_STONE),
                        'B', TomeRecipe.BlockMapping.of(Blocks.BLACKSTONE)))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.REDSTONE_BLOCK), 1)))
                .result(new ItemStack(itemSkyStoneBlock, 27), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{" QQQ ", "QQCQQ", "QCGCQ", "QQCQQ", " QQQ "})
                .withLayer(new String[]{"QQCQQ", "Q   Q", "C   C", "Q   Q", "QQCQQ"})
                .withLayer(new String[]{"QCGCQ", "C   C", "G   G", "C   C", "QCGCQ"})
                .withLayer(new String[]{"QQCQQ", "Q   Q", "C   C", "Q   Q", "QQCQQ"})
                .withLayer(new String[]{" QQQ ", "QQCQQ", "QCGCQ", "QQCQQ", " QQQ "})
                .blockMappings(ImmutableMap.of('Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK),
                        'C', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/certus_quartz"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(itemFlawedBuddingQuartz), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/diamond"))), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 2)))
                .result(new ItemStack(itemFlawlessBuddingQuartz),5120)
                .build());
        //endregion

        //region Macguffin crafts
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"A A", " C ", "A A"})
                .midLayer(new String[]{"ACA", "C C", "ACA"})
                .lowLayer(new String[]{"G G", " C ", "G G"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'C', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/copper"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.EMERALD_BLOCK), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_RUNES.get()), 1)))
                .result(new ItemStack(Registry.ITEM_MACGUFFIN_15_UNFINISHED_1.get()), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"A A", " I ", "A A"})
                .midLayer(new String[]{"AIA", "I I", "AIA"})
                .lowLayer(new String[]{"G G", " I ", "G G"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MACGUFFIN_15_UNFINISHED_1.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_OLD_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_MACGUFFIN_15_UNFINISHED_2.get()), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"A A", " L ", "A A"})
                .midLayer(new String[]{"ALA", "L L", "ALA"})
                .lowLayer(new String[]{"G G", " L ", "G G"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'L', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/gold"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MACGUFFIN_15_UNFINISHED_2.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_INFERNAL_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_MACGUFFIN_15_UNFINISHED_3.get()), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"A A", " D ", "A A"})
                .midLayer(new String[]{"ADA", "D D", "ADA"})
                .lowLayer(new String[]{"G G", " D ", "G G"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'D', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/diamond"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MACGUFFIN_15_UNFINISHED_3.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENDER_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_MACGUFFIN_15_UNFINISHED_4.get()), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{" QGQ ", "QQ QQ", "G   G", "QQ QQ", " QGQ "})
                .withLayer(new String[]{"QQ QQ", "QAAAQ", " AAA ", "QAAAQ", "QQ QQ"})
                .withLayer(new String[]{"G N G", " AAA ", "NA AN", " AAA ", "G N G"})
                .withLayer(new String[]{"QQ QQ", "QAAAQ", " AAA ", "QAAAQ", "QQ QQ"})
                .withLayer(new String[]{" QGQ ", "QQ QQ", "G   G", "QQ QQ", " QGQ "})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK),
                        'N', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/netherite"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MACGUFFIN_15_UNFINISHED_4.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ANCIENT_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_MACGUFFIN_15.get()), 10240)
                .build());
        //endregion

        //region Resource Crafts
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"HHH", "HHH", "HHH"})
                .midLayer(new String[]{"HEH", "E E", "HEH"})
                .lowLayer(new String[]{"HHH", "HHH", "HHH"})
                .blockMappings(ImmutableMap.of('H', TomeRecipe.BlockMapping.of(Blocks.HAY_BLOCK),
                        'E', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/emerald")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "veggie_blocks"))), 64),
                        TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "veggie_blocks"))), 64),
                        TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "veggie_blocks"))), 64),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_DIAMOND_CARROT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_TOME_OF_STAMINA.get(), 1), 5120)
                .withHelp(List.of("recipe.help.stamina_tome.1", "recipe.help.stamina_tome.2"))
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"AQA", "QGQ", "AQA"})
                .midLayer(new String[]{"QGQ", "G G", "QGQ"})
                .lowLayer(new String[]{"AQA", "QGQ", "AQA"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.LAPIS_BLOCK), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.DIAMOND), 2)))
                .result(new ItemStack(Registry.ITEM_MAGICAL_RUNES.get()), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"NGN", "GOG", "NGN"})
                .midLayer(new String[]{" O ", "O O", " O "})
                .lowLayer(new String[]{"NGN", "GOG", "NGN"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'N', TomeRecipe.BlockMapping.of(Blocks.NETHER_BRICKS),
                        'O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_RUNES.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.OBSIDIAN), 6)))
                .result(new ItemStack(Items.CRYING_OBSIDIAN, 12), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"NGN", "GOG", "NGN"})
                .midLayer(new String[]{" O ", "O O", " O "})
                .lowLayer(new String[]{"NGN", "GOG", "NGN"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'N', TomeRecipe.BlockMapping.of(Blocks.NETHER_BRICKS),
                        'O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.NETHERITE_SCRAP), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BLAZE_POWDER), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_OLD_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_INFERNAL_MANUSCRIPT.get()), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"     ", "     ", "  O  ", "     ", "     "})
                .withLayer(new String[]{"  O  ", "     ", "O A O", "     ", "  O  "})
                .withLayer(new String[]{"  C  ", "  S  ", "CS SC", "  S  ", "  C  "})
                .withLayer(new String[]{"  O  ", "     ", "O A O", "     ", "  O  "})
                .withLayer(new String[]{"     ", "     ", "  O  ", "     ", "     "})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'C', TomeRecipe.BlockMapping.of(Blocks.CRYING_OBSIDIAN),
                        'S', TomeRecipe.BlockMapping.of(Blocks.SCULK)))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(itemEtheriumNugget), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEnderAir), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_INFERNAL_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_ENDER_MANUSCRIPT.get()), 10240)
                .build());
        //endregion

        //region Tool Crafts
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"AQA", "QGQ", "AQA"})
                .midLayer(new String[]{"QGQ", "G G", "QGQ"})
                .lowLayer(new String[]{"AQA", "QGQ", "AQA"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK), 'Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK), 'G', TomeRecipe.BlockMapping.of(Blocks.GLOWSTONE)))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(tagEndRemEyes), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(tagEndRemEyes), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EXPERIENCE_BOTTLE), 1)))
                .result(new ItemStack(itemCrypticEye), 5120)
                .withHelp(List.of("recipe.help.cryptic_eye.1", "recipe.help.cryptic_eye.2"))
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"LAL", "AIA", "LAL"})
                .midLayer(new String[]{"GIG", "I I", "GIG"})
                .lowLayer(new String[]{"LAL", "AIA", "LAL"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron"))),
                        'L', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "leaves"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "logs")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.LODESTONE), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_RUNES.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.DIAMOND), 2)))
                .result(new ItemStack(itemNaturesCompass), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"BLB", "LFL", "BLB"})
                .midLayer(new String[]{"DFD", "F F", "DFD"})
                .lowLayer(new String[]{"BLB", "LFL", "BLB"})
                .blockMappings(ImmutableMap.of('F', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "froglights"))),
                        'D', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/diamond"))),
                        'L', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "leaves"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(itemEarthHeart), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.CHORUS_FLOWER), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.CACTUS), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.NETHER_WART), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BONE_MEAL), 1)))
                .result(new ItemStack(itemInfinimeal), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"  D  ", " SIS ", "DIGID", " SIS ", "  D  "})
                .withLayer(new String[]{" SIS ", "S   S", "I N I", "S   S", " SIS "})
                .withLayer(new String[]{"DIGID", "I   I", "G C G", "I   I", "DIGID"})
                .withLayer(new String[]{" SIS ", "S   S", "I   I", "S   S", " SIS "})
                .withLayer(new String[]{"  D  ", " SIS ", "DIGID", " SIS ", "  D  "})
                .blockMappings(ImmutableMap.of('S', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "stone_bricks"))),
                        'C', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "chests/wooden"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron"))),
                        'D', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/diamond"))),
                        'N', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/netherite")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ETERNA_CRYSTALIS.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "adventure_macguffins"))), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "adventure_macguffins"))), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "adventure_macguffins"))), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "adventure_macguffins"))), 1)))
                .result(new ItemStack(itemExplorersCompass),5120)
                .withHelp(List.of("recipe.help.explore_compass.1", "recipe.help.explore_compass.2"))
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"O   O", "     ", "     ", "     ", "O   O"})
                .withLayer(new String[]{"E P E", " OPO ", "PPDPP", " OPO ", "E P E"})
                .withLayer(new String[]{"EODOE", "OO OO", "D   D", "OO OO", "EODOE"})
                .withLayer(new String[]{"E P E", " OPO ", "PPDPP", " OPO ", "E P E"})
                .withLayer(new String[]{"N   N", "     ", "     ", "     ", "N   N"})
                .blockMappings(ImmutableMap.of('E', TomeRecipe.BlockMapping.of(Blocks.END_STONE_BRICKS),
                        'P', TomeRecipe.BlockMapping.of(Blocks.PURPUR_BLOCK),
                        'O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'D', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/diamond"))),
                        'N', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/netherite")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_UNFINISHED_ELYTRA.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_VOID_RUNES.get()), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ETERNA_CRYSTALIS.get()), 1)))
                .result(new ItemStack(Items.ELYTRA), 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{" OLO ", "O O O", "LOGOL", "O O O", " OLO "})
                .withLayer(new String[]{"O O O", " AAA ", "OAAAO", " AAA ", "O O O"})
                .withLayer(new String[]{"LOGOL", "OAAAO", "GA AG", "OAAAO", "LOGOL"})
                .withLayer(new String[]{"O O O", " AAA ", "OAAAO", " AAA ", "O O O"})
                .withLayer(new String[]{" OLO ", "O O O", "LOGOL", "O O O", " OLO "})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'L', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/lapis")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(com.exosomnia.exoskills.Registry.ITEM_ONYX.get()), 8),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EXPERIENCE_BOTTLE), 8),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.NETHERITE_INGOT), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.DIAMOND_BLOCK), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_OLD_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_FLAWLESS_ONYX.get()), 10240)
                .build());

        ItemStack ancientTomePackage = new ItemStack(Registry.ITEM_MYSTERIOUS_PACKAGE.get());
        CompoundTag compoundTag = new CompoundTag();
        ListTag listTag = new ListTag();
        CompoundTag contentsTag = new CompoundTag();
        contentsTag.putString("table", "exoadvadditions:gameplay/ancient_tome_package");
        listTag.add(contentsTag);
        compoundTag.put("contents", listTag);
        ancientTomePackage.setTag(compoundTag);
        ancientTomePackage.setHoverName(Component.translatable("craft.exoadvadditions.ancient_tome_package"));
        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{" OLO ", "O O O", "LOGOL", "O O O", " OLO "})
                .withLayer(new String[]{"O O O", " AAA ", "OAAAO", " AAA ", "O O O"})
                .withLayer(new String[]{"LOGOL", "OAAAO", "GA AG", "OAAAO", "LOGOL"})
                .withLayer(new String[]{"O O O", " AAA ", "OAAAO", " AAA ", "O O O"})
                .withLayer(new String[]{" OLO ", "O O O", "LOGOL", "O O O", " OLO "})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'L', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/lapis")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(com.exosomnia.exoskills.Registry.ITEM_HORIZON_OF_FATE.get()), 64),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 256),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BOOK), 8),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.SCULK), 8),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.DIAMOND_BLOCK), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_OLD_MANUSCRIPT.get()), 1)))
                .result(ancientTomePackage, 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{" OLO ", "O O O", "LOGOL", "O O O", " OLO "})
                .withLayer(new String[]{"O O O", " AAA ", "OAAAO", " AAA ", "O O O"})
                .withLayer(new String[]{"LOGOL", "OAAAO", "GA AG", "OAAAO", "LOGOL"})
                .withLayer(new String[]{"O O O", " AAA ", "OAAAO", " AAA ", "O O O"})
                .withLayer(new String[]{" OLO ", "O O O", "LOGOL", "O O O", " OLO "})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'L', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/lapis")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_BLANK_SMITHING_TEMPLATE.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEtheriumIngot), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEnderRod), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENDER_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_ETHERIUM_UPGRADE_TEMPLATE.get()), 10240)
                .build());
        //endregion

        //region Botania Recipes
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"RLR", "LLL", "RLR"})
                .midLayer(new String[]{" M ", "M M", " M "})
                .lowLayer(new String[]{"RLR", "LLL", "RLR"})
                .blockMappings(ImmutableMap.of('R', TomeRecipe.BlockMapping.of(blockLivingrock),
                        'M', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/manasteel"))),
                        'L', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/lapis")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(itemWaterRune), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEarthRune), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemManaRune), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemAirRune), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemFireRune), 1)))
                .result(new ItemStack(itemTerraPlate), 5120)
                .build());
        //endregion

        //region Growth Scrolls - Tier 1
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"IAI", "AIA", "IAI"})
                .midLayer(new String[]{"BBB", "B B", "BBB"})
                .lowLayer(new String[]{"IAI", "AIA", "IAI"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_OLD_MANUSCRIPT.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICKED_FEATHER.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EXPERIENCE_BOTTLE), 2)))
                .result(new ItemStack(Registry.ITEM_SCROLL_OF_GROWTH.get()), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"GAG", "AGA", "GAG"})
                .midLayer(new String[]{"BBB", "B B", "BBB"})
                .lowLayer(new String[]{"GAG", "AGA", "GAG"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/gold"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_SCROLL_OF_GROWTH.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_INFERNAL_MANUSCRIPT.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_INFERNAL_FEATHER.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EXPERIENCE_BOTTLE), 2)))
                .result(new ItemStack(Registry.ITEM_INFERNAL_SCROLL_OF_GROWTH.get()), 5120)
                .build());
        //endregion

        //region Waystone Related
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"ASA", "SIS", "ASA"})
                .midLayer(new String[]{"SGS", "G G", "SGS"})
                .lowLayer(new String[]{"ASA", "SIS", "ASA"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron"))),
                        'S', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "stone_bricks"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 4),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.DIAMOND), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_RUNES.get()), 1)))
                .result(new ItemStack(itemWarpStone), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"ASA", "SIS", "ASA"})
                .midLayer(new String[]{"SGS", "G G", "SGS"})
                .lowLayer(new String[]{"ASA", "SIS", "ASA"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron"))),
                        'S', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "stone_bricks"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(itemPortstone), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemWarpDust), 4),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.OBSIDIAN), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEnderAir), 2)))
                .result(new ItemStack(itemWaystone), 5120)
                .build());
        //endregion

        //region Summon Recipes
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"WGW", "GHG", "WGW"})
                .midLayer(new String[]{"GHG", "H H", "GHG"})
                .lowLayer(new String[]{"WGW", "GHG", "WGW"})
                .blockMappings(ImmutableMap.of('H', TomeRecipe.BlockMapping.of(Blocks.HAY_BLOCK),
                        'W', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "full_wood"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/gold")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_FIERY_INGOT.get()), 16),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICKED_FEATHER.get()), 16),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_RUNES.get()), 4),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EGG), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemDiamondHeart), 1)))
                .result(new ItemStack(itemFirebirdEgg), 5120, false)
                .execute((player, position) -> {
                    ServerLevel level = player.serverLevel();
                    Entity summon = Registry.ENTITY_FIREBIRD.create(level);
                    summon.getPersistentData().putBoolean("validSpawn", true);
                    summon.setPos(position.getCenter());
                    level.addFreshEntity(summon);
                })
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"WGW", "GHG", "WGW"})
                .midLayer(new String[]{"GHG", "H H", "GHG"})
                .lowLayer(new String[]{"WGW", "GHG", "WGW"})
                .blockMappings(ImmutableMap.of('H', TomeRecipe.BlockMapping.of(Blocks.HAY_BLOCK),
                        'W', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "full_wood"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/gold")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(tagWool), 24),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemBottledCloud), 8),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICKED_FEATHER.get()), 16),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_RUNES.get()), 4),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EGG), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemDiamondHeart), 1)))
                .result(new ItemStack(itemGriffonEgg), 5120, false)
                .execute((player, position) -> {
                    ServerLevel level = player.serverLevel();
                    Entity summon = Registry.ENTITY_GRIFFON.create(level);
                    summon.getPersistentData().putBoolean("validSpawn", true);
                    summon.setPos(position.getCenter());
                    level.addFreshEntity(summon);
                })
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"WGW", "GHG", "WGW"})
                .midLayer(new String[]{"GHG", "H H", "GHG"})
                .lowLayer(new String[]{"WGW", "GHG", "WGW"})
                .blockMappings(ImmutableMap.of('H', TomeRecipe.BlockMapping.of(Blocks.HAY_BLOCK),
                        'W', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "full_wood"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/gold")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.RED_MUSHROOM_BLOCK), 32),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICKED_FEATHER.get()), 16),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_RUNES.get()), 4),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EGG), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemDiamondHeart), 1)))
                .result(new ItemStack(itemColelytraEgg), 5120, false)
                .execute((player, position) -> {
                    ServerLevel level = player.serverLevel();
                    Entity summon = Registry.ENTITY_COLELYTRA.create(level);
                    summon.getPersistentData().putBoolean("validSpawn", true);
                    summon.setPos(position.getCenter());
                    level.addFreshEntity(summon);
                })
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"WGW", "GHG", "WGW"})
                .midLayer(new String[]{"GHG", "H H", "GHG"})
                .lowLayer(new String[]{"WGW", "GHG", "WGW"})
                .blockMappings(ImmutableMap.of('H', TomeRecipe.BlockMapping.of(Blocks.HAY_BLOCK),
                        'W', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("exoadvadditions", "full_wood"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/gold")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.SOUL_SAND), 32),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICKED_FEATHER.get()), 16),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_RUNES.get()), 4),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EGG), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemDiamondHeart), 1)))
                .result(new ItemStack(itemNetherBatEgg), 5120, false)
                .execute((player, position) -> {
                    ServerLevel level = player.serverLevel();
                    Entity summon = Registry.ENTITY_NETHER_BAT.create(level);
                    summon.getPersistentData().putBoolean("validSpawn", true);
                    summon.setPos(position.getCenter());
                    level.addFreshEntity(summon);
                })
                .build());
        //endregion

        //region Book related recipes
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"BAB", "ALA", "BAB"})
                .midLayer(new String[]{"GLG", "L L", "GLG"})
                .lowLayer(new String[]{"BAB", "ALA", "BAB"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/gold"))),
                        'L', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/lapis"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.HAY_BLOCK), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.LEAD), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BOOK), 1)))
                .result(new ItemStack(itemAnimalGuidebook), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"BAB", "ALA", "BAB"})
                .midLayer(new String[]{"GLG", "L L", "GLG"})
                .lowLayer(new String[]{"BAB", "ALA", "BAB"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/gold"))),
                        'L', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/lapis"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.EMERALD), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.GOLDEN_APPLE), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BOOK), 1)))
                .result(new ItemStack(itemHunterGuidebook), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"BAB", "ALA", "BAB"})
                .midLayer(new String[]{"GLG", "L L", "GLG"})
                .lowLayer(new String[]{"BAB", "ALA", "BAB"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/gold"))),
                        'L', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/lapis"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.BLAZE_POWDER), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.DIAMOND), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_RUNES.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_OLD_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(itemEnchantmentTransposer), 5120)
                .build());
        //endregion

        //region TomeItem Recipes - Tier 1
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{" Q ", "QQQ", " Q "})
                .midLayer(new String[]{"GBG", "B B", "GBG"})
                .lowLayer(new String[]{" Q ", "QQQ", " Q "})
                .blockMappings(ImmutableMap.of('Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.SUNFLOWER), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BOOK), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_INFERNAL_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_TOME_OF_SUNRISE.get(), 3), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{" Q ", "QQQ", " Q "})
                .midLayer(new String[]{"GBG", "B B", "GBG"})
                .lowLayer(new String[]{" Q ", "QQQ", " Q "})
                .blockMappings(ImmutableMap.of('Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "beds"))), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BOOK), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_INFERNAL_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_TOME_OF_WAKEFULNESS.get(), 3), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{" Q ", "QQQ", " Q "})
                .midLayer(new String[]{"GBG", "B B", "GBG"})
                .lowLayer(new String[]{" Q ", "QQQ", " Q "})
                .blockMappings(ImmutableMap.of('Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.ENDER_EYE), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BOOK), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_INFERNAL_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_TOME_OF_AMNESIA.get(), 3), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{" Q ", "QQQ", " Q "})
                .midLayer(new String[]{"GBG", "B B", "GBG"})
                .lowLayer(new String[]{" Q ", "QQQ", " Q "})
                .blockMappings(ImmutableMap.of('Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.PHANTOM_MEMBRANE), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BOOK), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_INFERNAL_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_TOME_OF_FLIGHT.get(), 3), 5120)
                .build());
        //endregion

        //region TomeItem Recipes - Tier 2
        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{" P ", "PPP", " P "})
                .midLayer(new String[]{"GBG", "B B", "GBG"})
                .lowLayer(new String[]{" P ", "PPP", " P "})
                .blockMappings(ImmutableMap.of('P', TomeRecipe.BlockMapping.of(Blocks.PURPUR_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.SUNFLOWER), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BOOK), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEnderAir), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENDER_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_ENHANCED_TOME_OF_SUNRISE.get(), 3), 5120)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{" P ", "PPP", " P "})
                .midLayer(new String[]{"GBG", "B B", "GBG"})
                .lowLayer(new String[]{" P ", "PPP", " P "})
                .blockMappings(ImmutableMap.of('P', TomeRecipe.BlockMapping.of(Blocks.PURPUR_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "beds"))), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BOOK), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEnderAir), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENDER_MANUSCRIPT.get()), 1)))
                .result(new ItemStack(Registry.ITEM_ENHANCED_TOME_OF_WAKEFULNESS.get(), 3), 5120)
                .build());
        //endregion

        //region TomeItem Recipes - Tier 3
        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"Q   Q", "     ", "     ", "     ", "Q   Q"})
                .withLayer(new String[]{"O   O", "  A  ", " AAA ", "  A  ", "O   O"})
                .withLayer(new String[]{"OBBBO", "BGAGB", "BA AB", "BGAGB", "OBBBO"})
                .withLayer(new String[]{"O   O", "  A  ", " AAA ", "  A  ", "O   O"})
                .withLayer(new String[]{"E   E", "     ", "     ", "     ", "E   E"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'E', TomeRecipe.BlockMapping.of(Blocks.ENCHANTING_TABLE),
                        'Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(Blocks.GLOWSTONE)/*TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))*/,
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves"))),
                        'O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENHANCED_TOME_OF_SUNRISE.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ANCIENT_MANUSCRIPT.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_NETHERITE_INGOT_STACK.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ETERNA_CRYSTALIS.get()), 1)))
                .result(new ItemStack(Registry.ITEM_ASCENDED_TOME_OF_SUNRISE.get()), 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"Q   Q", "     ", "     ", "     ", "Q   Q"})
                .withLayer(new String[]{"O   O", "  A  ", " AAA ", "  A  ", "O   O"})
                .withLayer(new String[]{"OBBBO", "BGAGB", "BA AB", "BGAGB", "OBBBO"})
                .withLayer(new String[]{"O   O", "  A  ", " AAA ", "  A  ", "O   O"})
                .withLayer(new String[]{"E   E", "     ", "     ", "     ", "E   E"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'E', TomeRecipe.BlockMapping.of(Blocks.ENCHANTING_TABLE),
                        'Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves"))),
                        'O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENHANCED_TOME_OF_WAKEFULNESS.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ANCIENT_MANUSCRIPT.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_NETHERITE_INGOT_STACK.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ETERNA_CRYSTALIS.get()), 1)))
                .result(new ItemStack(Registry.ITEM_ASCENDED_TOME_OF_WAKEFULNESS.get()), 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"Q   Q", "     ", "     ", "     ", "Q   Q"})
                .withLayer(new String[]{"O   O", "  A  ", " AAA ", "  A  ", "O   O"})
                .withLayer(new String[]{"OBBBO", "BGAGB", "BA AB", "BGAGB", "OBBBO"})
                .withLayer(new String[]{"O   O", "  A  ", " AAA ", "  A  ", "O   O"})
                .withLayer(new String[]{"E   E", "     ", "     ", "     ", "E   E"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'E', TomeRecipe.BlockMapping.of(Blocks.ENCHANTING_TABLE),
                        'Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves"))),
                        'O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_TOME_OF_FLIGHT.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ANCIENT_MANUSCRIPT.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_NETHERITE_INGOT_STACK.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ETERNA_CRYSTALIS.get()), 1)))
                .result(new ItemStack(Registry.ITEM_ASCENDED_TOME_OF_FLIGHT.get()), 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"Q   Q", "     ", "     ", "     ", "Q   Q"})
                .withLayer(new String[]{"O   O", "  A  ", " AAA ", "  A  ", "O   O"})
                .withLayer(new String[]{"OBBBO", "BGAGB", "BA AB", "BGAGB", "OBBBO"})
                .withLayer(new String[]{"O   O", "  A  ", " AAA ", "  A  ", "O   O"})
                .withLayer(new String[]{"E   E", "     ", "     ", "     ", "E   E"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'E', TomeRecipe.BlockMapping.of(Blocks.ENCHANTING_TABLE),
                        'Q', TomeRecipe.BlockMapping.of(Blocks.QUARTZ_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves"))),
                        'O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_TOME_OF_AMNESIA.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.AMETHYST_SHARD), 2),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ANCIENT_MANUSCRIPT.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_NETHERITE_INGOT_STACK.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ETERNA_CRYSTALIS.get()), 1)))
                .result(new ItemStack(Registry.ITEM_ASCENDED_TOME_OF_AMNESIA.get()), 10240)
                .build());
        //endregion

        //region Growth Scrolls - Tier 2
        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"DOAOD", "OAAAO", "AAAAA", "OAAAO", "DOAOD"})
                .withLayer(new String[]{"OBBBO", "B   B", "B   B", "B   B", "OBBBO"})
                .withLayer(new String[]{"ABDBA", "B   B", "D   D", "B   B", "ABDBA"})
                .withLayer(new String[]{"OBBBO", "B   B", "B   B", "B   B", "OBBBO"})
                .withLayer(new String[]{"DOAOD", "OAAAO", "AAAAA", "OAAAO", "DOAOD"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'D', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/diamond"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves"))),
                        'O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_INFERNAL_SCROLL_OF_GROWTH.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENDER_MANUSCRIPT.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENDER_FEATHER.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EXPERIENCE_BOTTLE), 2)))
                .result(new ItemStack(Registry.ITEM_ENDER_SCROLL_OF_GROWTH.get()), 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"EOAOE", "OAAAO", "AAAAA", "OAAAO", "EOAOE"})
                .withLayer(new String[]{"OBBBO", "B   B", "B   B", "B   B", "OBBBO"})
                .withLayer(new String[]{"ABNBA", "B   B", "N   N", "B   B", "ABNBA"})
                .withLayer(new String[]{"OBBBO", "B   B", "B   B", "B   B", "OBBBO"})
                .withLayer(new String[]{"EOAOE", "OAAAO", "AAAAA", "OAAAO", "EOAOE"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'E', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/emerald"))),
                        'N', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/netherite"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves"))),
                        'O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENDER_SCROLL_OF_GROWTH.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ANCIENT_MANUSCRIPT.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ANCIENT_FEATHER.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EXPERIENCE_BOTTLE), 2)))
                .result(new ItemStack(Registry.ITEM_ANCIENT_SCROLL_OF_GROWTH.get()), 10240)
                .build());
        //endregion

        //region Ancient Items
        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"     ", "  O  ", " OCO ", "  O  ", "     "})
                .withLayer(new String[]{"  O  ", "  S  ", "OSSSO", "  S  ", "  O  "})
                .withLayer(new String[]{" OCO ", "OSSSO", "CS SC", "OSSSO", " OCO "})
                .withLayer(new String[]{"  O  ", "  S  ", "OSSSO", "  S  ", "  O  "})
                .withLayer(new String[]{"     ", "  O  ", " OCO ", "  O  ", "     "})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'C', TomeRecipe.BlockMapping.of(Blocks.CRYING_OBSIDIAN),
                        'S', TomeRecipe.BlockMapping.of(Blocks.SCULK)))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_OLD_MANUSCRIPT.get()), 1), TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_INFERNAL_MANUSCRIPT.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENDER_MANUSCRIPT.get()), 1), TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_VOID_RUNES.get()), 1)))
                .result(new ItemStack(Registry.ITEM_ANCIENT_MANUSCRIPT.get()), 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"     ", "  O  ", " OCO ", "  O  ", "     "})
                .withLayer(new String[]{"  O  ", "  S  ", "OSSSO", "  S  ", "  O  "})
                .withLayer(new String[]{" OCO ", "OSSSO", "CS SC", "OSSSO", " OCO "})
                .withLayer(new String[]{"  O  ", "  S  ", "OSSSO", "  S  ", "  O  "})
                .withLayer(new String[]{"     ", "  O  ", " OCO ", "  O  ", "     "})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'C', TomeRecipe.BlockMapping.of(Blocks.CRYING_OBSIDIAN),
                        'S', TomeRecipe.BlockMapping.of(Blocks.SCULK)))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICKED_FEATHER.get()), 1), TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_INFERNAL_FEATHER.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ENDER_FEATHER.get()), 1), TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_VOID_RUNES.get()), 1)))
                .result(new ItemStack(Registry.ITEM_ANCIENT_FEATHER.get()), 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{"     ", "     ", "  O  ", "     ", "     "})
                .withLayer(new String[]{"  O  ", "     ", "O A O", "     ", "  O  "})
                .withLayer(new String[]{"  C  ", "  S  ", "CS SC", "  S  ", "  C  "})
                .withLayer(new String[]{"  O  ", "     ", "O A O", "     ", "  O  "})
                .withLayer(new String[]{"     ", "     ", "  O  ", "     ", "     "})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'C', TomeRecipe.BlockMapping.of(Blocks.CRYING_OBSIDIAN),
                        'S', TomeRecipe.BlockMapping.of(Blocks.SCULK)))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_MAGICAL_RUNES.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEnderAir), 3),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEnderiumShard), 1)))
                .result(new ItemStack(Registry.ITEM_MAGICAL_VOID_RUNES.get()), 10240)
                .build());
        //endregion

        //region Reinforcement Templates
        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{" O O ", "OO OO", "  A  ", "OO OO", " O O "})
                .withLayer(new String[]{"OA AO", "ADDDA", " DID ", "ADDDA", "OA AO"})
                .withLayer(new String[]{"  G  ", " DID ", "GI IG", " DID ", "  G  "})
                .withLayer(new String[]{"OA AO", "ADDDA", " DID ", "ADDDA", "OA AO"})
                .withLayer(new String[]{" O O ", "OO OO", "  A  ", "OO OO", " O O "})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron"))),
                        'D', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/diamond")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(itemArcaneEssence), 64),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemBlankRunestone), 4),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.EXPERIENCE_BOTTLE), 32),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_BLANK_SMITHING_TEMPLATE.get()), 2)))
                .result(new ItemStack(Registry.ITEM_MAGE_TEMPLATE.get()), 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{" O O ", "OO OO", "  A  ", "OO OO", " O O "})
                .withLayer(new String[]{"OA AO", "ADDDA", " DID ", "ADDDA", "OA AO"})
                .withLayer(new String[]{"  G  ", " DID ", "GI IG", " DID ", "  G  "})
                .withLayer(new String[]{"OA AO", "ADDDA", " DID ", "ADDDA", "OA AO"})
                .withLayer(new String[]{" O O ", "OO OO", "  A  ", "OO OO", " O O "})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron"))),
                        'D', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/diamond")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.ROTTEN_FLESH), 128),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.WITHER_SKELETON_SKULL), 4),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.BLAZE_POWDER), 32),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_BLANK_SMITHING_TEMPLATE.get()), 2)))
                .result(new ItemStack(Registry.ITEM_FIGHTER_TEMPLATE.get()), 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{" O O ", "OO OO", "  A  ", "OO OO", " O O "})
                .withLayer(new String[]{"OA AO", "ADDDA", " DID ", "ADDDA", "OA AO"})
                .withLayer(new String[]{"  G  ", " DID ", "GI IG", " DID ", "  G  "})
                .withLayer(new String[]{"OA AO", "ADDDA", " DID ", "ADDDA", "OA AO"})
                .withLayer(new String[]{" O O ", "OO OO", "  A  ", "OO OO", " O O "})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron"))),
                        'D', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/diamond")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), ResourceLocation.fromNamespaceAndPath("forge", "bones"))), 128),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.SKELETON_SKULL), 4),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.ARROW), 64),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_BLANK_SMITHING_TEMPLATE.get()), 2)))
                .result(new ItemStack(Registry.ITEM_RANGER_TEMPLATE.get()), 10240)
                .build());

        Registry.TOME_RECIPE_MANAGER.registerAdvancedRecipe(new AdvancedShapedTomeRecipe.Builder()
                .withLayer(new String[]{" O O ", "OO OO", "  A  ", "OO OO", " O O "})
                .withLayer(new String[]{"OA AO", "ADDDA", " DID ", "ADDDA", "OA AO"})
                .withLayer(new String[]{"  G  ", " DID ", "GI IG", " DID ", "  G  "})
                .withLayer(new String[]{"OA AO", "ADDDA", " DID ", "ADDDA", "OA AO"})
                .withLayer(new String[]{" O O ", "OO OO", "  A  ", "OO OO", " O O "})
                .blockMappings(ImmutableMap.of('O', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "obsidian"))),
                        'A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))),
                        'I', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/iron"))),
                        'D', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "storage_blocks/diamond")))))
                .itemMappings(ImmutableList.of(TomeRecipe.ItemMapping.of(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Items.NETHERITE_INGOT), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEnderiumUpgradeTemplate), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEnderiumIngot), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_ETHERIUM_UPGRADE_TEMPLATE.get()), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(itemEtheriumIngot), 1),
                        TomeRecipe.ItemMapping.of(Ingredient.of(Registry.ITEM_BLANK_SMITHING_TEMPLATE.get()), 2)))
                .result(new ItemStack(Registry.ITEM_ARMOR_REINFORCEMENT_TEMPLATE.get()), 10240)
                .build());
        //endregion

        Registry.TOME_RECIPE_MANAGER.registerRecipe(new ShapedTomeRecipe.Builder()
                .topLayer(new String[]{"AGA", "GOG", "AGA"})
                .midLayer(new String[]{"EBE", "BCB", "EBE"})
                .lowLayer(new String[]{"AGA", "GOG", "AGA"})
                .blockMappings(ImmutableMap.of('A', TomeRecipe.BlockMapping.of(Blocks.AMETHYST_BLOCK),
                        'E', TomeRecipe.BlockMapping.of(Blocks.ENCHANTING_TABLE),
                        'O', TomeRecipe.BlockMapping.of(Blocks.CRYING_OBSIDIAN),
                        'C', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "chests/wooden"))),
                        'B', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("forge", "bookshelves"))),
                        'G', TomeRecipe.BlockMapping.of(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone")))))
                .result(new ItemStack(Registry.ITEM_ESSENCE_OF_KNOWLEDGE.get()), 5120, false)
                .execute((player, position) -> {
                    ServerLevel level = player.serverLevel();
                    BlockEntity blockEntity = level.getBlockEntity(position);
                    float playerLuck = player.getLuck();
                    if (blockEntity instanceof ChestBlockEntity chestEntity) {
                        double essenceTally = 0.0;
                        ObjectArrayList<ItemStack> passedItems = new ObjectArrayList<>();

                        int size = chestEntity.getContainerSize();
                        for (int i = 0; i < size; i++) {
                            ItemStack slotItem = chestEntity.getItem(i);
                            if (slotItem.isEmpty()) {}
                            else if (slotItem.is(tagOccultItems)) {
                                if (slotItem.is(Items.AMETHYST_BLOCK)) { essenceTally += slotItem.getCount() * 0.334; }
                                else if (slotItem.is(tagMacguffinItems)) essenceTally += slotItem.getCount() * 8;
                                else { essenceTally += slotItem.getCount(); }
                                slotItem.setCount(0);
                            }
                            else { passedItems.add(slotItem); }
                        }
                        int essenceCount = (int)(essenceTally * (1.0F + (level.random.nextFloat() * playerLuck * 0.04F)));

                        //Handle passedItems
                        Vec3 center = position.getCenter();
                        if (!passedItems.isEmpty()) {
                            passedItems.forEach(item -> level.addFreshEntity(new ItemEntity(level, center.x, center.y, center.z, item)));
                        }

                        int essenceStacks = (int) Math.ceil(essenceCount / 64.0);
                        for (int i = 0; i < (essenceStacks - 1); i++) {
                            level.addFreshEntity(new ItemEntity(level, center.x, center.y, center.z, new ItemStack(Registry.ITEM_ESSENCE_OF_KNOWLEDGE.get(), 64)));
                        }
                        int essenceRemainder = essenceCount % 64;
                        if (essenceRemainder > 0) {
                            level.addFreshEntity(new ItemEntity(level, center.x, center.y, center.z, new ItemStack(Registry.ITEM_ESSENCE_OF_KNOWLEDGE.get(), essenceRemainder)));
                        }
                    }
                })
                .withHelp(List.of("recipe.help.knowledge_essence.1", "recipe.help.knowledge_essence.2", "recipe.help.knowledge_essence.3",
                        "recipe.help.knowledge_essence.4", "recipe.help.knowledge_essence.5", "recipe.help.knowledge_essence.6",
                        "recipe.help.knowledge_essence.7"))
                .build());
    }

    private static Item findItem(String namespace, String path) {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private static Block findBlock(String namespace, String path) {
        return ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
