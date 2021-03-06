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

package io.github.jamalam360.jamfabric.block;

import io.github.jamalam360.jamfabric.color.Color;
import io.github.jamalam360.jamfabric.jam.JamStateListener;
import io.github.jamalam360.jamfabric.registry.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;

/**
 * @author Jamalam360
 */
public class JamPotBlockEntityRenderer implements BlockEntityRenderer<JamPotBlockEntity>, JamStateListener {
    public static final BlockState JAM = BlockRegistry.JAM_RENDER.getDefaultState();
    private static final BakedModel JAM_BAKED_MODEL = MinecraftClient.getInstance().getBlockRenderManager().getModel(JAM);
    private static final int LERP = 1;

    @SuppressWarnings("unused")
    public JamPotBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(JamPotBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.jam.getIngredients().size() > 0 && entity.jam.getColor().getBlue() == 255 && entity.jam.getColor().getGreen() == 255 && entity.jam.getColor().getRed() == 255) {
            entity.jam.recalculate();
        }

        if (entity.jam.getIngredients().size() == 0 && !entity.hasWater()) return; //No Items, no water, no render!

        entity.onUpdated();

        assert entity.getWorld() != null;
        assert GameRenderer.getRenderTypeEntityCutoutShader() != null;
        assert GameRenderer.getRenderTypeEntityCutoutShader().colorModulator != null;

        Color color;

        if (!entity.cachedColor.equals(entity.lerpingTo)) { // If we're not already lerping towards the new color, start lerping towards the new color (from the last color)
            entity.lerpingTo = entity.cachedColor;
        }

        if (entity.lastLerpProgress == null) { // If we haven't started lerping yet, set the last lerp progress to the last color
            entity.lastLerpProgress = entity.lastColorBeforeChange;
        }

        // Lerp from the last lerp position towards the current color of the BE
        color = lerpBetween(LERP, entity.lastLerpProgress, entity.lerpingTo);
        entity.lastLerpProgress = color; // Set the last progress to the calculated color for next tick

        if (entity.hasWater() && entity.jam.getIngredients().size() == 0) {
            color = Color.WATER;
        }

        matrices.push();
        matrices.scale(0.82f, 0.82f, 0.82f);
        matrices.translate(0.0625f, 0.0625f, 0.0625f);

        MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(
                matrices.peek(),
                vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout()),
                JAM,
                JAM_BAKED_MODEL,
                color.getRed() / 255f,
                color.getGreen() / 255f,
                color.getBlue() / 255f,
                light,
                overlay
        );

        matrices.pop();
    }

    private static Color lerpBetween(int lerp, Color from, Color to) {
        int red = lerpBetweenInt(lerp, from.getRed(), to.getRed());
        int green = lerpBetweenInt(lerp, from.getGreen(), to.getGreen());
        int blue = lerpBetweenInt(lerp, from.getBlue(), to.getBlue());

        return new Color(red, green, blue);
    }

    private static int lerpBetweenInt(int lerp, int from, int to) {
        if (from != to) {
            if (from > to) {
                return from - lerp;
            } else {
                return from + lerp;
            }
        } else {
            return to;
        }
    }
}
