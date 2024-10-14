package me.lucko.spark.kookbc.command;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.suggester.Suggester;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.kookbc.KookBCCommandSenderRepo;
import snw.jkook.command.CommandSender;

/**
 * 2024/9/3<br>
 * spark<br>
 *
 * @author huanmeng_qwq
 */
public class SparkLiteSuggester implements Suggester<CommandSender, String[]> {
    private final SparkPlatform platform;

    public SparkLiteSuggester(SparkPlatform platform) {
        this.platform = platform;
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<String[]> argument, SuggestionContext suggestionContext) {
        return SuggestionResult.of(platform.tabCompleteCommand(new KookBCCommandSenderRepo(invocation.sender()), invocation.arguments().asArray()));
    }
}
