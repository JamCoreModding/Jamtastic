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

package io.github.jamalam360.jamfabric.util.registry;

import io.github.jamalam360.jamfabric.JamModInit;
import io.github.jamalam360.jamfabric.compat.CompatibilityPlugin;
import io.github.jamalam360.jamfabric.compat.sandwichable.SandwichablePlugin;
import io.github.jamalam360.jamfabric.mixin.JamMixinPlugin;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jamalam360
 */
public class CompatRegistry {
    public static final List<CompatibilityPlugin> COMPATIBILITY_PLUGINS = new ArrayList<>();

    static {
        COMPATIBILITY_PLUGINS.add(new SandwichablePlugin());
    }

    public static void init() {
        for (CompatibilityPlugin plugin : COMPATIBILITY_PLUGINS) {
            if (FabricLoader.getInstance().isModLoaded(plugin.getModId())) {
                JamModInit.LOGGER.log(Level.INFO, "Initializing compatibility with " + plugin.getModId());
                JamMixinPlugin.ACTIVE_COMPATIBILITY_MIXIN_PACKAGES.add(plugin.getModId());
                plugin.init();
            }
        }
    }
}
