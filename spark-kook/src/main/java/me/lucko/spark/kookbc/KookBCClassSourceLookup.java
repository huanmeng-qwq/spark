/*
 * This file is part of spark.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lucko.spark.kookbc;

import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import org.checkerframework.checker.nullness.qual.Nullable;
import snw.jkook.Core;
import snw.jkook.config.file.YamlConfiguration;
import snw.jkook.plugin.Plugin;
import snw.kookbc.impl.launch.LaunchClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class KookBCClassSourceLookup implements ClassSourceLookup {
    private static final Class<?> PLUGIN_CLASS_LOADER;
    private static final Field JAR_FILE_FIELD;

    static {
        try {
            PLUGIN_CLASS_LOADER = Class.forName("snw.kookbc.impl.plugin.SimplePluginClassLoader");
            JAR_FILE_FIELD = PLUGIN_CLASS_LOADER.getDeclaredField("jarFile");
            JAR_FILE_FIELD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final Core core;
    private final Map<Class<?>, String> NAME_CACHE = new HashMap<>();
    private final Map<ClassLoader, String> LOADER_CACHE = new HashMap<>();

    public KookBCClassSourceLookup(Core core) {
        this.core = core;
    }

    @Override
    public @Nullable String identify(Class<?> clazz) throws Exception {
        if (NAME_CACHE.containsKey(clazz)) {
            return NAME_CACHE.get(clazz);
        }
        ClassLoader loader = clazz.getClassLoader();
        if (PLUGIN_CLASS_LOADER.isInstance(loader)) {
            if (LOADER_CACHE.containsKey(loader)) {
                return LOADER_CACHE.get(loader);
            }
            JarFile plugin = (JarFile) JAR_FILE_FIELD.get(loader);
            ZipEntry entry = plugin.getEntry("plugin.yml");
            try (InputStream a = plugin.getInputStream(entry)) {
                YamlConfiguration pluginYml = YamlConfiguration.loadConfiguration(new InputStreamReader(a));
                String mainClass = pluginYml.getString("main");
                for (Plugin p1 : core.getPluginManager().getPlugins()) {
                    if (p1.getClass().getName().equals(mainClass)) {
                        NAME_CACHE.put(clazz, p1.getDescription().getName());
                        LOADER_CACHE.put(loader, p1.getDescription().getName());
                        return p1.getDescription().getName();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (loader instanceof LaunchClassLoader) {
            // if mixin ?
            URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
            for (Plugin plugin : core.getPluginManager().getPlugins()) {
                if (location.toString().contains(plugin.getFile().toURI().toURL().toString())) {
                    NAME_CACHE.put(clazz, plugin.getDescription().getName());
                    return plugin.getDescription().getName();
                }
            }
            NAME_CACHE.put(clazz, "KookBC");
        }
        return "KookBC";
    }
}

