package com.exosomnia.exoadvadditions.events;

import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoadvadditions.items.GrowthScrollItem;
import com.exosomnia.exoadvadditions.items.TomeOfLuck;
import com.exosomnia.exoarmory.ExoArmory;
import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataProvider;
import com.exosomnia.exolib.mixin.interfaces.ILivingEntityMixin;
import com.exosomnia.exoskills.item.ArcaneSingularityItem;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.puffish.attributesmod.AttributesMod;
import net.puffish.attributesmod.api.DynamicModification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VanillaEventTweaks {

    private static final Map<Integer, VillagerTrades.ItemListing> ENCHANTED_BOOKS_TRADE = new HashMap<>();
    static {
        ENCHANTED_BOOKS_TRADE.put(1, VillagerTrades.TRADES.get(VillagerProfession.LIBRARIAN).get(1)[1]);
        ENCHANTED_BOOKS_TRADE.put(5, VillagerTrades.TRADES.get(VillagerProfession.LIBRARIAN).get(2)[1]);
        ENCHANTED_BOOKS_TRADE.put(10, VillagerTrades.TRADES.get(VillagerProfession.LIBRARIAN).get(3)[1]);
        ENCHANTED_BOOKS_TRADE.put(15, VillagerTrades.TRADES.get(VillagerProfession.LIBRARIAN).get(4)[1]);
    }

    /*
        Anvil repair tweaks, repairing at an anvil with the repair material now only
        costs 1 resource, and 2 levels, it also doesn't increase future repair costs.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void anvilUpdateEvent(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (left.getItem().isValidRepairItem(left, right) && left.getDamageValue() != 0) {
            ItemStack output = left.copy();
            output.setDamageValue(0);

            event.setCost(2);
            event.setMaterialCost(1);
            event.setOutput(output);
        }
    }

    /*
        Villager trading balancing, 1 enchant book guaranteed at rank 4, chance for 2, adds
        them to rank 5, and removes them from rank 1. Also, you can only trade for a book twice
        before it goes out of stock. Once a book goes out of stock, it changes to a new book.
    */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void villagerTradesEvent(VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        if (event.getType() == VillagerProfession.LIBRARIAN) {
            //Move the possible book trade from rank 1 to 5, move the lantern trade from rank 2 to rank 1, and add a new rank 2 trade possibility to make up for it
            VillagerTrades.ItemListing rank1Book = trades.get(1).remove(1);
            trades.get(5).add(0, rank1Book);
            VillagerTrades.ItemListing newRank1Trade = (entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(Items.LANTERN, 3), 12, 1, 0.05F);
            VillagerTrades.ItemListing newRank2Trade = (entity, random) -> new MerchantOffer(new ItemStack(Items.CHISELED_BOOKSHELF, 2), new ItemStack(Items.EMERALD), 12, 5, 0.05F);
            trades.get(1).add(newRank1Trade);
            trades.get(2).set(2, newRank2Trade);

            /*
                Make the book trade at rank 4 guaranteed by removing clock and compass trade.
                4 & 5 emeralds for those items is insane. Also gives a chance for 2 enchanted
                book trade offers at this level.
             */
            trades.get(4).remove(2);
            trades.get(4).remove(2);
            trades.get(4).add(ENCHANTED_BOOKS_TRADE.get(15));
        }
        else if (event.getType() == VillagerProfession.LEATHERWORKER) {
            VillagerTrades.ItemListing rank1Leather = trades.get(1).set(0, (entity, random) -> new MerchantOffer(new ItemStack(Items.LEATHER, 5), new ItemStack(Items.EMERALD), 16, 2, 0.05F));

            trades.get(2).add((entity, random) -> new MerchantOffer(new ItemStack(Items.STRING, 24), new ItemStack(Items.EMERALD), 12, 5, 0.05F));

            trades.get(3).remove(1);
            trades.get(3).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(Items.SADDLE), 12, 10, 0.05F));

            trades.get(4).remove(0);
            trades.get(4).add(0, (entity, random) -> new MerchantOffer(new ItemStack(Items.SCUTE, 2), new ItemStack(Items.EMERALD), 12, 30, 0.05F));
            trades.get(4).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.LEATHER, 2), 6, 15, 0.20F));

            trades.get(5).remove(1);
            trades.get(5).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(Items.GLOW_ITEM_FRAME, 2), 12, 30, 0.05F));

            ItemStack scholarSet = new ItemStack(Registry.ITEM_MYSTERIOUS_PACKAGE.get());
            CompoundTag compoundTag = new CompoundTag();
            ListTag listTag = new ListTag();
            CompoundTag contentsTag = new CompoundTag();
            contentsTag.putString("table", "exoadvadditions:gameplay/trade_scholar_set");
            listTag.add(contentsTag);
            compoundTag.put("contents", listTag);
            scholarSet.setTag(compoundTag);
            scholarSet.setHoverName(Component.translatable("trade.exoadvadditions.scholar_gear"));
            trades.get(5).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 16), scholarSet.copy(), 6, 30, 0.20F));
        }
        else if (event.getType() == VillagerProfession.CARTOGRAPHER) {
            ItemStack unlocatedMapTrade = new ItemStack(Registry.ITEM_UNLOCATED_MAP.get());
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("structure", "#exoadventure:rank_i_illager");
            compoundTag.putString("name", "map.exoadventure.rank_i_illager");
            unlocatedMapTrade.setTag(compoundTag);
            trades.get(1).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 4), unlocatedMapTrade.copy(), 12, 1, 0.05F));

            trades.get(5).add((entity, random) -> new MerchantOffer(new ItemStack(Registry.ITEM_UNLOCATED_MAP.get()), new ItemStack(Items.EMERALD), 12, 5, 0.05F));
        }
    }

    @SubscribeEvent
    public static void villagerTradeWithEvent(TradeWithVillagerEvent event) {
        MerchantOffer offer = event.getMerchantOffer();
        if (offer.getResult().is(Items.ENCHANTED_BOOK) && offer.isOutOfStock() && event.getAbstractVillager() instanceof Villager villager) {
            MerchantOffers offers = villager.getOffers();
            MerchantOffer newOffer = ENCHANTED_BOOKS_TRADE.get(offer.getXp()).getOffer(villager, villager.level().random);

            newOffer.setToOutOfStock();
            offers.set(offers.indexOf(offer), newOffer);

            Player player = event.getEntity();
            player.sendMerchantOffers(player.containerMenu.containerId, offers, villager.getVillagerData().getLevel(), villager.getVillagerXp(), villager.showProgressBar(), villager.canRestock());
        }
    }

    /*
        After using a totem of undying, get inflicted with a buff that prevents you
        from using another totem for 3 - 6 minutes, depending on the difficulty. This
        effect can be cured with an enchanted golden apple. Totems now also give
        invulnerability for 5 seconds.
     */
    @SubscribeEvent
    public static void totemPopEvent(LivingUseTotemEvent event) {
        if (event.getEntity().hasEffect(Registry.EFFECT_CHEATED_DEATH.get())) { event.setCanceled(true); }
    }

    @SubscribeEvent
    public static void tagUpdateEvent(TagsUpdatedEvent event) {
        TagKey<Block> naturalOresTag = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(ExoAdventureAdditions.MODID, "natural_ores"));
        net.minecraft.core.Registry<Block> reg = event.getRegistryAccess().registryOrThrow(ForgeRegistries.Keys.BLOCKS);
        // Loop through ore pairs and resolve tags
        for (Pair<ResourceKey<Block>, ResourceKey<Block>> orePair : Registry.NATURAL_ORE_PAIRS) {
            List<TagKey<Block>> originalTags = new ArrayList<>();
            reg.getHolder(orePair.left()).get().tags().forEach(originalTags::add);
            originalTags.add(naturalOresTag);
            reg.getHolder(orePair.right()).get().bindTags(originalTags);
        }
    }

    @SubscribeEvent
    public static void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        player.getCapability(PersistentPlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
            CompoundTag tag = playerData.get();
            if (tag.contains("exoadventure")) {
                CompoundTag modTag = tag.getCompound("exoadventure");
                AttributeInstance expAttribute = player.getAttribute(Registry.ATTRIBUTE_SKILL_EXP_BONUS.get());
                for (var i = 0; i < modTag.getInt("growthScrolls"); i++) {
                    GrowthScrollItem.applyAttributes(expAttribute, i);
                }
                if (modTag.getBoolean("luckTome")) {
                    TomeOfLuck.applyAttributes(player.getAttribute(Attributes.LUCK));
                }
            }
        });
    }

    @SubscribeEvent
    public static void playerMountEvent(EntityMountEvent event) {
        Entity mounter = event.getEntityMounting();
        Entity mounted = event.getEntityBeingMounted();
        if (!(mounter instanceof Player) || !(mounted instanceof LivingEntity entity) || !Registry.FLYING_MOUNT_CLASS.isInstance(event.getEntityBeingMounted())) { return; }

        boolean canFly = entity.hasEffect(Registry.EFFECT_FLIGHT_READY.get());
        try { Registry.FLYING_ALLOWED_FIELD.set(Registry.FLYING_MOUNT_CLASS.cast(mounted), canFly); }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void spawnerBreakDampen(PlayerEvent.BreakSpeed event) {
        if (!event.getState().is(Blocks.SPAWNER)) { return; }
        event.setNewSpeed((float)(6.0 * event.getOriginalSpeed() / (event.getOriginalSpeed() + 6.0)));
    }

    @SubscribeEvent
    public static void torchDepthsRemoval(BlockEvent.EntityPlaceEvent event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (!(event.getState().is(Blocks.TORCH) || event.getState().is(Blocks.WALL_TORCH)) || !(levelAccessor instanceof ServerLevel level) || !level.dimension().equals(Registry.DEPTHS_DIMENSION)) { return; }
        BlockPos blockOrigin = event.getPos();
        Vec3 origin = blockOrigin.getCenter();
        level.destroyBlock(blockOrigin, false);
        level.playSound(null, blockOrigin, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 0.75F);
        level.sendParticles(ParticleTypes.SMOKE, origin.x, origin.y, origin.z, 15, 0, 0, 0, 0.025);
    }

    /*
     * Various fixes for the exo mods bow functionality for modpack
     * No idea what mods are causing issues, but they are screwing with releaseUsing method for BowItem
     */
    @SubscribeEvent
    public static void modpackBowItemFixes(EntityJoinLevelEvent event) {
        Entity joined = event.getEntity();
        if (event.getLevel().isClientSide || !(joined instanceof AbstractArrow arrow) || !(arrow.getOwner() instanceof LivingEntity owner)) return;

        ItemStack usedItem = ((ILivingEntityMixin)owner).getLastUsedItemStack();
        if (usedItem == null || !(usedItem.getItem() instanceof ProjectileWeaponItem)) return;

        //Power nerf, reduce damage scaling to just 0.5 per level instead of 1.0 base + 0.5 after level 1
        int power = usedItem.getEnchantmentLevel(Enchantments.POWER_ARROWS);
        if (power > 0) arrow.setBaseDamage(arrow.getBaseDamage() - 0.5);

        CompoundTag arrowTag = arrow.getPersistentData();

        //Arcane Singularity fix
        if (ArcaneSingularityItem.isSingularityActive(usedItem)) {
            if (ArcaneSingularityItem.hasSingularityEffect(usedItem, ArcaneSingularityItem.SingularityEffect.SOUL_BURN)) {
                arrowTag.putBoolean("SOUL_BURN", true);
            }
            else if (ArcaneSingularityItem.hasSingularityEffect(usedItem, ArcaneSingularityItem.SingularityEffect.CRIPPLING_STRIKE)) {
                arrowTag.putBoolean("CRIPPLING_STRIKE", true);
            }
        }

        //Arrow Recovery attribute fix & Puffish attributes
        if (owner instanceof ServerPlayer player) {
            double recoveryChance = (player.getAttributeValue(ExoArmory.REGISTRY.ATTRIBUTE_ARROW_RECOVERY.get()) - 1.0);
            if (player.getRandom().nextDouble() < recoveryChance) arrowTag.putBoolean("Recovery", true);

            Item usedItemObject = usedItem.getItem();
            Vec3 movement = arrow.getDeltaMovement();
            if (usedItemObject instanceof BowItem) {
                arrow.setDeltaMovement(movement.scale(DynamicModification.create().withPositive(AttributesMod.BOW_PROJECTILE_SPEED, player).applyTo(1.0)));
            }
            else if (usedItemObject instanceof CrossbowItem) {
                arrow.setDeltaMovement(movement.scale(DynamicModification.create().withPositive(AttributesMod.CROSSBOW_PROJECTILE_SPEED, player).applyTo(1.0)));
            }
        }
    }
}