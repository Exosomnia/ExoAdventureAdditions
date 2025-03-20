package com.exosomnia.exoadvadditions.loot;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataProvider;
import com.exosomnia.exolib.mixin.interfaces.ILootParamsMixin;
import com.exosomnia.exolib.mixin.mixins.LootContextAccessor;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class ChestsLootModifier extends LootModifier {

    private static final ResourceLocation CHEST_CONTEXT = ResourceLocation.fromNamespaceAndPath("minecraft", "chest");

    public static final Supplier<Codec<ChestsLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(codec -> codecStart(codec).apply(codec, ChestsLootModifier::new)));

    protected ChestsLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if(!context.hasParam(LootContextParams.THIS_ENTITY)) { return generatedLoot; }

        ILootParamsMixin lootParams = ((ILootParamsMixin)((LootContextAccessor)context).getParams());
        if (!lootParams.shouldLootModify()) { return generatedLoot; }

        ResourceLocation lootCause = lootParams.getCause();
        if (lootCause.equals(CHEST_CONTEXT) && context.getParam(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player) {
            player.getCapability(PersistentPlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> {
                CompoundTag dataTag = playerData.get();
                CompoundTag modTag = dataTag.getCompound("exoadventure");
                if (!modTag.contains("firstLoot")) {
                    modTag.putBoolean("firstLoot", true);
                    generatedLoot.add(new ItemStack(Registry.ITEM_MYSTERIOUS_TOME.get()));
                }
                dataTag.put("exoadventure", modTag);
                playerData.set(dataTag);
            });
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
