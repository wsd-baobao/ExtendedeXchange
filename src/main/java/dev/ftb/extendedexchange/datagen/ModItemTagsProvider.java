package dev.ftb.extendedexchange.datagen;

import dev.ftb.extendedexchange.EXTags;
import dev.ftb.extendedexchange.item.ModItems;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;

import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class ModItemTagsProvider extends ItemTagsProvider {


    public ModItemTagsProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> completableFuture2, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(arg, completableFuture, completableFuture2, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        ModItems.MAGNUM_STAR.values().forEach(ro -> tag(PETags.Items.CURIOS_KLEIN_STAR).add(ro.get()));
        ModItems.COLOSSAL_STAR.values().forEach(ro -> tag(PETags.Items.CURIOS_KLEIN_STAR).add(ro.get()));

        tag(EXTags.Items.STONE_TABLE_WHITELIST).addTags(
                Tags.Items.INGOTS,
                Tags.Items.GEMS,
                Tags.Items.DUSTS,
                Tags.Items.NUGGETS,
                Tags.Items.STORAGE_BLOCKS,
                Tags.Items.ORES,
                Tags.Items.STONE,
                Tags.Items.COBBLESTONE,
                Tags.Items.SAND,
                ItemTags.DIRT,
                Tags.Items.GRAVEL,
                Tags.Items.OBSIDIAN,
                Tags.Items.NETHERRACK,
                Tags.Items.END_STONES,
                ItemTags.COALS,
                Tags.Items.DYES,
                Tags.Items.STRING,
                Tags.Items.LEATHER,
                Tags.Items.FEATHERS,
                Tags.Items.GUNPOWDER,
                Tags.Items.SEEDS,
                ItemTags.LOGS,
                ItemTags.SAPLINGS,
                Tags.Items.GLASS,
                Tags.Items.ENDER_PEARLS,
                Tags.Items.RODS_BLAZE
        );
        tag(EXTags.Items.STONE_TABLE_WHITELIST).add(
                Items.FLINT,
                Items.CLAY_BALL,
                Items.SUGAR_CANE,
                Items.GHAST_TEAR
        );
    }

}
