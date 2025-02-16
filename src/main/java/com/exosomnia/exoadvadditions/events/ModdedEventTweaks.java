package com.exosomnia.exoadvadditions.events;

import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoadvadditions.utils.ItemUtils;
import com.exosomnia.exoarmory.ExoArmory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
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
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModdedEventTweaks {

    private record AdditionalDrop(ItemLike item, int minDrop, int maxDrop) {}

    //private static Structure MAGICAL_EYE_STRUCT;
    private static Holder<Biome> BIOME_SOUL_DROP;
    private static Item ITEM_UNDEAD_SOUL;
    private static Item ITEM_MAGICAL_EYE;
    private static Item ITEM_EVIL_EYE;
    private static Item ITEM_EVIL_ESSENCE;
    private static Item ITEM_TWISTED_MIRROR;

    private static Item ITEM_SPELLBREAKER;
    private static Item ITEM_AMETHYST_RAPIER;
    private static Item ITEM_KEEPER_FLAMBERGE;

    private static ImmutableMap<EntityType<? extends Entity>, AdditionalDrop> additionalDrops;
    private static ImmutableSet<EntityType<?>> invalidEntities;

    private static EntityType<?> ENTITY_ENDER_GUARDIAN;
    private static EntityType<?> ENTITY_THE_LEVIATHAN;
    private static EntityType<?> ENTITY_ANCIENT_REMNANT;
    private static EntityType<?> ENTITY_DEAD_KING;

    private static HashSet<ResourceLocation> MONSTER_TABLES = new HashSet<>();

    public static void initalizeTweaks() {
        for (EntityType type : ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (type.getCategory().equals(MobCategory.MONSTER)) {
                MONSTER_TABLES.add(type.getDefaultLootTable());
            }
        }
    }

    public static void setupTweaks(Level level) {
        //MAGICAL_EYE_STRUCT = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(new ResourceLocation("incendium", "sanctum"));
        BIOME_SOUL_DROP = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.SOUL_SAND_VALLEY);
        ITEM_UNDEAD_SOUL = ForgeRegistries.ITEMS.getValue(new ResourceLocation("endrem", "undead_soul"));
        ITEM_MAGICAL_EYE = ForgeRegistries.ITEMS.getValue(new ResourceLocation("endrem", "magical_eye"));
        ITEM_EVIL_EYE = ForgeRegistries.ITEMS.getValue(new ResourceLocation("endrem", "evil_eye"));
        ITEM_EVIL_ESSENCE = ForgeRegistries.ITEMS.getValue(new ResourceLocation("enigmaticlegacy", "evil_essence"));
        ITEM_TWISTED_MIRROR = ForgeRegistries.ITEMS.getValue(new ResourceLocation("enigmaticlegacy", "twisted_mirror"));

        ITEM_SPELLBREAKER = ForgeRegistries.ITEMS.getValue(new ResourceLocation("irons_spellbooks", "spellbreaker"));
        ITEM_AMETHYST_RAPIER = ForgeRegistries.ITEMS.getValue(new ResourceLocation("irons_spellbooks", "amethyst_rapier"));
        ITEM_KEEPER_FLAMBERGE = ForgeRegistries.ITEMS.getValue(new ResourceLocation("irons_spellbooks", "keeper_flamberge"));

        ENTITY_ENDER_GUARDIAN = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("cataclysm", "ender_guardian"));
        ENTITY_THE_LEVIATHAN = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("cataclysm", "the_leviathan"));
        ENTITY_ANCIENT_REMNANT = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("cataclysm", "ancient_remnant"));
        ENTITY_DEAD_KING = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("irons_spellbooks", "dead_king"));

        additionalDrops = ImmutableMap.of(
                EntityType.ELDER_GUARDIAN, new AdditionalDrop(Registry.ITEM_ELDER_GUARDIAN_EYE.get(), 1, 1),
                EntityType.WITHER, new AdditionalDrop(ITEM_EVIL_ESSENCE, 1, 4),
                ENTITY_ENDER_GUARDIAN, new AdditionalDrop(Registry.ITEM_VOID_ORB.get(), 1, 1)
                //ENTITY_THE_LEVIATHAN, new AdditionalDrop(Registry.ITEM_OMINOUS_BAR.get(), 1, 1),
                //ENTITY_ANCIENT_REMNANT, new AdditionalDrop(Registry.ITEM_GILDED_ANCIENT_TOOTH.get(), 1, 1),
                //ENTITY_DEAD_KING, new AdditionalDrop(Registry.ITEM_DEAD_KINGS_GEM.get(), 1, 1)
        );

        invalidEntities = ImmutableSet.of(
                //Registry.ENTITY_COLELYTRA, Registry.ENTITY_MOTH, Registry.ENTITY_DRAGON,
                //Registry.ENTITY_FIREBIRD, Registry.ENTITY_GRIFFON, Registry.ENTITY_NETHER_BAT,
                Registry.ENTITY_NUDIBRANCH
        );
    }

    /*
        The Depths spawn rule tweaks, makes creepers have a chance to breach
        and zombies a chance to be a miner
    */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void eventEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (event.getLevel().isClientSide || !(entity instanceof LivingEntity living)) { return; }

        if (invalidEntities.contains(living.getType()) && !living.getPersistentData().getBoolean("validSpawn")) {
            event.setCanceled(true);
            return;
        }

        if (!living.getType().getCategory().equals(MobCategory.MONSTER)) {
            return;
        }

        int stageOrdinal = GameStageHelper.getGlobalGameStage().getOrdinal();
        AttributeInstance maxHealthAttr = living.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance damageAttr = living.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance speedAttr = living.getAttribute(Attributes.MOVEMENT_SPEED);
        maxHealthAttr.addPermanentModifier(new AttributeModifier("spawn_stage_bonus", stageOrdinal * 0.125, AttributeModifier.Operation.MULTIPLY_BASE));
        damageAttr.addPermanentModifier(new AttributeModifier("spawn_stage_bonus", stageOrdinal * 0.0625, AttributeModifier.Operation.MULTIPLY_BASE));
        speedAttr.addPermanentModifier(new AttributeModifier("spawn_stage_bonus", stageOrdinal * 0.025, AttributeModifier.Operation.MULTIPLY_BASE));
        living.heal(Float.MAX_VALUE);

        if (event.getLevel().dimension().equals(Registry.DEPTHS_DIMENSION)) {
            //Maybe move to like a map lookup? IDK, or actually, IDC
            if (entity instanceof Creeper creeper) {
                if (event.getLevel().getRandom().nextInt(3) == 2) {
                    creeper.getPersistentData().putBoolean("enhancedai:breach", true);
                }
            } else if (entity instanceof Zombie zombie) {
                if (event.getLevel().getRandom().nextInt(3) == 2) {
                    zombie.getPersistentData().putBoolean("enhancedai:miner", true);
                }
            } else if (entity instanceof Skeleton skeleton) {
                if (event.getLevel().getRandom().nextInt(3) == 2) {
                    MobEffectInstance newEffect = switch (event.getLevel().getRandom().nextInt(3)) {
                        case 0 -> new MobEffectInstance(MobEffects.BLINDNESS, 300, 0);
                        case 1 -> new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_VULNERABLE.get(), 300, 0);
                        default -> new MobEffectInstance(MobEffects.WITHER, 300, 0);
                    };
                    skeleton.setItemInHand(InteractionHand.OFF_HAND, PotionUtils.setCustomEffects(new ItemStack(Items.TIPPED_ARROW), List.of(newEffect)));
                }
            } else if (entity instanceof Spider spider) {
                if (event.getLevel().getRandom().nextInt(3) == 2) {
                    spider.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, Integer.MAX_VALUE, 0));
                }
            } else if (entity instanceof EnderMan enderman) {
                if (event.getLevel().getRandom().nextInt(3) == 2) {
                    enderman.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 0));
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
        if (level.getGameTime() % 30 != 0) {
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
        else if (!level.isClientSide && level.dimension().equals(Registry.DEPTHS_DIMENSION)) {
            player.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_BLIGHTED.get(), 60, 1, true, true));
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
        AdditionalDrop addlDrops = additionalDrops.get(defender.getType());

        if (addlDrops != null) {
            level.addFreshEntity(new ItemEntity(level, origin.x, origin.y, origin.z, new ItemStack(addlDrops.item, level.random.nextInt(addlDrops.minDrop, addlDrops.maxDrop + 1))));
        }
        else if (level.dimension().equals(Level.NETHER)) {
            if (defender.getMobType().equals(MobType.UNDEAD) && random.nextInt(level.getBiomeManager().getBiome(defender.blockPosition()).equals(BIOME_SOUL_DROP) ? 200 : 400) == 0) {
                level.addFreshEntity(new ItemEntity(level, origin.x, origin.y, origin.z, new ItemStack(ITEM_UNDEAD_SOUL)));
            }
            else if (defender.getTags().contains("in.sanctum_guardian")) {
                level.addFreshEntity(new ItemEntity(level, origin.x, origin.y, origin.z, new ItemStack(ITEM_MAGICAL_EYE)));
            }

            else if (defender.getTags().contains("in.castle") && random.nextInt(100) == 0) {
                level.addFreshEntity(new ItemEntity(level, origin.x, origin.y, origin.z, new ItemStack(ITEM_EVIL_EYE)));
            }
        }
    }

    @SubscribeEvent
    public static void flightTakeDamage(LivingHurtEvent event) {
        LivingEntity defender = event.getEntity();
        if (event.getSource().getDirectEntity() != null && defender.hasEffect(Registry.EFFECT_FLIGHT_READY.get())) {
            defender.removeEffect(Registry.EFFECT_FLIGHT_READY.get());
        }
    }

    @SubscribeEvent
    public static void flightReadyFinished(MobEffectEvent.Expired event) {
        LivingEntity expiree = event.getEntity();
        MobEffectInstance effect = event.getEffectInstance();
        if (effect != null && effect.getEffect().equals(Registry.EFFECT_FLIGHT_READY.get()) && Registry.FLYING_MOUNT_CLASS.isInstance(expiree)) {
            expiree.addEffect(new MobEffectInstance(Registry.EFFECT_GROUNDED.get(), 7200, 0));
        }
    }

    @SubscribeEvent
    public static void flightReadyRemove(MobEffectEvent.Remove event) {
        LivingEntity expiree = event.getEntity();
        if (event.getEffect().equals(Registry.EFFECT_FLIGHT_READY.get()) && Registry.FLYING_MOUNT_CLASS.isInstance(expiree)) {
            expiree.addEffect(new MobEffectInstance(Registry.EFFECT_GROUNDED.get(), 7200, 0));
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
        EquipmentSlot slot = event.getSlotType();
        if (!slot.equals(EquipmentSlot.MAINHAND)) { return; }
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        if (item.equals(ITEM_KEEPER_FLAMBERGE)) {
            event.removeAttribute(Attributes.ATTACK_SPEED);
            event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(ItemUtils.UUID_ATTACK_SPEED, "Nerf", -2.8, AttributeModifier.Operation.ADDITION));
        }
        else if (item.equals(ITEM_SPELLBREAKER)) {
            event.removeAttribute(Attributes.ATTACK_SPEED);
            event.removeAttribute(Attributes.ATTACK_DAMAGE);
            event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(ItemUtils.UUID_ATTACK_SPEED, "Nerf", -2.4, AttributeModifier.Operation.ADDITION));
            event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(ItemUtils.UUID_ATTACK_DAMAGE, "Buff", 10.0, AttributeModifier.Operation.ADDITION));
        }
        else if (item.equals(ITEM_AMETHYST_RAPIER)) {
            event.removeAttribute(Attributes.ATTACK_SPEED);
            event.removeAttribute(Attributes.ATTACK_DAMAGE);
            event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(ItemUtils.UUID_ATTACK_SPEED, "Nerf", -2.2, AttributeModifier.Operation.ADDITION));
            event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(ItemUtils.UUID_ATTACK_DAMAGE, "Nerf_2", 5.0, AttributeModifier.Operation.ADDITION));
        }
    }
}
