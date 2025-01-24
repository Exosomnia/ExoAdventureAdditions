package com.exosomnia.exoadvadditions.events;

import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.Registry;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VanillaEventTweaks {

    private static final VillagerTrades.ItemListing ENCHANTED_BOOKS_TRADE = VillagerTrades.TRADES.get(VillagerProfession.LIBRARIAN).get(1)[1];

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
        Villager trading balancing, makes enchanted books guaranteed at ranks 3, 4, and 5
        but removes them from rank 1. Also changes it, so you can only trade for a book once
        before it goes out of stock.
    */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void villagerTradesEvent(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.LIBRARIAN) {
            //Move the possible book trade from rank 1 to 5, move the lantern trade from rank 2 to rank 1, and add a new rank 2 trade possibility to make up for it
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            VillagerTrades.ItemListing rank1Book = trades.get(1).remove(1);
            trades.get(5).add(0, rank1Book);
            VillagerTrades.ItemListing newRank1Trade = (entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(Items.LANTERN, 3), 12, 1, 0.05F);
            VillagerTrades.ItemListing newRank2Trade = (entity, random) -> new MerchantOffer(new ItemStack(Items.CHISELED_BOOKSHELF, 2), new ItemStack(Items.EMERALD), 12, 5, 0.05F);
            trades.get(1).add(newRank1Trade);
            trades.get(2).set(2, newRank2Trade);

            //Make the book trade at rank 3, and 4 guaranteed
            VillagerTrades.ItemListing rank3Book = trades.get(3).remove(1);
            trades.get(3).add(0, rank3Book);
            VillagerTrades.ItemListing rank4Book = trades.get(4).remove(1);
            trades.get(4).add(0, rank4Book);
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
        TagKey<Block> naturalOresTag = TagKey.create(Registries.BLOCK, new ResourceLocation(ExoAdventureAdditions.MODID, "natural_ores"));
        net.minecraft.core.Registry<Block> reg = event.getRegistryAccess().registryOrThrow(ForgeRegistries.Keys.BLOCKS);
        // Loop through ore pairs and resolve tags
        for (Pair<ResourceKey<Block>, ResourceKey<Block>> orePair : Registry.NATURAL_ORE_PAIRS) {
            List<TagKey<Block>> originalTags = reg.getHolder(orePair.left()).get().tags().toList();
            originalTags.add(naturalOresTag);
            reg.getHolder(orePair.right()).get().bindTags(originalTags);
        }
    }
}
