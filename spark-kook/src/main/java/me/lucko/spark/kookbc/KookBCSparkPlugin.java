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

import dev.rollczi.litecommands.argument.ArgumentKey;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.SparkPlugin;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.platform.serverconfig.ServerConfigProvider;
import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import me.lucko.spark.common.sampler.source.SourceMetadata;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;
import me.lucko.spark.kookbc.command.SparkLiteCommand;
import me.lucko.spark.kookbc.command.SparkLiteSuggester;
import snw.jkook.plugin.BasePlugin;
import snw.kookbc.impl.command.litecommands.LiteKookFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.stream.Stream;

public class KookBCSparkPlugin extends BasePlugin implements SparkPlugin {

    private SparkPlatform platform;

    @Override
    public void onEnable() {
        this.platform = new SparkPlatform(this);
        this.platform.enable();

        LiteKookFactory.builder(this).bind(SparkPlatform.class, () -> platform).commands(SparkLiteCommand.class)
                .argumentSuggester(String[].class, ArgumentKey.of("spark"), new SparkLiteSuggester(platform))
                .build();
    }

    @Override
    public void onDisable() {
        this.platform.disable();
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public Path getPluginDirectory() {
        return getDataFolder().toPath();
    }

    @Override
    public String getCommandName() {
        return "spark";
    }

    @Override
    public Stream<KookBCCommandSenderRepo> getCommandSenders() {
        return Stream.empty();
    }

    @Override
    public void executeAsync(Runnable task) {
        getCore().getScheduler().runTask(this, task);
    }

    @Override
    public void executeSync(Runnable task) {
        getCore().getScheduler().runTask(this, task);
    }

    @Override
    public void log(Level level, String msg) {
        if (level == Level.WARNING) {
            getLogger().warn(msg);
        } else if (level == Level.SEVERE) {
            getLogger().error(msg);
        } else if (level == Level.INFO) {
            getLogger().info(msg);
        } else {
            getLogger().info("{}: {}", level.getName(), msg);
        }
    }

    @Override
    public TickHook createTickHook() {
        return new KookBCTickHook(this);
    }

    @Override
    public ClassSourceLookup createClassSourceLookup() {
        return new KookBCClassSourceLookup(getCore());
    }

    @Override
    public Collection<SourceMetadata> getKnownSources() {
        return SourceMetadata.gather(
                Arrays.asList(getCore().getPluginManager().getPlugins()),
                plugin -> plugin.getDescription().getName(),
                plugin -> plugin.getDescription().getVersion(),
                plugin -> String.join(", ", plugin.getDescription().getAuthors())
        );
    }

    @Override
    public TickReporter createTickReporter() {
        return new KookBCTickReporter(this);
    }

    @Override
    public ServerConfigProvider createServerConfigProvider() {
        return new KookBCConfigProvider();
    }


    @Override
    public PlatformInfo getPlatformInfo() {
        return new KookBCPlatformInfo(getCore());
    }
}
