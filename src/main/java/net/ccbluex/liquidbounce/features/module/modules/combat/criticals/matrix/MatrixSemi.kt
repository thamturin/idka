package net.ccbluex.liquidbounce.features.module.modules.combat.criticals.matrix


import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.CriticalMode
import net.ccbluex.liquidbounce.event.AttackEvent
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
class MatrixSemi: CriticalMode("MatrixSemi") {
	private var attacks = 0
    override fun onEnable() {
        attacks = 0
    }
    override fun onAttack(event: AttackEvent) {
        attacks++
        if (attacks > 3) {
        	mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0825080378093, mc.thePlayer.posZ, true))
        	mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.023243243674, mc.thePlayer.posZ, true))
        	mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0215634532004, mc.thePlayer.posZ, true))
        	mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.00150000001304, mc.thePlayer.posZ, true))
            attacks = 0
        }
    }
}
