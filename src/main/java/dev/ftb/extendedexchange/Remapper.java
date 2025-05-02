package dev.ftb.extendedexchange;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

//@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ExtendedExchange.MOD_ID)
public class Remapper {
    // Temporary!
//    @SubscribeEvent
    public static void blocks(MissingMappingsEvent event) {
        event.getMappings(Registries.BLOCK,"projectex").forEach(mapping -> {
            Block remapped = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ExtendedExchange.MOD_ID, mapping.getKey().getPath()));
            mapping.remap(remapped);
        });
    }
//    @SubscribeEvent
    public static void items(MissingMappingsEvent event) {
        event.getMappings(Registries.ITEM,"projectex").forEach(mapping -> {
            Item remapped = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ExtendedExchange.MOD_ID, mapping.getKey().getPath()));
            mapping.remap(remapped);
        });
    }
//    @SubscribeEvent
    public static void menus(MissingMappingsEvent event) {
        event.getMappings(Registries.MENU,"projectex").forEach(mapping -> {
            MenuType<?> remapped = ForgeRegistries.MENU_TYPES.getValue(new ResourceLocation(ExtendedExchange.MOD_ID, mapping.getKey().getPath()));
            mapping.remap(remapped);
        });
    }
//    @SubscribeEvent
    public static void blockEntities(MissingMappingsEvent event) {
        event.getMappings(Registries.BLOCK_ENTITY_TYPE,"projectex").forEach(mapping -> {
            BlockEntityType<?> remapped = ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(new ResourceLocation(ExtendedExchange.MOD_ID, mapping.getKey().getPath()));
            mapping.remap(remapped);
        });
    }
//    @SubscribeEvent
    public static void recipeSerializers(MissingMappingsEvent event) {
        event.getMappings(Registries.RECIPE_SERIALIZER,"projectex").forEach(mapping -> {
            RecipeSerializer<?> remapped = ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(ExtendedExchange.MOD_ID, mapping.getKey().getPath()));
            mapping.remap(remapped);
        });
    }
}
