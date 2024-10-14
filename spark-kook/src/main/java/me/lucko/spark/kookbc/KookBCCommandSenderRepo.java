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

import me.lucko.spark.common.command.sender.AbstractCommandSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import snw.jkook.command.CommandSender;
import snw.jkook.command.ConsoleCommandSender;
import snw.jkook.entity.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KookBCCommandSenderRepo extends AbstractCommandSender<CommandSender> {
    public List<String> messages = new ArrayList<>();

    public KookBCCommandSenderRepo(CommandSender sender) {
        super(sender);
    }

    @Override
    public String getName() {
        if (this.delegate instanceof ConsoleCommandSender) {
            return "CONSOLE";
        }
        if (this.delegate instanceof User) {
            return ((User) this.delegate).getFullName(null);
        }
        return this.delegate.getClass().getName();
    }

    @Override
    public UUID getUniqueId() {
        if (super.delegate instanceof User) {
            return UUID.nameUUIDFromBytes(("User:" + ((User) super.delegate).getId()).getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    @Override
    public void sendMessage(Component message) {
        String serialize = LegacyComponentSerializer.builder().character('§').formats(new ArrayList<>()).build().serialize(message);
//        System.out.println(serialize);
//        System.out.println(message);
        messages.add(serialize);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.delegate instanceof ConsoleCommandSender;
    }
}
