package dev.ftb.extendedexchange.datagen;

import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.datagen.loot.BlockLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Mod.EventBusSubscriber(modid = ExtendedExchange.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EXDataGen {
    public static final String MODID = ExtendedExchange.MOD_ID;

    @SubscribeEvent
    public static void dataGenEvent(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper efh = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        if (event.includeClient()) {
            gen.addProvider(event.includeClient(),new ModLangProvider(gen, MODID, "en_us"));
            gen.addProvider(event.includeClient(),new ModBlockStateProvider(gen, MODID, efh));
            gen.addProvider(event.includeClient(),new ModBlockModelProvider(gen, MODID, efh));
            gen.addProvider(event.includeClient(),new ModItemModelProvider(gen, MODID, efh));
        }

        if (event.includeServer()) {
            ModBlockTagsProvider blockTags = new ModBlockTagsProvider(gen.getPackOutput(),lookupProvider, MODID, efh);
            gen.addProvider(event.includeServer(),blockTags);
            gen.addProvider(event.includeServer(),new ModItemTagsProvider(gen.getPackOutput(), lookupProvider,blockTags.contentsGetter(), MODID, efh));
            gen.addProvider(event.includeServer(),new ModRecipeProvider(gen));
            gen.addProvider(event.includeServer(),new LootTableProvider(gen.getPackOutput(), Collections.emptySet(), List.of(
                    new LootTableProvider.SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK)
            )));
        }
    }
}
