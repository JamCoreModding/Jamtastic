/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Jamalam360
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.jamfabric.compat.sandwichable;

import io.github.foundationgames.sandwichable.items.spread.SpreadType;
import io.github.jamalam360.jamfabric.jam.Jam;
import io.github.jamalam360.jamfabric.registry.ItemRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jamalam360
 */
public class JamSpreadType extends SpreadType {
    public static Map<ItemStack, Integer> cache = new HashMap<>();

    public JamSpreadType() {
        super(0, 0.0f, 0, ItemRegistry.JAM_JAR, ItemRegistry.JAM_JAR);
    }

    @Override
    public int getColor(ItemStack stack) {
        Jam jam = Jam.fromNbt(stack.getSubNbt("Jam"));
        if (jam.getIngredients().size() == 0) {
            return 0;
        }

        if (!cache.containsKey(stack)) {
            cache.put(stack, jam.getColor().getRGB());
        }

        return cache.get(stack);
    }

    @Override
    public int getHunger() {
        return 0;
    }

    @Override
    public float getSaturationModifier() {
        return 0;
    }

    @Override
    public List<StatusEffectInstance> getStatusEffects(ItemStack stack) {
        return Jam.fromNbt(stack.getSubNbt("Jam")).getEffects();
    }

    @Override
    public ItemConvertible getContainingItem() {
        return ItemRegistry.JAM_JAR;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemRegistry.JAM_JAR.getDefaultStack();
    }

    @Override
    public void onPour(ItemStack container, ItemStack spread) {
        spread.setSubNbt("Jam", container.getSubNbt("Jam"));
    }

    @Override
    public String getTranslationKey(String id, ItemStack stack) {
        return "text.jamfabric.jam_spread";
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }
}