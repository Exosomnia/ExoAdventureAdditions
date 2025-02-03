package com.exosomnia.exoadvadditions.items;

import com.exosomnia.exoadvadditions.Registry;
import com.exosomnia.exoadvadditions.recipes.tome.ShapedTomeRecipe;
import com.exosomnia.exolib.ExoLib;
import com.exosomnia.exolib.networking.PacketHandler;
import com.exosomnia.exolib.networking.packets.ParticleShapePacket;
import com.exosomnia.exolib.particles.options.RGBSParticleOptions;
import com.exosomnia.exolib.particles.shapes.ParticleShapeLine;
import com.exosomnia.exolib.particles.shapes.ParticleShapeOptions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.BiConsumer;

public class MysteriousTomeItem extends Item {
    public MysteriousTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    public Rarity getRarity(ItemStack itemStack) {
        return Registry.MYSTERIOUS_RARITY;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level contextLevel = context.getLevel();
        if (contextLevel.isClientSide) return InteractionResult.sidedSuccess(true);

        BlockPos clickedPos = context.getClickedPos();
        ServerLevel level = (ServerLevel)contextLevel;
        ServerPlayer player = (ServerPlayer)context.getPlayer();
        Direction useDir = context.getClickedFace();
        Vec3i offsets = switch(useDir) {
            case UP -> new Vec3i(1, -2, 1);
            case DOWN -> new Vec3i(1, 0, 1);
            case NORTH -> new Vec3i(1, -1, 2);
            case SOUTH -> new Vec3i(1, -1, 0);
            case EAST -> new Vec3i(0, -1, 1);
            case WEST -> new Vec3i(2, -1, 1);
        };

        BlockPos startPos = clickedPos.offset(offsets);
        BlockPos middlePos = startPos.offset(-1, 1, -1);
        BlockPos[] positions = new BlockPos[27];

        BlockState[][] lowerLayer = new BlockState[3][3];
        BlockState[][] midLayer = new BlockState[3][3];
        BlockState[][] topLayer = new BlockState[3][3];

        int index = 0;
        for(var y = 0; y < 3; y++) {
            BlockState[][] targetLayer = switch (y) {
                default -> lowerLayer;
                case 1 -> midLayer;
                case 2 -> topLayer;
            };
            for(var x = 0; x < 3; x++) {
                for(var z = 0; z < 3; z++) {
                    BlockPos searchPos = startPos.offset(-x, y, -z);
                    positions[index++] = searchPos;
                    BlockState state = level.getBlockState(searchPos);
                    targetLayer[x][z] = state;
                }
            }
        }

        AABB items = new AABB(startPos.offset(0, 1, 0), middlePos.offset(0, 2, 0));
        List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, items);
        ImmutableList<ItemLike> craftingItems = null;
        if (!itemEntities.isEmpty()) {
            ImmutableList.Builder<ItemLike> builder = ImmutableList.builder();
            for (ItemEntity entity : itemEntities) {
                ItemStack stack = entity.getItem();
                for (var i = 0; i < stack.getCount(); i++) {
                    builder.add(stack.getItem());
                }
            }
            craftingItems = builder.build();
        }

        ShapedTomeRecipe recipe = Registry.TOME_RECIPE_MANAGER.getRecipe(craftingItems, topLayer, midLayer, lowerLayer);
        if (recipe == null) {
            player.sendSystemMessage(Component.translatable("item.exoadvadditions.mysterious_tome_active.not_valid").withStyle(ChatFormatting.RED), false);
            player.playNotifySound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 1.0F, 0.5F);
            player.getCooldowns().addCooldown(this, 20);
        }
        else {
            //Drop result and player mechanics
            ItemStack result = recipe.getResult();
            if (result != null) {
                level.addFreshEntity(new ItemEntity(level, middlePos.getX() + .5, middlePos.getY() + .5, middlePos.getZ() + .5, result));
                player.awardStat(Stats.ITEM_CRAFTED.get(result.getItem()), 1);
            }
            player.getCooldowns().addCooldown(this, 20);

            //Recipe resource cleanup
            for (BlockPos pos : positions) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
            for (ItemEntity entity : itemEntities) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }

            //Flashy effects
            level.playSound(null, middlePos, SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 1.0F, 0.75F);
            ParticleShapePacket particles = new ParticleShapePacket(new ParticleShapeLine(new RGBSParticleOptions(
                    ExoLib.REGISTRY.SWIRL_PARTICLE.get(), 0.0F, 0.5F, 0.25F, 0.1F),
                    player.getEyePosition(), new ParticleShapeOptions.Line(middlePos.getCenter(), 16)));
            for(ServerPlayer connected : level.players()) {
                PacketHandler.sendToPlayer(particles, connected);
            }

            BiConsumer<ServerPlayer, BlockPos> execute = recipe.getExecute();
            if (execute != null) { execute.accept(player, middlePos); }
        }

        return InteractionResult.sidedSuccess(false);
    }
}
