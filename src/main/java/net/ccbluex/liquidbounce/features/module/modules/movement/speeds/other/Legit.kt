package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedType
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.event.MotionEvent
import net.minecraft.client.settings.GameSettings
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.movement.InvMove
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiIngameMenu
import net.minecraft.client.gui.inventory.GuiContainer

class Legit: SpeedMode("Legit", SpeedType.OTHER) {
    override fun onMotion(event: MotionEvent) {
        val invmove = LiquidBounce.moduleManager[InvMove::class.java]!!
        mc.gameSettings.keyBindJump.pressed = (
            (MovementUtils.isMoving() || GameSettings.isKeyDown(mc.gameSettings.keyBindJump))
            && (mc.inGameHasFocus || 
                (invmove.state && !(mc.currentScreen is GuiChat || mc.currentScreen is GuiIngameMenu) && (invmove.noDetectableValue.get() || mc.currentScreen !is GuiContainer))
            )
        )
    }
}