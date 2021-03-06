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

package io.github.jamalam360.jamfabric.mixin.general;

import io.github.jamalam360.jamfabric.block.JamPotBlock;
import io.github.jamalam360.jamfabric.block.JamPotBlockEntity;
import io.github.jamalam360.jamfabric.data.JamIngredient;
import io.github.jamalam360.jamfabric.data.JamIngredientRegistry;
import io.github.jamalam360.jamfabric.registry.ItemRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Jamalam360
 */

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
    @Inject(
            method = "insert",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void jamfabric$tryInsertIntoJamJar(World world, BlockPos pos, BlockState state, Inventory inventory, CallbackInfoReturnable<Boolean> cir) {
        BlockPos jamPos = pos.offset(state.get(HopperBlock.FACING));

        if (world.getBlockEntity(jamPos) instanceof JamPotBlockEntity jamPotBlockEntity) {
            for (int i = 0; i < inventory.size(); ++i) {
                if (!inventory.getStack(i).isEmpty()) {
                    ItemStack itemStack = inventory.getStack(i).copy();

                    if (jamPotBlockEntity.canInsertWater() && itemStack.isOf(Items.WATER_BUCKET)) {
                        jamPotBlockEntity.setFilledWater(true);
                        itemStack.decrement(1);
                        inventory.setStack(i, itemStack);

                        if (world.isClient) {
                            world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                        }

                        cir.setReturnValue(true);
                    }

                    if (jamPotBlockEntity.canInsertSugar() && itemStack.isOf(Items.SUGAR)) {
                        jamPotBlockEntity.setFilledSugar(true);
                        itemStack.decrement(1);
                        inventory.setStack(i, itemStack);

                        JamPotBlock.playRandomBrewingSound(world, jamPos);

                        cir.setReturnValue(true);
                    }

                    if (itemStack.isFood() && jamPotBlockEntity.canInsertIngredients() && !itemStack.isOf(ItemRegistry.JAM_JAR)) {
                        jamPotBlockEntity.jam.add(itemStack.getItem());
                        itemStack.decrement(1);
                        inventory.setStack(i, itemStack);

                        JamPotBlock.playRandomBrewingSound(world, jamPos);

                        cir.setReturnValue(true);
                    }

                    if (JamIngredientRegistry.has(itemStack.getItem()) && jamPotBlockEntity.canInsertIngredients()) {
                        JamIngredient ingredient = JamIngredientRegistry.get(itemStack.getItem());
                        jamPotBlockEntity.jam.addRaw(ingredient);

                        itemStack.decrement(1);
                        inventory.setStack(i, itemStack);
                        JamPotBlock.playRandomBrewingSound(world, jamPos);

                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
}
