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

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.jamalam360.jamfabric.item.JamJarItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

/**
 * @author Jamalam360
 */

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    private final boolean jamfabric$staLoaded = FabricLoader.getInstance().isModLoaded("server_translations");
    private final Gson jamfabric$gson = new Gson();
    private String jamfabric$cachedJson = "";
    private String jamfabric$cachedName = "";

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean hasCustomName();

    @Shadow
    public abstract @Nullable NbtCompound getSubNbt(String key);

    @Shadow
    public abstract ItemStack setCustomName(@Nullable Text name);

    @Shadow
    public abstract Text getName();

    @Inject(
            method = "getName",
            at = @At("HEAD"),
            cancellable = true
    )
    public void jamfabric$translateJamName(CallbackInfoReturnable<Text> cir) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER && this.getItem() instanceof JamJarItem) {
            if (this.hasCustomName()) {
                NbtCompound nbtCompound = this.getSubNbt("display");

                if (nbtCompound != null && nbtCompound.contains("Name", 8)) {
                    String[] text = new String[0];

                    if (this.jamfabric$staLoaded) {
                        String json = nbtCompound.getString("Name");

                        if (json.equals(this.jamfabric$cachedJson)) {
                            text = this.jamfabric$cachedName.split(" ");
                        } else {
                            JsonReader reader = this.jamfabric$gson.newJsonReader(new StringReader(json));

                            try {
                                while (!reader.nextName().equals("text")) {
                                }

                                String name = reader.nextString();
                                this.jamfabric$cachedJson = json;
                                this.jamfabric$cachedName = name;
                                text = name.split(" ");
                            } catch (IOException ignored) {
                            }
                        }
                    } else {
                       text = Text.Serializer.fromJson(nbtCompound.getString("Name")).asString().split(" ");
                    }

                    if (Arrays.stream(text).anyMatch(s -> s.contains("."))) {
                        String[] translatedText = new String[text.length];

                        for (int i = 0; i < text.length; i++) {
                            String s = text[i];

                            if (I18n.hasTranslation(s)) {
                                translatedText[i] = I18n.translate(s);
                            } else {
                                translatedText[i] = s;
                            }
                        }

                        this.setCustomName(new LiteralText(String.join(" ", translatedText)));
                        cir.setReturnValue(this.getName());
                    }
                }
            }
        }
    }
}
