package dev.ftb.extendedexchange.inventory;

import dev.ftb.extendedexchange.block.entity.AbstractLinkInvBlockEntity;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LinkInputHandler extends BaseItemStackHandler<AbstractLinkInvBlockEntity> {
    public LinkInputHandler(AbstractLinkInvBlockEntity blockEntity, int inputSize) {
        super(blockEntity, inputSize);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return IEMCProxy.INSTANCE.hasValue(stack);
    }
}
