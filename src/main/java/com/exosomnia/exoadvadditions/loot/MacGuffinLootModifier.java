package com.exosomnia.exoadvadditions.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class MacGuffinLootModifier extends LootModifier {

//    public static final Supplier<Codec<LootrMacGuffinLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(codec -> codecStart(codec)
//            .and(ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(dat -> dat.dimension))
//            .and(TagKey.codec(Registries.BLOCK).fieldOf("blockTag").forGetter(dat -> dat.blockTag))
//            .apply(codec, LootrMacGuffinLootModifier::new)
//    ));

    public static final Supplier<Codec<MacGuffinLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(codec -> codecStart(codec).apply(codec, MacGuffinLootModifier::new)));

    protected MacGuffinLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
