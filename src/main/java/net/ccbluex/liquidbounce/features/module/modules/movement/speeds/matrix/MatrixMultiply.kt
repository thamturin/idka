package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.matrix

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedType
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.event.MotionEvent


class MatrixMultiply: SpeedMode("MatrixMultiply", SpeedType.MATRIX) {
	override fun onEnable() {
		mc.thePlayer.jumpMovementFactor = 0.02f
		mc.timer.timerSpeed = 1f
	}
	override fun onDisable() {
		mc.thePlayer.jumpMovementFactor = 0.02f
        mc.timer.timerSpeed = 1.0f
	}
	override fun onMotion(event: MotionEvent) {
		if (!MovementUtils.isMoving()) {
            return
        }
        if (mc.thePlayer.onGround) {
            mc.timer.timerSpeed = 1.0f
            mc.thePlayer.jump()
        }
        if (mc.thePlayer.motionY > 0.003) {
            mc.thePlayer.motionX *= 1.0012
            mc.thePlayer.motionZ *= 1.0012
            mc.timer.timerSpeed = 1.05f
        }
	}
}