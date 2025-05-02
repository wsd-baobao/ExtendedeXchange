package dev.ftb.extendedexchange.block;

import dev.ftb.extendedexchange.Matter;
import dev.ftb.extendedexchange.block.entity.RelayBlockEntity;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RelayBlock extends AbstractEXBlock {
    public final Matter matter;

    public RelayBlock(Matter m) {
        super(Properties.of().strength(5F).sound(SoundType.STONE));
        matter = m;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RelayBlockEntity(blockPos, blockState);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        list.add(Component.translatable("block.extendedexchange.relay.tooltip").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("block.extendedexchange.relay.relay_bonus",
                Component.literal("").append(TransmutationEMCFormatter.formatEMC(matter.relayBonus)).withStyle(ChatFormatting.GREEN)
        ).withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("block.extendedexchange.relay.max_transfer",
                Component.literal("").append(TransmutationEMCFormatter.formatEMC(matter.relayTransfer))
                        .withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }
}
