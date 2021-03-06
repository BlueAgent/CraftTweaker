package com.blamejared.crafttweaker.impl.ingredients.transform;

import com.blamejared.crafttweaker.CraftTweaker;
import com.blamejared.crafttweaker.CraftTweakerRegistries;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.item.transformed.IIngredientTransformer;
import com.blamejared.crafttweaker.api.item.transformed.IIngredientTransformerSerializer;
import com.blamejared.crafttweaker.impl.item.MCItemStack;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class TransformDamage<T extends IIngredient> implements IIngredientTransformer<T> {
    private final int amount;

    public TransformDamage(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public IItemStack transform(IItemStack stack) {
        final ItemStack internal = stack.getInternal().copy();
        final int newDamage = internal.getDamage() + amount;
        if (internal.getMaxDamage() < newDamage) {
            return MCItemStack.EMPTY.get();
        }
        internal.setDamage(newDamage);
        return new MCItemStack(internal);
    }

    @Override
    public String getCommandString(T transformedIngredient) {
        if(this.amount == 1) {
            return transformedIngredient.getCommandString() + ".transformDamage()";
        }
        return String.format("%s.transformDamage(%s)", transformedIngredient.getCommandString(), amount);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public IIngredientTransformerSerializer getSerializer() {
        return CraftTweakerRegistries.TRANSFORM_DAMAGE_SERIALIZER;
    }

    public static final class TransformDamageSerializer implements IIngredientTransformerSerializer<TransformDamage<?>> {

        @Override
        public TransformDamage<?> parse(PacketBuffer buffer) {
            return new TransformDamage<>(buffer.readVarInt());
        }

        @Override
        public TransformDamage<?> parse(JsonObject json) {
            return new TransformDamage<>(json.getAsJsonPrimitive("amount").getAsInt());
        }

        @Override
        public void write(PacketBuffer buffer, TransformDamage<?> ingredient) {
            buffer.writeVarInt(ingredient.amount);
        }

        @Override
        public JsonObject toJson(TransformDamage<?> transformer) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("amount", transformer.amount);
            return jsonObject;
        }

        @Override
        public ResourceLocation getType() {
            return new ResourceLocation(CraftTweaker.MODID, "transform_damage");
        }
    }
}
