package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.aac


import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.event.AttackEvent
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
class AACv4: CriticalMode("AACv4") {
	override fun onAttack(event: AttackEvent) {
		mc.thePlayer.motionZ *= 0
        mc.thePlayer.motionX *= 0
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3e-14, mc.thePlayer.posZ, true))
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 8e-15, mc.thePlayer.posZ, true))
	}
}
