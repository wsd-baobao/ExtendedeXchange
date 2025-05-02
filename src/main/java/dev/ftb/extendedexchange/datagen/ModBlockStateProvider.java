package dev.ftb.extendedexchange.datagen;

import dev.ftb.extendedexchange.block.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen.getPackOutput(), modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.ENERGY_LINK.get());
        simpleBlock(ModBlocks.PERSONAL_LINK.get());
        simpleBlock(ModBlocks.REFINED_LINK.get());
        simpleBlock(ModBlocks.COMPRESSED_REFINED_LINK.get());

        ModBlocks.COLLECTOR.forEach((k, v) ->
                simpleBlock(v.get(), models().cubeAll(ForgeRegistries.BLOCKS.getKey(v.get()).getPath(), modLoc("block/collector/" + k.name))));
        ModBlocks.RELAY.forEach((k, v) ->
                simpleBlock(v.get(), models().cubeAll(ForgeRegistries.BLOCKS.getKey(v.get()).getPath(), modLoc("block/relay/" + k.name))));
        ModBlocks.POWER_FLOWER.forEach((k, v) ->
                simpleBlock(v.get(), models().getBuilder(k.name + "_power_flower").parent(models().getExistingFile(modLoc("block/power_flower"))).texture("collector", modLoc("block/collector/" + k.name)).texture("relay", modLoc("block/relay/" + k.name))));

        getVariantBuilder(ModBlocks.STONE_TABLE.get())
                .forAllStates(state -> {
                    Direction dir = state.getValue(BlockStateProperties.FACING);
                    return ConfiguredModel.builder()
                            .modelFile(models().getExistingFile(modLoc("block/stone_table")))
                            .rotationX(dir == Direction.DOWN ? 0 : dir.getAxis().isHorizontal() ? 90 : 180)
                            .rotationY(dir.getAxis().isVertical() ? 0 : ((int) dir.toYRot()) % 360)
                            .build();
                });

        simpleBlock(ModBlocks.ALCHEMY_TABLE.get(), models().getExistingFile(modLoc("block/alchemy_table")));
    }
}
