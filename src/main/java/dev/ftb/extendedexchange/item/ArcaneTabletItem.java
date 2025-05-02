package dev.ftb.extendedexchange.item;

import dev.ftb.extendedexchange.menu.ArcaneTabletMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class ArcaneTabletItem extends Item {
    public ArcaneTabletItem() {
        super(new Properties().stacksTo(1)
//                .tab(ModItems.ItemGroups.CREATIVE_TAB)
        );
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!world.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) player, new ContainerProvider(hand), (buf) -> buf.writeEnum(hand));
        }

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        list.add(Component.translatable("item.extendedexchange.arcane_tablet.tooltip").withStyle(ChatFormatting.GRAY));
    }

    private record ContainerProvider(InteractionHand hand) implements MenuProvider {
        @Override
        public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
            return new ArcaneTabletMenu(windowId, playerInventory);
        }

        @Override
        @Nonnull
        public Component getDisplayName() {
            return Component.translatable("item.extendedexchange.arcane_tablet");
        }
    }
}
