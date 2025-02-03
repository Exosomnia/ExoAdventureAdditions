package com.exosomnia.exoadvadditions.loot;

import com.exosomnia.exoadvadditions.Registry;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class ChestsLootModifier extends LootModifier {

    //private static final Item ITEM_COLD_EYE = ForgeRegistries.ITEMS.getValue(new ResourceLocation("endrem", "cold_eye"));

    public static final Supplier<Codec<ChestsLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(codec -> codecStart(codec).apply(codec, ChestsLootModifier::new)));

    protected ChestsLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!context.hasParam(LootContextParams.THIS_ENTITY)) { return generatedLoot; }
        Entity entity = context.getParam(LootContextParams.THIS_ENTITY);

        //level.structureManager().getStructureAt(defender.blockPosition(), MAGICAL_EYE_STRUCT).getStructure() != null (OLD CONDITION FOR BEING IN STRUCTURE)
        ServerLevel level = (ServerLevel)entity.level();
        Structure endCity = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.END_CITY);
        if (!(entity instanceof ServerPlayer player) ||
                !context.getQueriedLootTableId().equals(ResourceLocation.of("minecraft:chests/end_city_treasure", ':')) ||
                level.structureManager().getStructureAt(player.blockPosition(), endCity).getStructure() == null) { return generatedLoot; }

        if (level.random.nextInt(5) == 0) { generatedLoot.add(new ItemStack(Registry.ITEM_MYSTERIOUS_TOME.get())); }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
