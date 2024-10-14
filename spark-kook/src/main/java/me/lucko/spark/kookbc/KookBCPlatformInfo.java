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

import me.lucko.spark.common.platform.PlatformInfo;
import snw.jkook.Core;

public class KookBCPlatformInfo implements PlatformInfo {
    private final Core core;

    public KookBCPlatformInfo(Core core) {
        this.core = core;
    }

    @Override
    public Type getType() {
        return Type.SERVER;
    }

    @Override
    public String getName() {
        return "JKook";
    }

    @Override
    public String getBrand() {
        return this.core.getImplementationName();
    }

    @Override
    public String getVersion() {
        return this.core.getImplementationVersion();
    }

    @Override
    public String getMinecraftVersion() {
        return this.core.getImplementationVersion();
    }
}
