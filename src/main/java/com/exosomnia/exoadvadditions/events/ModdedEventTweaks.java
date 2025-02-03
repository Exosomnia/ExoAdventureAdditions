package com.exosomnia.exoadvadditions.events;

import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoarmory.ExoArmory;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModdedEventTweaks {

    //private static Structure MAGICAL_EYE_STRUCT;
    private static Holder<Biome> BIOME_SOUL_DROP;
    private static Item ITEM_UNDEAD_SOUL;
    private static Item ITEM_MAGICAL_EYE;
    private static Item ITEM_EVIL_EYE;
    private static Item ITEM_EVIL_ESSENCE;

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
    }

    /*
        The Depths spawn rule tweaks, makes creepers have a chance to breach
        and zombies a chance to be a miner
    */
    @SubscribeEvent
    public static void eventEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (event.getLevel().isClientSide || !event.getLevel().dimension().equals(Registry.DEPTHS_DIMENSION)) {
            return;
        }
        //Maybe move to like a map lookup? IDK, or actually, IDC
        if (entity instanceof Creeper creeper) {
            if (event.getLevel().getRandom().nextInt(3) == 2) {
                creeper.getPersistentData().putBoolean("enhancedai:breach", true);
            }
        }
        else if (entity instanceof Zombie zombie) {
            if (event.getLevel().getRandom().nextInt(3) == 2) {
                zombie.getPersistentData().putBoolean("enhancedai:miner", true);
            }
        }
        else if (entity instanceof Skeleton skeleton) {
            if (event.getLevel().getRandom().nextInt(3) == 2) {
                MobEffectInstance newEffect = switch (event.getLevel().getRandom().nextInt(3)) {
                    case 0 -> new MobEffectInstance(MobEffects.BLINDNESS, 300, 0);
                    case 1 -> new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_VULNERABLE.get(), 300, 0);
                    default -> new MobEffectInstance(MobEffects.WITHER, 300, 0);
                };
                skeleton.setItemInHand(InteractionHand.OFF_HAND, PotionUtils.setCustomEffects(new ItemStack(Items.TIPPED_ARROW), List.of(newEffect)));
            }
        }
        else if (entity instanceof Spider spider) {
            if (event.getLevel().getRandom().nextInt(3) == 2) {
                spider.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, Integer.MAX_VALUE, 0));
            }
        }
        else if (entity instanceof EnderMan enderman) {
            if (event.getLevel().getRandom().nextInt(3) == 2) {
                enderman.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 0));
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
        if (level.isClientSide || level.getGameTime() % 30 != 0 || !level.dimension().equals(Registry.DEPTHS_DIMENSION)) {
            return;
        }
        player.addEffect(new MobEffectInstance(ExoArmory.REGISTRY.EFFECT_BLIGHTED.get(), 60, 1, true, true));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tableLoadEvent(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        String namespace = name.getNamespace();
        String path = name.getPath();
        if (path.startsWith("chests") || (namespace.equals("betterdungeons") && path.contains("chests")) || (namespace.equals("explorify") && path.startsWith("chest")) || name.toString().equals("terralith:underground/chest")) {
            event.getTable().addPool(LootPool.lootPool().when(LootItemRandomChanceCondition.randomChance(0.001F)).setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Registry.ITEM_MACGUFFIN_16.get())).build());
        }
        else if (MONSTER_TABLES.contains(name)) {
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
        if (!(attacker instanceof ServerPlayer player)) { return; }

        LivingEntity defender = event.getEntity();
        Vec3 origin = defender.position();
        ServerLevel level = (ServerLevel)player.level();
        RandomSource random = level.getRandom();

        if (defender.getType().equals(EntityType.ELDER_GUARDIAN) && event.isRecentlyHit()) { level.addFreshEntity(new ItemEntity(level, origin.x, origin.y, origin.z, new ItemStack(Registry.ITEM_ELDER_GUARDIAN_EYE.get()))); }
        else if (defender.getType().equals(EntityType.WITHER) && event.isRecentlyHit()) {
            level.addFreshEntity(new ItemEntity(level, origin.x, origin.y, origin.z, new ItemStack(ITEM_EVIL_ESSENCE, level.random.nextInt(5))));
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
}
