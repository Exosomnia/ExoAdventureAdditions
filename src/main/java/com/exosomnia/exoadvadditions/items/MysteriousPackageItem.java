package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MysteriousPackageItem extends Item {
    public MysteriousPackageItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(level.isClientSide) { return InteractionResultHolder.consume(stack); }
        player.playNotifySound(SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0F, 0.75F);

        CompoundTag packageTag = stack.getTag();
        if (packageTag == null) { return InteractionResultHolder.fail(stack); }

        String contains = packageTag.getString("contains");
        if (contains.isBlank()) { return InteractionResultHolder.fail(stack); }

        ServerLevel serverLevel = (ServerLevel)level;
        Item newItem = serverLevel.registryAccess().registryOrThrow(Registries.ITEM).get(ResourceLocation.of(contains, ':'));
        if (newItem == null) { return InteractionResultHolder.fail(stack); }

        int count = Math.max(packageTag.getInt("amount"), 1);
        ItemStack newStack = new ItemStack(newItem, count);
        stack.shrink(1);

        if (stack.isEmpty()) { return InteractionResultHolder.consume(newStack); }
        if (!player.getInventory().add(newStack.copy())) {
            player.drop(newStack, false);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("item.exoadvadditions.mysterious_package.info").withStyle(ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
    }
}
