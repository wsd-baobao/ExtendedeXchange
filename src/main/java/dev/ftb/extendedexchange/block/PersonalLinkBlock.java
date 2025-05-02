package dev.ftb.extendedexchange.block;

import dev.ftb.extendedexchange.block.entity.PersonalLinkBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PersonalLinkBlock extends AbstractEXBlock {

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockpos, BlockState state) {
        return new PersonalLinkBlockEntity(blockpos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        list.add(Component.translatable("block.extendedexchange.personal_link.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
