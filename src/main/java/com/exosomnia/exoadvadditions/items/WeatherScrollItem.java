package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exolib.capabilities.persistentplayerdata.PersistentPlayerDataProvider;
import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class WeatherScrollItem extends Item {

    public WeatherScrollItem() {
        super(new Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.scroll_of_weather.info.1"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) { return InteractionResultHolder.consume(itemStack); }

        if (serverLevel.isRaining()) {
            serverLevel.setWeatherParameters(ServerLevel.RAIN_DELAY.sample(serverLevel.random) * 2, 0, false, false);

            itemStack.shrink(1);
            player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.75F);
            player.sendSystemMessage(Component.translatable("item.exoadvadditions.scroll_of_weather.used").withStyle(ChatFormatting.GOLD));

            return InteractionResultHolder.consume(itemStack);
        }

        player.sendSystemMessage(Component.translatable("item.exoadvadditions.scroll_of_weather.no_weather").withStyle(ChatFormatting.RED));
        return InteractionResultHolder.consume(itemStack);
    }
}
