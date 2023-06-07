package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.aac

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedType
import net.ccbluex.liquidbounce.event.MotionEvent

class AACv4BHop: SpeedMode("AACv4BHop", SpeedType.AAC) {
	override fun onMotion(event: MotionEvent) {
		if(mc.thePlayer.isInWater) return;

        if (mc.thePlayer.moveForward > 0) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump()
                mc.timer.timerSpeed = 1.6105f
                mc.thePlayer.motionX *= 1.0708
                mc.thePlayer.motionZ *= 1.0708
            } else if (mc.thePlayer.fallDistance > 0) {
                mc.timer.timerSpeed = 0.6f
            }               
        }    
	}
}
