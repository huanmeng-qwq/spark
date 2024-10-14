package me.lucko.spark.kookbc.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.argument.Key;
import dev.rollczi.litecommands.annotations.bind.Bind;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Sender;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.kookbc.KookBCCommandSenderRepo;
import snw.jkook.command.CommandSender;

import java.util.function.Function;

/**
 * 2024/9/1<br>
 * spark<br>
 *
 * @author huanmeng_qwq
 */
@Command(name = "spark")
@Description("Spark 主命令")
public class SparkLiteCommand {
    @Execute
    public Object execute(@Bind SparkPlatform platform, @Sender CommandSender sender, @Arg("参数...") @Key("spark") String[] args) {
        KookBCCommandSenderRepo repo = new KookBCCommandSenderRepo(sender);
        return platform.executeCommand(repo, args).thenApply((Function<Void, Object>) unused -> repo.messages);
    }
}
