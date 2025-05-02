package dev.ftb.extendedexchange.block.entity;

import dev.ftb.extendedexchange.menu.PersonalLinkMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PersonalLinkBlockEntity extends AbstractLinkInvBlockEntity implements MenuProvider {
    public PersonalLinkBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntityTypes.PERSONAL_LINK.get(), blockPos, blockState, 18, 1);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.extendedexchange.personal_link");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return new PersonalLinkMenu(windowId, playerInv, getBlockPos());
    }
}
