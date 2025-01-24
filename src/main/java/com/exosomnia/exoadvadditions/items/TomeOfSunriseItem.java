package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.ExoAdventureAdditions;
import com.exosomnia.exoadvadditions.capabilities.daytimedilation.DaytimeDilationProvider;
import com.exosomnia.exoadvadditions.capabilities.daytimedilation.DaytimeDilationStorage;
import com.exosomnia.exoadvadditions.capabilities.daytimedilation.IDaytimeDilationStorage;
import com.exosomnia.exolib.utils.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = ExoAdventureAdditions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TomeOfSunriseItem extends TomeItem {

    private final String reduction;

    public TomeOfSunriseItem(Item.Properties properties, int rank, boolean eternal) {
        super(properties, rank, eternal);
        reduction = String.format("%.1f", (1.0 - (1.0/(1.0 + (rank / 2.0)))) * 100.0);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_sunrise.info", reduction),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle()));
        if (eternal) { components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.tome_of_sunrise.info.extra"),
                ComponentUtils.Styles.DEFAULT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_DESC.getStyle(), ComponentUtils.Styles.HIGHLIGHT_STAT.getStyle())); }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (level.dimensionType().hasFixedTime()) {
                player.sendSystemMessage(Component.translatable("item.exoadvadditions.tome_of_sunrise.not_valid_dimension").withStyle(ChatFormatting.RED));
                return InteractionResultHolder.consume(itemStack);
            }
            if (!level.isNight()) {
                player.sendSystemMessage(Component.translatable("item.exoadvadditions.tome_of_sunrise.not_night").withStyle(ChatFormatting.RED));
                return InteractionResultHolder.consume(itemStack);
            }
            IDaytimeDilationStorage dilation = level.getCapability(DaytimeDilationProvider.DAYTIME_DILATION).resolve().orElse(new DaytimeDilationStorage(0));
            if (dilation.getAmount() >= rank) {
                player.sendSystemMessage(Component.translatable("item.exoadvadditions.tome_of_sunrise.already_active").withStyle(ChatFormatting.RED));
                return InteractionResultHolder.consume(itemStack);
            }
            player.startUsingItem(hand);
        }

        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getCapability(DaytimeDilationProvider.DAYTIME_DILATION).ifPresent(dilation -> {
                dilation.setAmount(rank);
            });
            if (entity instanceof ServerPlayer player) { player.getCooldowns().addCooldown(this, 20); }
            for (ServerPlayer player : serverLevel.players()) {
                player.displayClientMessage(Component.translatable("item.exoadvadditions.tome_of_sunrise.activated").withStyle(ChatFormatting.GOLD), false);
            }
        }
        else if (entity instanceof LocalPlayer player) { player.playSound(SoundEvents.PLAYER_LEVELUP, 0.75F, 0.75F); }
        if (!eternal) { itemStack.shrink(1); }
        return itemStack;
    }

    @SubscribeEvent
    public static void serverLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.level.isClientSide || event.level.dimensionType().hasFixedTime()) { return; }

        ServerLevel level = ((ServerLevel)event.level);
        IDaytimeDilationStorage dilation = level.getCapability(DaytimeDilationProvider.DAYTIME_DILATION).resolve().orElse(new DaytimeDilationStorage(0));
        int amount = dilation.getAmount();
        if (amount != 0 && level.getDayTime() % 2 == 0) {
            level.setDayTime(level.getDayTime() + amount);
            if (level.isDay()) {
                dilation.setAmount(0);
                for (ServerPlayer player : level.players()) {
                    player.displayClientMessage(Component.translatable("item.exoadvadditions.tome_of_sunrise.finished").withStyle(ChatFormatting.GOLD), false);
                }
            }
        }
    }
}
