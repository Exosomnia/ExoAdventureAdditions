package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.networking.packets.ParticleShapePacket;
import com.exosomnia.exolib.particles.options.RGBSParticleOptions;
import com.exosomnia.exolib.particles.shapes.ParticleShapeLine;
import com.exosomnia.exolib.particles.shapes.ParticleShapeOptions;
import com.exosomnia.exolib.utils.ComponentUtils;
import com.majruszsdifficulty.gamestage.GameStageHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysteriousTomeDormantItem extends Item {
    public MysteriousTomeDormantItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide) { return InteractionResultHolder.success(itemStack); }

        ServerLevel serverLevel = (ServerLevel)level;
        Holder<Structure> tomeStruct = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(Registry.MYSTERIOUS_TOME_STRUCTURE);
        Pair<BlockPos, Holder<Structure>> structure = serverLevel.getChunkSource().getGenerator().findNearestMapStructure(serverLevel, HolderSet.direct(tomeStruct), player.blockPosition(), 50, false);
        BlockPos blockpos = structure == null ? null : structure.getFirst();
        if (blockpos == null) return InteractionResultHolder.pass(itemStack);

        Vec3 eyePos = player.getEyePosition();
        Vec3 lineVec = eyePos.vectorTo(blockpos.getCenter()).normalize().multiply(4.0, 4.0, 4.0);
        PacketHandler.sendToPlayer(new ParticleShapePacket(new ParticleShapeLine(new RGBSParticleOptions(ExoLib.REGISTRY.GRID_PARTICLE.get(), 1.0F, 0.2F, 1.0F, 0.1F), eyePos, new ParticleShapeOptions.Line(lineVec.add(eyePos), 8))), (ServerPlayer) player);
        player.playNotifySound(SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0F, 0.5F);
        player.getCooldowns().addCooldown(this, 100);

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level contextLevel = context.getLevel();
        if (contextLevel.isClientSide) return InteractionResult.sidedSuccess(false);

        ServerLevel level = (ServerLevel)contextLevel;
        BlockPos position = context.getClickedPos();
        ServerPlayer player = (ServerPlayer)context.getPlayer();

        Holder<Structure> tomeStruct = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(Registry.MYSTERIOUS_TOME_STRUCTURE);
        if (level.structureManager().getStructureAt(player.blockPosition(), tomeStruct.get()) != StructureStart.INVALID_START) {
            if (level.getBlockState(position).is(Blocks.LECTERN)) {
                level.playSound(null, position, SoundEvents.BLAZE_DEATH, SoundSource.AMBIENT, 1.0F, 0.5F);

                ItemStack itemStack = context.getItemInHand();
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    player.setItemInHand(context.getHand(), new ItemStack(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get()));
                } else if (!player.getInventory().add(new ItemStack(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get()))) {
                    player.drop(new ItemStack(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get()), false);
                }
                player.awardStat(Stats.ITEM_CRAFTED.get(Registry.ITEM_MYSTERIOUS_TOME_ACTIVE.get()), 1);
                player.getCooldowns().addCooldown(this, 200);

                Vec3 middlePos = position.getCenter().add(0, 1, 1);
                level.explode(null, middlePos.x, middlePos.y, middlePos.z, 2F, Level.ExplosionInteraction.TNT);
                level.sendParticles(ParticleTypes.END_ROD, middlePos.x, middlePos.y, middlePos.z, 75, 0, 0, 0, 0.25);

                GameStageHelper.increaseGlobalGameStage(GameStageHelper.getGameStages().get(2));
                return InteractionResult.sidedSuccess(true);
            }
        }
        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.mysterious_tome_dormant.info.1"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
        components.add(ComponentUtils.formatLine(I18n.get("item.exoadvadditions.mysterious_tome_dormant.info.2"), ComponentUtils.Styles.DEFAULT_DESC.getStyle()));
    }
}