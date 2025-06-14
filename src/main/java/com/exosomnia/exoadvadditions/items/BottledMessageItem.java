package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class BottledMessageItem extends Item {

    public BottledMessageItem() {
        super(new Item.Properties().stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide) { return InteractionResultHolder.success(itemStack); }

        level.playSound(null, player, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 0.5F);

        ItemStack message;
        RandomSource random = level.random;
        switch (random.nextInt(5)) {
            case 0, 1:
                message = new ItemStack(Registry.ITEM_OLD_MANUSCRIPT.get());
                break;
            case 3, 4:
                message = new ItemStack(Registry.xpScrolls[random.nextInt(Registry.xpScrolls.length)]);
                message.getOrCreateTag().putInt("value", message.is(Registry.ITEM_SCROLL_OF_SKILL_XP_FISHING.get()) ? 9600 : 6400);
                break;
            default:
                message = new ItemStack(Registry.ITEM_UNLOCATED_MAP.get());
                CompoundTag tag = message.getOrCreateTag();
                tag.putString("structure", "minecraft:buried_treasure");
                tag.putString("name", "map.exoadvadditions.buried_treasure");
                break;
        }

        itemStack.shrink(1);
        if (itemStack.isEmpty()) { return InteractionResultHolder.consume(message); }
        if (!player.getInventory().add(message)) {
            player.drop(message, false);
        }
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("item.exoadvadditions.message_in_a_bottle.info.1").withStyle(ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
    }
}
