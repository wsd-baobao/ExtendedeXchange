package dev.ftb.extendedexchange.datagen.loot;

import dev.ftb.extendedexchange.block.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class BlockLootTables extends BlockLootSubProvider implements Supplier<LootTableSubProvider> {
    private final Set<Block> blocks = new HashSet<>();

    public BlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }


    @Override
    protected void add(Block blockIn, LootTable.Builder table) {
        super.add(blockIn, table);
        this.blocks.add(blockIn);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.ENERGY_LINK.get());
        dropSelf(ModBlocks.PERSONAL_LINK.get());
        dropSelf(ModBlocks.REFINED_LINK.get());
        dropSelf(ModBlocks.COMPRESSED_REFINED_LINK.get());
        ModBlocks.COLLECTOR.forEach((k, v) -> dropSelf(v.get()));
        ModBlocks.RELAY.forEach((k, v) -> dropSelf(v.get()));
        ModBlocks.POWER_FLOWER.forEach((k, v) -> dropSelf(v.get()));
        dropSelf(ModBlocks.STONE_TABLE.get());
        dropSelf(ModBlocks.ALCHEMY_TABLE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return this.blocks;
    }

    @Override
    public LootTableSubProvider get() {
        return null;
    }
}
