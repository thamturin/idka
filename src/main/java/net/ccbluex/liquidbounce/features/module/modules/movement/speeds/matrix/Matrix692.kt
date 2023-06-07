package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.matrix

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedType
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.event.MotionEvent
import net.minecraft.client.settings.GameSettings

class Matrix692: SpeedMode("Matrix692", SpeedType.MATRIX) {
	private var wasTimer = false

	override fun onDisable() {
		wasTimer = false
        mc.timer.timerSpeed = 1f
	}
	override fun onMotion(event: MotionEvent) {
		if (wasTimer) {
            wasTimer = false
            mc.timer.timerSpeed = 1f
        }
        mc.thePlayer.motionY -= 0.00348
        mc.thePlayer.jumpMovementFactor = 0.026f
        mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump)
        if (MovementUtils.isMoving() && mc.thePlayer.onGround) {
            mc.gameSettings.keyBindJump.pressed = false
            mc.timer.timerSpeed = 1.35f
            wasTimer = true
            mc.thePlayer.jump()
            MovementUtils.strafe()
        } else if (MovementUtils.getSpeed() < 0.215) {
            MovementUtils.strafe(0.215f)
        }
	}
}