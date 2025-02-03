package com.exosomnia.exoadvadditions.loot;

import com.exosomnia.exoadvadditions.Registry;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.NotNull;

public class DepthsLootModifier extends LootModifier {

    public static final Supplier<Codec<DepthsLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(codec -> codecStart(codec)
            .and(TagKey.codec(Registries.BLOCK).fieldOf("blockTag").forGetter(dat -> dat.blockTag))
            .apply(codec, DepthsLootModifier::new)
    ));
    private final TagKey<Block> blockTag;

    protected DepthsLootModifier(LootItemCondition[] conditionsIn, TagKey<Block> blockTag) {
        super(conditionsIn);
        this.blockTag = blockTag;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!context.hasParam(LootContextParams.THIS_ENTITY) || !context.hasParam(LootContextParams.BLOCK_STATE) || generatedLoot.isEmpty()) { return generatedLoot; }

        Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
        if (!entity.level().dimension().equals(Registry.DEPTHS_DIMENSION) || !(entity instanceof LivingEntity miner)) { return generatedLoot; }

        BlockState mined = context.getParam(LootContextParams.BLOCK_STATE);
        if (!mined.is(blockTag)) { return generatedLoot; }

        int luckMod = (int)(miner.getAttributeValue(Attributes.LUCK) * 4.0);
        if (context.getRandom().nextInt(100) < (20 + luckMod)) {
            generatedLoot.forEach(stack -> stack.grow(context.getRandom().nextInt(stack.getCount() + 1)));
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
