package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.matrix

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedType
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.event.MotionEvent


class MatrixSemiStrafe: SpeedMode("MatrixSemiStrafe", SpeedType.MATRIX) {
	override fun onEnable() {
		mc.thePlayer.jumpMovementFactor = 0.02f
		mc.timer.timerSpeed = 1f
	}
	override fun onDisable() {
		mc.thePlayer.jumpMovementFactor = 0.02f
        mc.timer.timerSpeed = 1.0f
	}
	override fun onMotion(event: MotionEvent) {
		if (MovementUtils.isMoving() && mc.thePlayer.onGround) {
            mc.thePlayer.jump()
            MovementUtils.strafe(0.3f)
        }
        if (mc.thePlayer.fallDistance > 0.1) {
            MovementUtils.strafe(0.22f)
        }
	}
}