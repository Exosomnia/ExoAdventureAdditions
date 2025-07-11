package com.exosomnia.exoadvadditions.events;

import com.exosomnia.exoadvadditions.Config;
import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoadvadditions.items.AttributeTemplateItem;
import com.exosomnia.exoadvadditions.managers.AdventureManager;
import com.exosomnia.exoadvadditions.mixins.CombatTrackerMixin;
import com.exosomnia.exoadvadditions.utils.ExoPotionUtils;
import com.exosomnia.exoadvadditions.utils.ItemUtils;
import com.exosomnia.exoarmory.ExoArmory;
import com.exosomnia.exolib.capabilities.persistentplayerdata.IPersistentPlayerDataStorage;
import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataProvider;
import com.exosomnia.exoskills.mixin.interfaces.IMobEffectInstanceMixin;
import com.exosomnia.exoskills.mixin.mixins.MobEffectInstanceAccessor;
import com.exosomnia.exostats.ExoStats;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import it.unimi.dsi.fastutil.Pair;
import net.blay09.mods.waystones.api.WaystoneTeleportEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModdedEventTweaks {

    static final TagKey<DamageType> IS_EXPLOSION_TYPE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("minecraft", "is_explosion"));

    private static Holder<Biome> BIOME_SOUL_DROP;
    private static Item ITEM_UNDEAD_SOUL;
    private static Item ITEM_MAGICAL_EYE;
    private static Item ITEM_TWISTED_MIRROR;

    private static ImmutableSet<EntityType<?>> invalidEntities = ImmutableSet.of();

    private static EntityType<?> ENTITY_WEX;

    private static final HashSet<ResourceLocation> MONSTER_TABLES = new HashSet<>();

    private static final ImmutableList<ImmutableList<Item>> validMeleeWeapons;
    private static ImmutableList<ItemStack> validOffhandItems;
    private static ImmutableMap<Item, Consumer<ItemAttributeModifierEvent>> attributeMods = ImmutableMap.of();
    static {
        validMeleeWeapons = ImmutableList.of(
                ImmutableList.of(Items.WOODEN_SWORD, Items.WOODEN_SHOVEL, Items.WOODEN_AXE,
                        Items.STONE_SWORD, Items.STONE_SHOVEL, Items.STONE_AXE),
                ImmutableList.of(Items.WOODEN_SWORD, Items.WOODEN_AXE,
                        Items.STONE_SWORD, Items.STONE_AXE,
                        Items.IRON_SWORD, Items.IRON_SHOVEL, Items.IRON_AXE)
        );
    }

    public static void initalizeTweaks() {
        for (EntityType type : ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (type.getCategory().equals(MobCategory.MONSTER)) {
                MONSTER_TABLES.add(type.getDefaultLootTable());
            }
        }
    }

    public static void setupAboutToStartTweaks() {
        ITEM_UNDEAD_SOUL = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("endrem", "undead_soul"));
        ITEM_MAGICAL_EYE = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("endrem", "magical_eye"));
        ITEM_TWISTED_MIRROR = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("enigmaticlegacy", "twisted_mirror"));

        ENTITY_WEX = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("bygonenether", "wex"));

        if (!Config.cowboyModeFixes) {
            invalidEntities = ImmutableSet.of(
                    Registry.ENTITY_COLELYTRA, Registry.ENTITY_MOTH, Registry.ENTITY_DRAGON,
                    Registry.ENTITY_FIREBIRD, Registry.ENTITY_GRIFFON, Registry.ENTITY_NETHER_BAT,
                    Registry.ENTITY_NUDIBRANCH
            );
        }

        validOffhandItems = ImmutableList.of(
                new ItemStack(Items.SHIELD), new ItemStack(ExoArmory.REGISTRY.ITEM_COPPER_SHIELD.get()),
                new ItemStack(ExoArmory.REGISTRY.ITEM_IRON_SHIELD.get()), new ItemStack(Items.ENDER_PEARL, 3),
                new ItemStack(Items.FISHING_ROD)
        );

        if (!Config.cowboyModeFixes) {
            attributeMods = ImmutableMap.of(
                    Registry.ITEM_KEEPER_FLAMBERGE, ((event) -> {
                        if (!event.getSlotType().equals(EquipmentSlot.MAINHAND)) return;
                        event.removeAttribute(Attributes.ATTACK_SPEED);
                        event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(ItemUtils.UUID_ATTACK_SPEED, "Nerf", -2.8, AttributeModifier.Operation.ADDITION));
                    }),
                    Registry.ITEM_SPELLBREAKER, ((event) -> {
                        if (!event.getSlotType().equals(EquipmentSlot.MAINHAND)) return;
                        event.removeAttribute(Attributes.ATTACK_SPEED);
                        event.removeAttribute(Attributes.ATTACK_DAMAGE);
                        event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(ItemUtils.UUID_ATTACK_SPEED, "Nerf", -2.4, AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(ItemUtils.UUID_ATTACK_DAMAGE, "Buff", 10.0, AttributeModifier.Operation.ADDITION));
                    }),
                    Registry.ITEM_AMETHYST_RAPIER, ((event) -> {
                        if (!event.getSlotType().equals(EquipmentSlot.MAINHAND)) return;
                        event.removeAttribute(Attributes.ATTACK_SPEED);
                        event.removeAttribute(Attributes.ATTACK_DAMAGE);
                        event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(ItemUtils.UUID_ATTACK_SPEED, "Nerf", -2.2, AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(ItemUtils.UUID_ATTACK_DAMAGE, "Nerf_2", 5.0, AttributeModifier.Operation.ADDITION));
                    }),
                    Registry.ITEM_THUNDERCALLER, ((event) -> {
                        if (!event.getSlotType().equals(EquipmentSlot.MAINHAND)) return;
                        event.removeAttribute(Attributes.ATTACK_SPEED);
                        event.removeAttribute(Attributes.ATTACK_DAMAGE);
                        event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(ItemUtils.UUID_ATTACK_SPEED, "Nerf", -2.2, AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(ItemUtils.UUID_ATTACK_DAMAGE, "Nerf_2", 5.0, AttributeModifier.Operation.ADDITION));
                    }),
                    Registry.ITEM_ETHERIUM_CHESTPLATE, ((event) -> {
                        if (!event.getSlotType().equals(EquipmentSlot.CHEST)) return;
                        event.removeAttribute(Attributes.ARMOR);
                        event.addModifier(Attributes.ARMOR, new AttributeModifier(ItemUtils.UUID_CHEST_ARMOR, "Fix", 9.0, AttributeModifier.Operation.ADDITION));
                    }),
                    Registry.ITEM_ETHERIUM_LEGGINGS, ((event) -> {
                        if (!event.getSlotType().equals(EquipmentSlot.LEGS)) return;
                        event.removeAttribute(Attributes.ARMOR);
                        event.addModifier(Attributes.ARMOR, new AttributeModifier(ItemUtils.UUID_LEGS_ARMOR, "Fix", 7.0, AttributeModifier.Operation.ADDITION));
                    })
            );
        }
    }

    public static void setupStartedTweaks(Level level) {
        BIOME_SOUL_DROP = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.SOUL_SAND_VALLEY);
    }

    /*
        The Depths spawn rule tweaks, makes creepers have a chance to breach
        and zombies a chance to be a miner (This has evolved way beyond this now)
    */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void eventEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        Level level = event.getLevel();
        if (level.isClientSide || (!(entity instanceof Mob living))) { return; }

        EntityType livingType = living.getType();
        CompoundTag livingData = living.getPersistentData();
        if (invalidEntities.contains(livingType) && !livingData.getBoolean("validSpawn")) {
            event.setCanceled(true);
            return;
        }

        if (!livingType.getCategory().equals(MobCategory.MONSTER) || livingData.getBoolean("SPAWN_BONUSES")) return;
        if (living.hasEffect(Registry.RAISE_DEAD_TIMER)) {
            return;
        }

        int stageOrdinal = !Config.cowboyModeFixes ? GameStageHelper.getGlobalGameStage().getOrdinal() : 0;
        boolean isCreeper = living instanceof Creeper;

        //Attributes for all mobs
        AttributeInstance maxHealthAttr = living.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance speedAttr = living.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockbackResistAttr = living.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        maxHealthAttr.addPermanentModifier(new AttributeModifier("spawn_stage_bonus", stageOrdinal * 0.125, AttributeModifier.Operation.MULTIPLY_BASE));
        speedAttr.addPermanentModifier(new AttributeModifier("spawn_stage_bonus", stageOrdinal * 0.025, AttributeModifier.Operation.MULTIPLY_BASE));
        knockbackResistAttr.addPermanentModifier(new AttributeModifier("spawn_stage_bonus", stageOrdinal * 0.05, AttributeModifier.Operation.ADDITION));

        //Equip mobs with gear
        RandomSource random = level.random;
        if (stageOrdinal > 1) {
            int bonusOrdinal = stageOrdinal - 2;
            double effectiveRandom = 0.20 + (bonusOrdinal * (7.0/30.0));
            if (entity instanceof Zombie zombie) {
                if (random.nextDouble() < effectiveRandom) {
                    ItemStack weapon = zombie.getMainHandItem();
                    if (weapon.isEmpty()) {
                        List<Item> validWeapons = switch (stageOrdinal) {
                            case 2, 3 -> validMeleeWeapons.get(0);
                            default -> validMeleeWeapons.get(1);
                        };
                        weapon = new ItemStack(validWeapons.get(random.nextInt(validWeapons.size())));
                        zombie.setItemInHand(InteractionHand.MAIN_HAND, weapon);
                        zombie.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                    }
                    if (!weapon.isEnchanted()) {
                        EnchantmentHelper.enchantItem(random, weapon, (int)(10 + (effectiveRandom * random.nextInt(15))), false);
                    }
                }
                if (random.nextDouble() < effectiveRandom) {
                    ItemStack offhand = zombie.getOffhandItem();
                    if (offhand.isEmpty()) {
                        zombie.setItemInHand(InteractionHand.OFF_HAND, validOffhandItems.get(random.nextInt(validOffhandItems.size())).copy());
                        zombie.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
                    }
                }
            }
            if (entity instanceof Skeleton skeleton) {
                if (random.nextDouble() < effectiveRandom) {
                    ItemStack weapon = skeleton.getMainHandItem();
                    if (!weapon.isEnchanted()) {
                        EnchantmentHelper.enchantItem(random, weapon, (int)(10 + (effectiveRandom * random.nextInt(15))), false);
                    }
                }
                if (random.nextDouble() < effectiveRandom) {
                    ItemStack offhand = skeleton.getOffhandItem();
                    if (offhand.isEmpty()) {
                        List<MobEffectInstance> newEffects = switch (event.getLevel().getRandom().nextInt(11 + bonusOrdinal)) {
                            case 0, 1, 2, 3 -> List.of(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_FROSTED.get(), 200, 0));
                            case 4, 5, 6, 7 -> List.of(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                            case 8, 9 -> List.of(new MobEffectInstance(MobEffects.HARM, 1, 1));
                            case 10, 11 -> List.of(new MobEffectInstance(MobEffects.WITHER, 160, 2));
                            default -> List.of(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_FROSTED.get(), 200, 0),
                                    new MobEffectInstance(MobEffects.WEAKNESS, 200, 0),
                                    new MobEffectInstance(MobEffects.HARM, 1, 1),
                                    new MobEffectInstance(MobEffects.WITHER, 160, 2));
                        };
                        skeleton.setItemInHand(InteractionHand.OFF_HAND, ExoPotionUtils.setColor(PotionUtils.setCustomEffects(new ItemStack(Items.TIPPED_ARROW), newEffects), 0x2b0500));
                        skeleton.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
                    }
                }
            }
            if (!isCreeper) {
                if (random.nextDouble() < (effectiveRandom * 0.5)) {
                    living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, Math.max(0, random.nextInt(3) - 1)));
                }
                if (random.nextDouble() < (effectiveRandom * 0.5)) {
                    MobEffectInstance regenEffect = new MobEffectInstance(MobEffects.REGENERATION, -1, Math.max(1, random.nextInt(3)));
                    ((IMobEffectInstanceMixin)regenEffect).setForced(true);
                    living.addEffect(regenEffect);
                }
            }
            else if (random.nextDouble() < (effectiveRandom * 0.5)) {
                AttributeInstance armorAttr = living.getAttribute(Attributes.ARMOR);
                armorAttr.addPermanentModifier(new AttributeModifier("spawn_stage_bonus", random.nextInt(8, 11), AttributeModifier.Operation.ADDITION));
            }
        }

        living.setHealth((float) maxHealthAttr.getValue());
        if (level.dimension().equals(Registry.DEPTHS_DIMENSION)) {
            //Maybe move to like a map lookup? Actually, IDC
            if (isCreeper) {
                if (random.nextInt(4) == 0) {
                    living.getPersistentData().putBoolean("enhancedai:breach", true);
                    AttributeInstance explosionAttr = living.getAttribute(ExoArmory.REGISTRY.ATTRIBUTE_EXPLOSION_STRENGTH.get());
                    explosionAttr.addPermanentModifier(new AttributeModifier("depths_bonus", 0.10, AttributeModifier.Operation.MULTIPLY_BASE));
                }
            } else if (entity instanceof Zombie) {
                if (random.nextInt(4) == 0) {
                    living.getPersistentData().putBoolean("enhancedai:miner", true);
                }
            } else if (entity instanceof Skeleton skeleton) {
                if (random.nextInt(4) == 0) {
                    ItemStack weapon = skeleton.getMainHandItem();
                    if (weapon.getItem() instanceof BowItem) {
                        Map<Enchantment, Integer> currentEnchants = weapon.getAllEnchantments();
                        currentEnchants.put(Enchantments.PUNCH_ARROWS, random.nextInt(1, 4));
                        EnchantmentHelper.setEnchantments(currentEnchants, weapon);

                        MobEffectInstance newEffect = switch (random.nextInt(3)) {
                            case 0 -> new MobEffectInstance(MobEffects.BLINDNESS, 240, 0);
                            case 1 -> new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_VULNERABLE.get(), 240, 1);
                            default -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 240, 1);
                        };
                        skeleton.setItemInHand(InteractionHand.OFF_HAND, ExoPotionUtils.setColor(PotionUtils.setCustomEffects(new ItemStack(Items.TIPPED_ARROW), List.of(newEffect)), 0x2b0500));
                        skeleton.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
                    }
                }
            } else if (entity instanceof Spider) {
                if (random.nextInt(4) == 0) {
                    living.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0));
                }
            } else if (entity instanceof EnderMan) {
                if (random.nextInt(4) == 0) {
                    living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0));
                }
            }
        }
        livingData.putBoolean("SPAWN_BONUSES", true);

        MobSpawnType spawnType = living.getSpawnType();
        if (spawnType != null && (spawnType.equals(MobSpawnType.SPAWNER) || spawnType.equals(MobSpawnType.STRUCTURE))) {
            living.setCanPickUpLoot(false);
        }
    }

    @SubscribeEvent
    public static void depthsBonusExperience(LivingExperienceDropEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide || !level.dimension().equals(Registry.DEPTHS_DIMENSION)) { return; }

        event.setDroppedExperience((int)(event.getOriginalExperience() * 1.25));
    }

    /*Needed to prevent MajruszsProgressiveDifficulty from allowing op item drops from spawners.*/
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void eventDropFixAndXP(LivingDeathEvent event) {
        LivingEntity defender = event.getEntity();
        DamageSource source = event.getSource();
        if (defender.level().isClientSide) { return; }

        if (defender instanceof Mob mob) {
            MobSpawnType spawnType = mob.getSpawnType();
            if (!mob.canPickUpLoot() && spawnType != null && (spawnType.equals(MobSpawnType.SPAWNER) || spawnType.equals(MobSpawnType.STRUCTURE))) {
                mob.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                mob.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
                mob.setDropChance(EquipmentSlot.HEAD, 0.0F);
                mob.setDropChance(EquipmentSlot.CHEST, 0.0F);
                mob.setDropChance(EquipmentSlot.LEGS, 0.0F);
                mob.setDropChance(EquipmentSlot.FEET, 0.0F);
            }

            float maxHealth = defender.getMaxHealth();
            int experience = defender.getExperienceReward();
            float stageOrdinal = !Config.cowboyModeFixes ? GameStageHelper.getGlobalGameStage().getOrdinal() * 0.025F : 0.0F;
            float monsterMod = mob.getType().getCategory().equals(MobCategory.MONSTER) ? 1.0F : 0.333F;
            float spawnerMod = (spawnType == null || !spawnType.equals(MobSpawnType.SPAWNER)) ? 1.0F : 0.5F;
            float zombiePiglinMod = !(defender instanceof ZombifiedPiglin) ? 1.0F : 0.5F;

            int reward = (int)((((experience * 20.0) + (maxHealth * 62.5)) * (1.0 + stageOrdinal) * monsterMod) * spawnerMod * zombiePiglinMod) ;

            Set<Player> playerAssists = new HashSet<>();
            if (source.getEntity() instanceof ServerPlayer attacker) {
                if (source.is(DamageTypes.PLAYER_ATTACK) && attacker.getMainHandItem().is(Registry.AQUATIC_WEAPONS) && attacker.isInWaterOrRain()) {
                    int fishReward = (int)(reward * 0.25);
                    attacker.awardStat(ExoStats.COMBAT_SCORE.get(), reward - fishReward);
                    attacker.awardStat(ExoStats.FISHING_SCORE.get(), fishReward);
                }
                else {
                    attacker.awardStat(ExoStats.COMBAT_SCORE.get(), reward);
                }
                playerAssists.add(attacker);
            }

            //Award partial XP to assists
            List<CombatEntry> combatEntries = ((CombatTrackerMixin)defender.getCombatTracker()).getEntries();

            int historyAmount = Math.min(20, combatEntries.size());
            for (int i = 0; i < historyAmount; i++) {
                CombatEntry entry = combatEntries.get(i);
                Entity assistEntity = entry.source().getEntity();
                if (assistEntity instanceof Player assistPlayer && !playerAssists.contains(assistPlayer)) {
                    playerAssists.add(assistPlayer);
                    assistPlayer.awardStat(ExoStats.COMBAT_SCORE.get(), (int)(reward * 0.333));
                }
            }
        }
    }

    /*
        Gives players blighted while in the depths.
    */
    @SubscribeEvent
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level();
        boolean serverSide = event.side.isServer();
        if (serverSide && !level.getGameRules().getRule(AdventureManager.RULE_CAN_HUNGER_DRAIN).get()) {
            event.player.getFoodData().setExhaustion(0);
        }

        if (level.getGameTime() % 20 != 0) {
            return;
        }
        if (player.isPassenger()) {
            Entity vehicle = player.getVehicle();
            if (Registry.FLYING_MOUNT_CLASS.isInstance(vehicle)) {
                LivingEntity mounted = (LivingEntity)vehicle;
                boolean canFly = mounted.hasEffect(Registry.EFFECT_FLIGHT_READY.get());
                try { Registry.FLYING_ALLOWED_FIELD.set(Registry.FLYING_MOUNT_CLASS.cast(mounted), canFly); }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (serverSide && level.dimension().equals(Registry.DEPTHS_DIMENSION)) {
            player.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_BLIGHTED.get(), 60, 0, true, true));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tableLoadEvent(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        if (MONSTER_TABLES.contains(name)) {
            event.getTable().addPool(LootPool.lootPool().when(LootItemKilledByPlayerCondition.killedByPlayer().and(LootItemRandomChanceCondition.randomChance(0.001F))).setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Registry.ITEM_MACGUFFIN_13.get())).build());
        }
    }

    @SubscribeEvent
    public static void lightningStrikeEvent(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Inventory inventory = player.getInventory();
            int size = inventory.getContainerSize();
            for (var i = 0; i < size; i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.is(Registry.ITEM_CIRCUIT_BOARD.get())) {
                    ItemStack newStack = new ItemStack(Registry.ITEM_CHARGED_CIRCUIT_BOARD.get(), stack.getCount());
                    if (stack.hasTag()) { newStack.setTag(stack.getTag().copy()); }
                    inventory.setItem(i, newStack);
                }
            }
        }
    }

    @SubscribeEvent
    public static void dropsEvent(LivingDropsEvent event) {
        Entity attacker = event.getSource().getDirectEntity();
        if (!(attacker instanceof ServerPlayer player) || !event.isRecentlyHit()) { return; }

        LivingEntity defender = event.getEntity();
        Vec3 origin = defender.position();
        ServerLevel level = (ServerLevel)player.level();
        RandomSource random = level.getRandom();

        if (level.dimension().equals(Level.NETHER)) {
            if (defender.getMobType().equals(MobType.UNDEAD) && random.nextInt(level.getBiomeManager().getBiome(defender.blockPosition()).equals(BIOME_SOUL_DROP) ? 200 : 400) == 0) {
                level.addFreshEntity(new ItemEntity(level, origin.x, origin.y, origin.z, new ItemStack(ITEM_UNDEAD_SOUL)));
            }
            else if (defender.getTags().contains("in.sanctum_guardian")) {
                level.addFreshEntity(new ItemEntity(level, origin.x, origin.y, origin.z, new ItemStack(ITEM_MAGICAL_EYE)));
            }
        }
        else if (defender instanceof WitherBoss && level.dimension().equals(Registry.BEGINNING_DIMENSION)) {
            level.addFreshEntity(new ItemEntity(level, origin.x, origin.y, origin.z, new ItemStack(Registry.ITEM_MACGUFFIN_ULTIMATE.get())));
        }
    }

    @SubscribeEvent
    public static void takeDamageEvent(LivingHurtEvent event) {
        LivingEntity defender = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        float isExplosionMod = source.is(IS_EXPLOSION_TYPE) ? 0.5F : 1.0F;
        float damageAmount = event.getAmount();
        float firstFullDamage = Math.min(damageAmount, 20.0F);
        float remainderDamage = damageAmount - firstFullDamage;
        int stageOrdinal = !Config.cowboyModeFixes ? GameStageHelper.getGlobalGameStage().getOrdinal() : 0;

        if (defender instanceof ServerPlayer playerDefender) {
            if (attacker != null && event.getAmount() > 0) {
                double damageMod = ((0.125F * stageOrdinal * isExplosionMod) + 1.0F);
                float totalModDamage = (float)(((firstFullDamage * damageMod) + (remainderDamage * (damageMod / 2))) * isExplosionMod);
                event.setAmount(totalModDamage);
                if (playerDefender.getAbilities().flying) {
                    playerDefender.getAbilities().flying = false;
                    playerDefender.onUpdateAbilities();
                }
                defender.addEffect(new MobEffectInstance(Registry.EFFECT_GROUNDED.get(), 400, 0));
            }

            if (attacker instanceof Mob attackerMob) {
                if (stageOrdinal >= 3 && attackerMob.getMobType().equals(MobType.UNDEAD) && !source.isIndirect()) {
                    //Fatigued Effect Chance - Duration grows on hit
                    if (defender.getRandom().nextInt(8) < 1 + stageOrdinal) {
                        MobEffectInstance existingFatigue = defender.getEffect(Registry.EFFECT_FATIGUED.get());
                        if (existingFatigue != null) {
                            defender.addEffect(new MobEffectInstance(Registry.EFFECT_FATIGUED.get(), existingFatigue.getDuration() + 200, 0));
                        }
                        defender.addEffect(new MobEffectInstance(Registry.EFFECT_FATIGUED.get(), 200, 0));
                    }

                    //Blighted Effect Chance - Amplifier grows on hit
                    if (defender.getRandom().nextInt(8) < 1 + stageOrdinal) {
                        MobEffectInstance existingBlight = defender.getEffect(ExoArmory.REGISTRY.EFFECT_BLIGHTED.get());
                        if (existingBlight != null) {
                            defender.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_BLIGHTED.get(), 200, existingBlight.getAmplifier() + 1));
                        }
                        defender.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_BLIGHTED.get(), 200, 0));
                    }
                }

                else if (stageOrdinal >= 4 && attacker instanceof EnderMan) {
                    MobEffectInstance existingVulnerable = defender.getEffect(ExoArmory.REGISTRY.EFFECT_VULNERABLE.get());
                    if (existingVulnerable == null) {
                        defender.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_VULNERABLE.get(), 200, 0));
                    }
                    else if (defender.getRandom().nextDouble() < 0.20) {
                        defender.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_VULNERABLE.get(), 200, existingVulnerable.getAmplifier() + 1));
                    }
                }
            }

            if (attacker != null && ForgeRegistries.ENTITY_TYPES.getKey(attacker.getType()).getNamespace().equals("cataclysm")) {
                event.setAmount(event.getAmount() * 1.25F);
            }
        }

        if (attacker instanceof ServerPlayer playerAttacker && source.is(Registry.GUN_DAMAGE)) {
            double rangedBonus = 0.1;

            Optional<IPersistentPlayerDataStorage> optionalPlayerDataStorage = playerAttacker.getCapability(PersistentPlayerDataProvider.PLAYER_DATA).resolve();
            if (optionalPlayerDataStorage.isPresent()) {
                IPersistentPlayerDataStorage playerData = optionalPlayerDataStorage.get();
                CompoundTag tag = playerData.get();

                CompoundTag modTag = tag.getCompound("exoadventure");
                if (modTag.getBoolean("ballistics")) {
                    rangedBonus += 0.1;
                }
            }

            rangedBonus = 1.0 + (rangedBonus * (playerAttacker.getAttributeValue(ExoArmory.REGISTRY.ATTRIBUTE_RANGED_STRENGTH.get()) - 1.0));

            event.setAmount((float)((event.getAmount() * rangedBonus) * playerAttacker.getAttributeValue(Registry.ATTRIBUTE_BALLISTICS_DAMAGE.get())));
        }
    }

    @SubscribeEvent
    public static void tenacityEffect(MobEffectEvent.Added event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        MobEffect effect = effectInstance.getEffect();
        LivingEntity effectEntity = event.getEntity();
        int duration = effectInstance.getDuration();

        if (Registry.SUMMON_EFFECTS.contains(effect)) {
            ((MobEffectInstanceAccessor)effectInstance).setDuration((int)(duration * 0.5));
        }

        if (!(effectEntity instanceof ServerPlayer effectPlayer)) return;

        if (effect.equals(Registry.OAKSKIN)) {
            if (effectPlayer.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                effectPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, effectInstance.getDuration(), effectPlayer.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1));
            }
            else {
                effectPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, effectInstance.getDuration(), 0));
            }
        }

        effectPlayer.getCapability(PersistentPlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
            CompoundTag tag = playerData.get();
            CompoundTag modTag = tag.getCompound("exoadventure");

            int tenacity = modTag.getInt("tenacity");
            if (tenacity > 0) {
                if (duration != -1 && effectInstance.getEffect().getCategory().equals(MobEffectCategory.HARMFUL)) {
                    ((MobEffectInstanceAccessor)effectInstance).setDuration((int)Math.round(duration * (1.0 - (tenacity * 0.05))));
                }
            }
        });
    }

    @SubscribeEvent
    public static void flightReadyFinished(MobEffectEvent.Expired event) {
        LivingEntity expiree = event.getEntity();
        MobEffectInstance effect = event.getEffectInstance();
        if (effect != null && effect.getEffect().equals(Registry.EFFECT_FLIGHT_READY.get()) && Registry.FLYING_MOUNT_CLASS.isInstance(expiree)) {
            expiree.addEffect(new MobEffectInstance(Registry.EFFECT_GROUNDED.get(), 1800, 0));
        }
    }

    @SubscribeEvent
    public static void flightReadyRemove(MobEffectEvent.Remove event) {
        LivingEntity expiree = event.getEntity();
        if (event.getEffect().equals(Registry.EFFECT_FLIGHT_READY.get()) && Registry.FLYING_MOUNT_CLASS.isInstance(expiree)) {
            expiree.addEffect(new MobEffectInstance(Registry.EFFECT_GROUNDED.get(), 1800, 0));
        }
    }

    @SubscribeEvent
    public static void mirrorUseEvent(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player && event.getItem().is(ITEM_TWISTED_MIRROR)) {
            player.getCooldowns().addCooldown(ITEM_TWISTED_MIRROR, 7200);
        }
    }

    @SubscribeEvent
    public static void attributeItemModifyEvent(ItemAttributeModifierEvent event) {
        Consumer<ItemAttributeModifierEvent> mod = attributeMods.get(event.getItemStack().getItem());
        if (mod != null) mod.accept(event);

        ItemStack item = event.getItemStack();
        CompoundTag itemTag = item.getTag();
        if (itemTag == null) return;

        String upgradeTemplate = itemTag.getString("AttributeTemplate");
        if (upgradeTemplate.isEmpty()) return;

        AttributeTemplateItem templateItem = Registry.ATTRIBUTE_TEMPLATES.get(upgradeTemplate);
        if (templateItem == null) return;

        for (Pair<Attribute, AttributeModifier> attributeMod : templateItem.getModifiers()) {
            Multimap<Attribute, AttributeModifier> originalMods = event.getOriginalModifiers();
            Attribute templateAttribute = attributeMod.left();
            AttributeModifier templateMod = attributeMod.right();
            if (originalMods.containsKey(templateAttribute)) {
                Optional<AttributeModifier> firstMod = originalMods.get(templateAttribute).stream().filter(attributeModifier -> attributeModifier.getOperation().equals(templateMod.getOperation())).findFirst();
                if (firstMod.isPresent()) {
                    AttributeModifier targetMod = firstMod.get();
                    event.removeModifier(templateAttribute, targetMod);
                    event.addModifier(templateAttribute, new AttributeModifier(targetMod.getId(),
                            targetMod.getName(), targetMod.getAmount() + templateMod.getAmount(), targetMod.getOperation()));
                    return;
                }
            }
            originalMods.put(templateAttribute, templateMod);
        }
    }

    @SubscribeEvent
    public static void spawnerWexes(BlockEvent.BreakEvent event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (!event.getState().is(Blocks.SPAWNER) || !(levelAccessor instanceof ServerLevel level) || event.getPlayer().getAbilities().instabuild) { return; }
        int spawns = level.getDifficulty().getId();
        Vec3 origin = event.getPos().getCenter();
        for(var i = 0; i < spawns; i++) {
            Entity wex = ENTITY_WEX.create(level);
            wex.setPos(origin);
            level.addFreshEntity(wex);
        }
    }

    @SubscribeEvent
    public static void reduceWaystoneXP(WaystoneTeleportEvent.Pre event) {
        Entity teleporter = event.getContext().getEntity();
        if (!(teleporter instanceof Player player) || !player.getTags().contains("WaystoneReduction")) return;

        event.setXpCost(Math.max(event.getXpCost() - 1, 0));
    }
}
