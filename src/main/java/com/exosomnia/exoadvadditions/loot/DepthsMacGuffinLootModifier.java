package com.exosomnia.exoadvadditions.loot;

import com.exosomnia.exoadvadditions.Registry;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class DepthsMacGuffinLootModifier extends LootModifier {

    public final ResourceKey<Level> dimension;
    public final TagKey<Block> blockTag;

    public static final Supplier<Codec<DepthsMacGuffinLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(codec -> codecStart(codec)
            .and(ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(dat -> dat.dimension))
            .and(TagKey.codec(Registries.BLOCK).fieldOf("blockTag").forGetter(dat -> dat.blockTag))
            .apply(codec, DepthsMacGuffinLootModifier::new)
    ));

    protected DepthsMacGuffinLootModifier(LootItemCondition[] conditionsIn, ResourceKey<Level> dimension, TagKey<Block> blockTag) {
        super(conditionsIn);
        this.dimension = dimension;
        this.blockTag = blockTag;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (generatedLoot.isEmpty() || !context.hasParam(LootContextParams.THIS_ENTITY) || !context.hasParam(LootContextParams.BLOCK_STATE)) { return generatedLoot; }

        Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
        BlockState blockState = context.getParam(LootContextParams.BLOCK_STATE);
        if (!entity.level().dimension().equals(dimension) || !blockState.is(blockTag)) { return generatedLoot; }

        generatedLoot.add(new ItemStack(Registry.ITEM_MACGUFFIN_11.get()));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
