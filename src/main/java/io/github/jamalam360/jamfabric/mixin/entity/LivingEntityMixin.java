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

package io.github.jamalam360.jamfabric.mixin.entity;

import com.mojang.datafixers.util.Pair;
import io.github.jamalam360.jamfabric.registry.ItemRegistry;
import io.github.jamalam360.jamfabric.jam.Jam;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * @author Jamalam360
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Redirect(
            method = "applyFoodEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/FoodComponent;getStatusEffects()Ljava/util/List;"
            )
    )
    public List<Pair<StatusEffectInstance, Float>> jamfabric$applyFoodEffects(FoodComponent instance, ItemStack stack, World world, LivingEntity targetEntity) {
        if (stack.isOf(ItemRegistry.JAM_JAR)) {
            return Jam.fromNbt(stack.getSubNbt("Jam")).getRawEffects();
        } else {
            return instance.getStatusEffects();
        }
    }

    @Redirect(
            method = "eatFood",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;decrement(I)V"
            )
    )
    public void jamfabric$eatFoodDecrementRedirect(ItemStack stack, int amount) {
        if (stack.isOf(ItemRegistry.JAM_JAR)) {
            stack.removeSubNbt("Jam");
            stack.removeCustomName();
        } else {
            stack.decrement(amount);
        }
    }

    @Redirect(
            method = "spawnConsumptionEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"
            )
    )
    public void jamfabric$cancelDrinkSoundIfEmpty(LivingEntity instance, SoundEvent soundEvent, float volume, float pitch, ItemStack stack) {
        if (stack.isOf(ItemRegistry.JAM_JAR)) {
            if (Jam.fromNbt(stack.getSubNbt("Jam")).getIngredients().size() != 0) {
                instance.playSound(soundEvent, volume, pitch);
            }
        } else {
            instance.playSound(soundEvent, volume, pitch);
        }
    }
}
