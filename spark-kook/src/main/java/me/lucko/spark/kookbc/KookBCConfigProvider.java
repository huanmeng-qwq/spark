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

import com.google.gson.*;
import me.lucko.spark.common.platform.serverconfig.ConfigParser;
import me.lucko.spark.common.platform.serverconfig.ExcludedConfigFilter;
import me.lucko.spark.common.platform.serverconfig.ServerConfigProvider;
import snw.jkook.config.MemorySection;
import snw.jkook.config.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class KookBCConfigProvider extends ServerConfigProvider {

    /**
     * A map of provided files and their type
     */
    private static final Map<String, ConfigParser> FILES;
    /**
     * A collection of paths to be excluded from the files
     */
    private static final Collection<String> HIDDEN_PATHS;

    public KookBCConfigProvider() {
        super(FILES, HIDDEN_PATHS);
    }

    private static class YamlConfigParser implements ConfigParser {
        public static final YamlConfigParser INSTANCE = new YamlConfigParser();
        protected static final Gson GSON = new GsonBuilder()
                .registerTypeAdapter(MemorySection.class, (JsonSerializer<MemorySection>) (obj, type, ctx) -> ctx.serialize(obj.getValues(false)))
                .create();

        @Override
        public JsonElement load(String file, ExcludedConfigFilter filter) throws IOException {
            Map<String, Object> values = this.parse(Paths.get(file));
            if (values == null) {
                return null;
            }

            return filter.apply(GSON.toJsonTree(values));
        }

        @Override
        public Map<String, Object> parse(BufferedReader reader) throws IOException {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
            return config.getValues(false);
        }
    }

    // Paper 1.19+ split config layout
    private static class SplitYamlConfigParser extends YamlConfigParser {
        public static final SplitYamlConfigParser INSTANCE = new SplitYamlConfigParser();

        @Override
        public JsonElement load(String group, ExcludedConfigFilter filter) throws IOException {
            String prefix = group.replace("/", "");

            Path configDir = Paths.get("config");
            if (!Files.exists(configDir)) {
                return null;
            }

            JsonObject root = new JsonObject();

            /*for (Map.Entry<String, Path> entry : getNestedFiles(configDir, prefix).entrySet()) {
                String fileName = entry.getKey();
                Path path = entry.getValue();

                Map<String, Object> values = this.parse(path);
                if (values == null) {
                    continue;
                }

                // apply the filter individually to each nested file
                root.add(fileName, filter.apply(GSON.toJsonTree(values)));
            }*/

            return root;
        }

        /*private static Map<String, Path> getNestedFiles(Path configDir, String prefix) {
            Map<String, Path> files = new LinkedHashMap<>();
            files.put("global.yml", configDir.resolve(prefix + "-global.yml"));
            files.put("world-defaults.yml", configDir.resolve(prefix + "-world-defaults.yml"));
            for (World world : Bukkit.getWorlds()) {
                files.put(world.getName() + ".yml", world.getWorldFolder().toPath().resolve(prefix + "-world.yml"));
            }
            return files;
        }*/
    }

    static {
        Map<String, ConfigParser> files = new HashMap<>();
        files.put("kbc.yml", YamlConfigParser.INSTANCE);

        for (String config : getSystemPropertyList("spark.serverconfigs.extra")) {
            files.put(config, YamlConfigParser.INSTANCE);
        }

        Set<String> hiddenPaths = new HashSet<>();
        hiddenPaths.add("token");
        hiddenPaths.add("webhook-port");
        hiddenPaths.add("webhook-encrypt-key");
        hiddenPaths.add("webhook-verify-token");
        hiddenPaths.add("webhook-route");
        FILES = files;
        HIDDEN_PATHS = hiddenPaths;
    }
}
