package net.kyrptonaught.tooltipfix.mixin;

import net.kyrptonaught.tooltipfix.Helper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(DrawContext.class)
public abstract class FixToolTipMixin {

    @Shadow
    public abstract int getScaledWindowWidth();

    @ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At(value = "HEAD"), index = 2, argsOnly = true)
    public List<TooltipComponent> makeListMutable(List<TooltipComponent> value) {
        return new ArrayList<>(value);
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At(value = "HEAD"))
    public void fix(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
        Helper.newFix(components, textRenderer, x, getScaledWindowWidth());
    }

    @Redirect(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At(value = "INVOKE", target = "Lorg/joml/Vector2ic;x()I", remap = false))
    public int modifyRenderX(Vector2ic vector2ic, TextRenderer textRenderer, List<TooltipComponent> components, int x) {
        return Helper.shouldFlip(components, textRenderer, x);
    }
}