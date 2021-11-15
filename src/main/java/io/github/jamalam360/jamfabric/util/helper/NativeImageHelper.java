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

package io.github.jamalam360.jamfabric.util.helper;

import io.github.jamalam360.jamfabric.JamModInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.Item;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import java.io.IOException;

/**
 * @author Jamalam360
 */
public class NativeImageHelper {
    /**
     * Returns a NativeImage of an Items texture
     */
    public static NativeImage getNativeImage(Item item) {
        try {
            Identifier id = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(item).getParticleSprite().getId();
            Resource texture = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(id.getNamespace(), "textures/" + id.getPath() + ".png"));
            return NativeImage.read(texture.getInputStream());
        } catch (IOException e) {
            JamModInit.LOGGER.log(Level.ERROR, "Caught an error while retrieving native image");
            e.printStackTrace();
        }

        return new NativeImage(16, 16, false);
    }
}
