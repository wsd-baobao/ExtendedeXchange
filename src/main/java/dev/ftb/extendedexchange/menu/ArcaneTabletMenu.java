package dev.ftb.extendedexchange.menu;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import dev.ftb.extendedexchange.util.EXUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.math.BigInteger;
import java.util.*;

public class ArcaneTabletMenu extends AbstractTableMenu {
    private static final int[] ROTATION_SLOTS = {0, 1, 2, 5, 8, 7, 6, 3};

    private final CraftingContainer craftMatrix;
    private final ResultContainer craftResult;

    public ArcaneTabletMenu(int windowId, Inventory invPlayer) {
        super(ModMenuTypes.ARCANE_TABLET.get(), windowId, invPlayer, null);

        craftMatrix = new ArcaneTabletCraftingContainer(this, 3, 3, (IItemHandlerModifiable) getProvider().getInputAndLocks());
        craftResult = new ResultContainer();

        addSlot(new ArcaneTabletResultSlot(invPlayer.player, craftMatrix, craftResult, 0, -23, 75));
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new Slot(craftMatrix, x + y * 3, -59 + x * 18, 17 + y * 18));
            }
        }
        addPlayerSlots(invPlayer, 8, 135);

        slotChangedCraftingGrid(this, invPlayer.player.level(), invPlayer.player, craftMatrix, craftResult);
    }

    public ArcaneTabletMenu(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
        this(windowId, playerInv);
    }

    @Override
    public void handleGUIButtonPress(String tag, boolean shiftHeld, ServerPlayer player) {
        switch (tag) {
            case "clear" -> {
                clearCraftingMatrix();
                return;
            }
            case "rotate" -> {
                rotateCraftingMatrix(!shiftHeld);
                return;
            }
            case "balance" -> {
                if (shiftHeld) {
                    spreadCraftingMatrix();
                } else {
                    balanceCraftingMatrix();
                }
                return;
            }
        }
        super.handleGUIButtonPress(tag, shiftHeld, player);
    }

    static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer container, ResultContainer result) {
        // copied from CraftingMenu.slotChangedCraftingGrid, which has protected access
        CraftingRecipe craftingRecipe;
        if (level.isClientSide) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ItemStack itemStack = ItemStack.EMPTY;
        Optional<CraftingRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, level);
        if (optional.isPresent() && result.setRecipeUsed(level, serverPlayer, craftingRecipe = optional.get())) {
            itemStack = craftingRecipe.assemble(container, RegistryAccess.EMPTY);
        }
        result.setItem(0, itemStack);
        menu.setRemoteSlot(0, itemStack);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, itemStack));
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);

        slotChangedCraftingGrid(this, player.level(), player, craftMatrix, craftResult);
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= playerSlotsStart && slotId < slots.size()) {
            Slot slot = slots.get(slotId);
            if (slot.getSlotIndex() == player.getInventory().selected) {
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot.getSlotIndex() == player.getInventory().selected) {
            return ItemStack.EMPTY;
        }
        if (slot instanceof ArcaneTabletResultSlot resultSlot) {
            if (resultSlot.hasItem()) {
                ItemStack stack = resultSlot.getItem();
                ItemStack oldStack = stack.copy();

                if (!moveItemStackTo(stack, playerSlotsStart, playerSlotsStart + 36, true)) {
                    return ItemStack.EMPTY;
                }

                if (stack.isEmpty()) {
                    resultSlot.set(ItemStack.EMPTY);
                } else {
                    resultSlot.setChanged();
                }

                if (stack.getCount() == oldStack.getCount()) {
                    return ItemStack.EMPTY;
                }

                resultSlot.onTakeNoRefill(player, stack);
                player.drop(stack, false);
                return oldStack;
            }

            return ItemStack.EMPTY;
        }
        return super.quickMoveStack(player, index);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != craftResult && super.canTakeItemForPickAll(stack, slot);
    }

    public void onRecipeTransfer(Int2ObjectMap<List<ItemStack>> stacksMap, boolean transferAll) {
        // handles transferring items into the crafting slots via JEI '+' button
        clearCraftingMatrix();

        int repeat = transferAll ? 64 : 1;
        for (int i = 0; i < repeat; i++) {
            transferItems(stacksMap);
        }

        slotChangedCraftingGrid(this, player.level(), player, craftMatrix, craftResult);
        broadcastChanges();
    }

    private void transferItems(Int2ObjectMap<List<ItemStack>> stacksMap) {
        // try and fulfill the request from player's inventory first
        stacksMap.forEach(this::transferFromInventory);

        // then try and fulfill from the tablet (EMC)
        boolean syncEMC = false;
        for (Map.Entry<Integer, List<ItemStack>> entry : stacksMap.int2ObjectEntrySet()) {
            if (transferFromTablet(entry.getKey(), entry.getValue())) {
                syncEMC = true;
            }
        }
        if (syncEMC) provider.syncEmc((ServerPlayer) player);
    }

    private boolean transferFromTablet(int destSlot, List<ItemStack> candidateStacks) {
        if (candidateStacks.size() > 1) {
            candidateStacks = candidateStacks.stream().sorted(Comparator.comparingLong(o -> IEMCProxy.INSTANCE.getValue(o))).toList();
        }

        for (ItemStack stack : candidateStacks) {
            ItemStack fixed = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(stack)).createStack();

            if (provider.hasKnowledge(fixed)) {
                long value = IEMCProxy.INSTANCE.getValue(fixed);
                BigInteger bigValue = BigInteger.valueOf(value);
                if (value > 0L && provider.getEmc().compareTo(bigValue) > 0) {
                    ItemStack slotItem = craftMatrix.getItem(destSlot);

                    if (slotItem.isEmpty()) {
                        craftMatrix.setItem(destSlot, fixed);
                    } else if (slotItem.getCount() < slotItem.getMaxStackSize()
                            && slotItem.getItem() == fixed.getItem()
                            && Objects.equals(slotItem.getItem().getShareTag(slotItem), fixed.getItem().getShareTag(fixed))) {
                        slotItem.grow(1);
                    } else {
                        continue;
                    }
                    provider.setEmc(provider.getEmc().subtract(bigValue));
                    return true;
                }
            }
        }

        return false;
    }

    private boolean transferFromInventory(int destSlot, List<ItemStack> candidateStacks) {
        for (ItemStack candidate : candidateStacks) {
            ItemStack candidateFixed = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(candidate)).createStack();

            for (int j = 0; j < player.getInventory().getContainerSize(); ++j) {
                ItemStack stack = player.getInventory().getItem(j);
                ItemStack fixed = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(stack)).createStack();

                if (ItemHandlerHelper.canItemStacksStack(candidateFixed, fixed)) {
                    ItemStack slotItem = craftMatrix.getItem(destSlot);

                    if (slotItem.isEmpty()) {
                        craftMatrix.setItem(destSlot, ItemHandlerHelper.copyStackWithSize(stack, 1));
                    } else if (slotItem.getCount() < slotItem.getMaxStackSize()
                            && slotItem.getItem() == stack.getItem()
                            && Objects.equals(slotItem.getItem().getShareTag(slotItem), stack.getItem().getShareTag(stack))) {
                        slotItem.grow(1);
                    } else {
                        continue;
                    }

                    stack.shrink(1);

                    if (stack.isEmpty()) {
                        player.getInventory().setItem(j, ItemStack.EMPTY);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public void clearCraftingMatrix() {
        boolean syncEmc = false;
        for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
            ItemStack stack = craftMatrix.removeItemNoUpdate(i);
            if (!stack.isEmpty()) {
                long value = IEMCProxy.INSTANCE.getValue(stack);
                if (ProjectEConfig.server.difficulty.covalenceLoss.get() >= 1D && value > 0L) {
                    ItemInfo itemInfo = ItemInfo.fromStack(stack);
                    EXUtils.KnowledgeAddResult res = EXUtils.addKnowledge(player, provider, IEMCProxy.INSTANCE.getPersistentInfo(itemInfo).createStack());
                    if (res != EXUtils.KnowledgeAddResult.NOT_ADDED) {
                        provider.setEmc(provider.getEmc().add(BigInteger.valueOf(value * stack.getCount())));
                        syncEmc = true;
                        if (res == EXUtils.KnowledgeAddResult.ADDED) {
                            provider.syncKnowledgeChange((ServerPlayer) player, itemInfo, true);
                        }
                        continue;
                    }
                }
                player.getInventory().placeItemBackInInventory(stack, true);
            }
        }
        if (syncEmc) provider.syncEmc((ServerPlayer) player);
        slotChangedCraftingGrid(this, player.level(), player, craftMatrix, craftResult);
    }

    public void rotateCraftingMatrix(boolean clockwise) {
        ItemStack[] stacks = new ItemStack[ROTATION_SLOTS.length];

        if (clockwise) {
            for (int i = 0; i < ROTATION_SLOTS.length; i++) {
                int j = i - 1;
                if (j < 0) {
                    j = ROTATION_SLOTS.length - 1;
                }
                stacks[i] = craftMatrix.getItem(ROTATION_SLOTS[j % ROTATION_SLOTS.length]);
            }
        } else {
            for (int i = 0; i < ROTATION_SLOTS.length; i++) {
                stacks[i] = craftMatrix.getItem(ROTATION_SLOTS[(i + 1) % ROTATION_SLOTS.length]);
            }
        }

        for (int i = 0; i < ROTATION_SLOTS.length; i++) {
            craftMatrix.setItem(ROTATION_SLOTS[i], stacks[i]);
        }

        slotChangedCraftingGrid(this, player.level(), player, craftMatrix, craftResult);
        broadcastChanges();
    }

    public void balanceCraftingMatrix() {
        ArrayListMultimap<CompoundTag, ItemStack> map = ArrayListMultimap.create();
        Multiset<CompoundTag> itemCount = HashMultiset.create();

        for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
            ItemStack stack = craftMatrix.getItem(i);
            if (!stack.isEmpty() && stack.getMaxStackSize() > 1) {
                CompoundTag key = stack.serializeNBT();
                key.remove("Count");
                map.put(key, stack);
                itemCount.add(key, stack.getCount());
            }
        }

        for (CompoundTag key : map.keySet()) {
            List<ItemStack> list = map.get(key);
            int totalCount = itemCount.count(key);
            int countPerStack = totalCount / list.size();
            int restCount = totalCount % list.size();

            for (ItemStack stack : list) {
                stack.setCount(countPerStack);
            }

            int idx = 0;

            while (restCount > 0) {
                ItemStack stack = list.get(idx);
                if (stack.getCount() < stack.getMaxStackSize()) {
                    stack.grow(1);
                    restCount--;
                }

                if (++idx >= list.size()) {
                    idx = 0;
                }
            }
        }

        slotChangedCraftingGrid(this, player.level(), player, craftMatrix, craftResult);
        broadcastChanges();
    }

    public void spreadCraftingMatrix() {
        while (true) {
            ItemStack biggestStack = null;
            int biggestSize = 1;

            for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
                ItemStack stack = craftMatrix.getItem(i);
                if (!stack.isEmpty() && stack.getCount() > biggestSize) {
                    biggestStack = stack;
                    biggestSize = stack.getCount();
                }
            }

            if (biggestStack == null) {
                return;
            }

            boolean emptyBiggestSlot = false;

            for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
                ItemStack stack = craftMatrix.getItem(i);

                if (stack.isEmpty()) {
                    if (biggestStack.getCount() > 1) {
                        craftMatrix.setItem(i, biggestStack.split(1));
                    } else {
                        emptyBiggestSlot = true;
                    }
                }
            }

            if (!emptyBiggestSlot) {
                break;
            }
        }

        balanceCraftingMatrix();
    }

    public class ArcaneTabletResultSlot extends ResultSlot {
        public ArcaneTabletResultSlot(Player player, CraftingContainer matrix, Container result, int slot, int x, int y) {
            super(player, matrix, result, slot, x, y);
        }

//        @Override
//        protected void onCrafting(ItemStack stack) {
//            super.onCrafting(stack);
//
//            if (IEMCProxy.INSTANCE.hasValue(stack) && ProjectEXUtils.addKnowledge(player, playerData, ProjectEXUtils.fixOutput(stack)) == 2 && knowledgeUpdate != null) {
//                knowledgeUpdate.updateKnowledge();
//            }
//        }

        @Override
        protected void onQuickCraft(ItemStack stack, int amount) {
            super.onQuickCraft(stack, amount);
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            // when extracting result, try to backfill the crafting matrix with items from EMC

            ItemStack[] prevItems = new ItemStack[craftMatrix.getContainerSize()];
            for (int i = 0; i < prevItems.length; i++) {
                prevItems[i] = craftMatrix.getItem(i);
                if (!prevItems[i].isEmpty()) {
                    prevItems[i] = ItemHandlerHelper.copyStackWithSize(prevItems[i], 1);
                }
            }

            super.onTake(player, stack);

            for (int i = 0; i < prevItems.length; i++) {
                if (!prevItems[i].isEmpty() && craftMatrix.getItem(i).isEmpty()) {
                    transferFromTablet(i, ImmutableList.of(prevItems[i]));
                }
            }
        }

        public void onTakeNoRefill(Player player, ItemStack stack) {
            // see ArcaneTable#quickMoveStack()
            // called when shift-clicking out; just call the super onTake(), to avoid backfilling
            super.onTake(player, stack);
        }
    }

    public class ArcaneTabletCraftingContainer implements CraftingContainer {
        private final IItemHandlerModifiable items;

        public ArcaneTabletCraftingContainer(ArcaneTabletMenu tablet, int width, int height, IItemHandlerModifiable items) {
            super();

            this.items = items;
        }

        @Override
        public int getContainerSize() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            for (int i = 0; i < items.getSlots(); i++) {
                if (!items.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int index) {
            return index < 0 || index >= getContainerSize() ? ItemStack.EMPTY : items.getStackInSlot(index);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            items.setStackInSlot(slot, stack);
            slotsChanged(this);
        }

        @Override
        public ItemStack removeItemNoUpdate(int index) {
            if (index < 0 || index >= getContainerSize()) {
                return ItemStack.EMPTY;
            }
            ItemStack stack0 = items.getStackInSlot(index);
            items.setStackInSlot(index, ItemStack.EMPTY);
            return stack0;
        }

        @Override
        public ItemStack removeItem(int index, int count) {
            if (index < 0 || index >= getContainerSize() || count <= 0 || items.getStackInSlot(index).isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack stack = items.getStackInSlot(index).split(count);

            if (!stack.isEmpty()) {
                slotsChanged(this);
            }

            return stack;
        }

        @Override
        public void clearContent() {
            for (int i = 0; i < items.getSlots(); i++) {
                items.setStackInSlot(i, ItemStack.EMPTY);
            }
        }

        @Override
        public void fillStackedContents(StackedContents helper) {
            for (int i = 0; i < items.getSlots(); i++) {
                helper.accountStack(items.getStackInSlot(i));
            }
        }

        @Override
        public void setChanged() {
            slotsChanged(this);
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public int getWidth() {
            return 3;
        }

        @Override
        public int getHeight() {
            return 3;
        }

        @Override
        public List<ItemStack> getItems() {
            return List.of();
        }
    }
}
