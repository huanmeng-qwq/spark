package me.lucko.spark.kookbc.mixin;

import me.lucko.spark.kookbc.event.TickEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snw.kookbc.impl.KBCClient;
import snw.kookbc.impl.console.Console;

/**
 * 2024/9/1<br>
 * spark<br>
 *
 * @author huanmeng_qwq
 */
@Mixin(Console.class)
public class ConsoleMixin {
    @Shadow
    @Final
    private KBCClient client;

    @Inject(method = "isRunning", at = @At("HEAD"), remap = false)
    public void loop(CallbackInfoReturnable<Boolean> cir) {
        client.getCore().getEventManager().callEvent(new TickEvent());
    }
}
