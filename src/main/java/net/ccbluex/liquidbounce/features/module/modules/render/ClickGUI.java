/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.TickEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.color.ColorMixer;
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.*;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "ClickGUI", description = "Opens the ClickGUI.", category = ModuleCategory.RENDER, keyBind = Keyboard.KEY_RSHIFT, forceNoSound = true, onlyEnable = true)
public class ClickGUI extends Module {
    private final ListValue styleValue = new ListValue("Style", new String[] {"LiquidBounce", "Null", "Slowly", "Black", "White", /* "Astolfo", "Test",  "Novoline", "Flux"*/}, "Null") {
        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            updateStyle();
        }
    };

    public final FloatValue scaleValue = new FloatValue("Scale", 1F, 0.4F, 2F);
    public final IntegerValue maxElementsValue = new IntegerValue("MaxElements", 15, 1, 20);

    private static final ListValue colorModeValue = new ListValue("Color", new String[] {"Custom", "Sky", "Rainbow", "LiquidSlowly", "Fade", "Mixer"}, "Custom");
    private static final IntegerValue colorRedValue = new IntegerValue("Red", 0, 0, 255);
    private static final IntegerValue colorGreenValue = new IntegerValue("Green", 160, 0, 255);
    private static final IntegerValue colorBlueValue = new IntegerValue("Blue", 255, 0, 255);
    private static final FloatValue saturationValue = new FloatValue("Saturation", 1F, 0F, 1F);
    private static final FloatValue brightnessValue = new FloatValue("Brightness", 1F, 0F, 1F);
    private static final IntegerValue mixerSecondsValue = new IntegerValue("Seconds", 2, 1, 10);

    public final ListValue backgroundValue = new ListValue("Background", new String[] {"Default", "Gradient", "None"}, "Default");

    public final IntegerValue gradStartValue = new IntegerValue("GradientStartAlpha", 255, 0, 255, () -> backgroundValue.get().equalsIgnoreCase("gradient"));
    public final IntegerValue gradEndValue = new IntegerValue("GradientEndAlpha", 0, 0, 255, () -> backgroundValue.get().equalsIgnoreCase("gradient"));

    public final ListValue animationValue = new ListValue("Animation", new String[] {"Azura", "Slide", "SlideBounce", "Zoom", "ZoomBounce", "None"}, "Azura");
    public final FloatValue animSpeedValue = new FloatValue("AnimSpeed", 1F, 0.01F, 5F, "x");

    public static Color generateColor() {
        Color c = new Color(255, 255, 255, 255);
        switch (colorModeValue.get().toLowerCase()) {
            case "custom":
                c = new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
                break;
            case "rainbow":
                c = new Color(RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), 0));
                break;
            case "sky":
                c = RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get());
                break;
            case "liquidslowly":
                c = ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get());
                break;
            case "fade":
                c = ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100);
                break;
            case "mixer":
                c = ColorMixer.getMixedColor(0, mixerSecondsValue.get());
                break;
        }
        return c;
    }

    @Override
    public void onEnable() {
        updateStyle();

        LiquidBounce.clickGui.progress = 0;
        LiquidBounce.clickGui.slide = 0;
        LiquidBounce.clickGui.lastMS = System.currentTimeMillis();
        mc.displayGuiScreen(LiquidBounce.clickGui);
    }

    private void updateStyle() {
        switch(styleValue.get().toLowerCase()) {
            case "liquidbounce":
                LiquidBounce.clickGui.style = new LiquidBounceStyle();
                break;
            case "null":
                LiquidBounce.clickGui.style = new NullStyle();
                break;
            case "slowly":
                LiquidBounce.clickGui.style = new SlowlyStyle();
                break;
            case "black":
                LiquidBounce.clickGui.style = new BlackStyle();
                break;
            case "white":
                LiquidBounce.clickGui.style = new WhiteStyle();
                break;
/*            case "astolfo":
                LiquidBounce.clickGui.style = new AstolfoStyle();
                break;
            case "test":
                LiquidBounce.clickGui.style = new TestStyle();
                break;*/
        }
    }
}
