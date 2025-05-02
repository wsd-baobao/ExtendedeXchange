package dev.ftb.extendedexchange.recipes;

import com.google.gson.JsonObject;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AlchemyTableRecipe implements Recipe<Container> {
    private static final long MIN_RECIPE_EMC_COST = 64L;

    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;
    private final long emcOverride;
    private final int craftingTime;

    public AlchemyTableRecipe(ResourceLocation id, Ingredient input, ItemStack output, long emcOverride, int craftingTime) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.emcOverride = emcOverride;
        this.craftingTime = craftingTime;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return output.copy();
    }

//    @Override
//    public ItemStack assemble(Container container) {
//        return output.copy();
//    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output;
    }

//    @Override
//    public ItemStack getResultItem() {
//        return output;
//    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.ALCHEMY_TABLE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ALCHEMY_TABLE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    public long getTotalCost(ItemStack inputStack) {
        return emcOverride > 0L ?
                emcOverride :
                Math.max(MIN_RECIPE_EMC_COST, (IEMCProxy.INSTANCE.getValue(inputStack) + IEMCProxy.INSTANCE.getValue(output)) * 3L);
    }

    public int getCraftingTime() {
        return craftingTime > 0 ? craftingTime : 200;
    }

    public void toJson(JsonObject json) {
        json.add("input", input.toJson());
        json.add("output", RecipeHelper.serializeOneItemStack(output));
        json.addProperty("emc_override", emcOverride);
        json.addProperty("crafting_time", craftingTime);
    }
//extends IForgeRegistryEntry<RecipeSerializer<?>>
    public static class Serializer<T extends AlchemyTableRecipe>  implements RecipeSerializer<T> {
        private final IFactory<T> factory;

        public Serializer(IFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.getAsJsonObject("input"));
            ItemStack output = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("output"));
            long emcOverride = json.has("emc_override") ? json.get("emc_override").getAsLong() : 0L;
            int progressOverride = json.has("crafting_time") ? json.get("crafting_time").getAsInt() : 200;
            return factory.create(recipeId, input, output, emcOverride, progressOverride);
        }

        @Nullable
        @Override
        public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            long emcOverride = buffer.readLong();
            int progressOverride = buffer.readInt();
            return factory.create(recipeId, input, output, emcOverride, progressOverride);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, AlchemyTableRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeLong(recipe.emcOverride);
            buffer.writeInt(recipe.craftingTime);
        }

        public interface IFactory<T extends AlchemyTableRecipe> {
            T create(ResourceLocation id, Ingredient input, ItemStack output, long emcOverride, int craftingTime);
        }
    }
}
