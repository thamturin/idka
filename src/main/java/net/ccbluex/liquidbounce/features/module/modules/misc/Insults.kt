package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.event.*
import net.minecraft.entity.player.EntityPlayer
import net.ccbluex.liquidbounce.value.TextValue
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "Insults", description = "Insults people when you kill them.", category = ModuleCategory.MISC)
class Insults: Module() {
	private val modeValue = ListValue("Mode", arrayOf("LB-", "HeroMC", "Redesky", "Other", "Custom"), "HeroMC")
	private val customName = TextValue("CustomName", "khoi", {modeValue.get().equals("Rape") || modeValue.get().equals("NamKi")})

	private val heroMCInsults = arrayOf(
		"I'm not hacking it's just my Intel Core I9 13900k RTX 4090Ti and 64GB RAM!",
		"")
	private val lbminusInsults = arrayOf(
		"Download LiquidBounceMinus đi %name% ",
	    "Giờ mà còn dùng FDP gì nữa %name%",
	    "Delete FDP today! Try to install LiquidBounceMinus",
	    "Why someone don't use LiquidBounceMinus?",
	    "Chắc là không giòn đâu %name% ",
	    "When people play with you, it's considered charity work",
	    "I know you're rage quitting but with that trash client, you'd be having the skill issue",
	    "I don't care about the fact that I'm hacking I just care about how you died by using FDP",
	    "Update Your gaming chair pls %name%",
	    "Your client sucks to be honest",
	    "It's a bird! It's a plane! No it's is LiquidBounceMinus",
	    "That client is so trash I think you should download LiquidBounceMinus",
	)

	private val redeskyInsults = arrayOf(
		"You're so bad that if I played with you, I'd be losing every single game",
	    "There's not enough adjectives to describe how bad you are",
	    "Here's your ticket to spectator mode",
	    "You're that kind of non-recycable trash that no one knows what to do with",
	    "Are your hands freezing? Because you missed every single hit",
	    "I must be in a deranked game if I'm in the lobby with you, %name%",
	    "I'm not hacking it's just my 871619-B21 HP Intel Xeon 8180 2.5GHz DL380 G10 processor",
	    "When people play with you, it's considered charity work",
	    "I know you're rage quitting but with that aim, you'd be having trouble clicking the disconnect button",
	    "I don't care about the fact that I'm hacking I just care how you died in a block game",
	    "Your gaming chair expired mid-fight so that's how you lost %name%",
	    "You're so special that you can be the password requirement",
	    "%name% is the type of person who climbs over a glass wall to see what's on the other side",
	    "It's a bird! It's a plane! No it's your rank falling!",
	    "*yawn* I get so bored playing against you. That's okay though",
	    "Damn you have the awareness of a sloth",
	    "That aim is so trash I think the safest place to stand is in front of you",
	)

	private var target: EntityPlayer? = null

	@EventTarget
	fun onAttack(event: AttackEvent) {
		if (event.targetEntity is EntityPlayer) target = event.targetEntity
	}

	@EventTarget
	fun onUpdate(event: UpdateEvent) {
		if (target != null && target!!.isDead) {
			val message = when (modeValue.get()) {
				"LB-" -> {
					lbminusInsults[(Math.random() * lbminusInsults.size).toInt()].replace("%name%", target!!.getName())
				}
				"HeroMC" -> {
					heroMCInsults[(Math.random() * heroMCInsults.size).toInt()].replace("%name%", target!!.getName())
				}
				"Redesky" -> {
					redeskyInsults[(Math.random() * redeskyInsults.size).toInt()].replace("%name%", target!!.getName())
				}
				else -> " "
			}
			mc.thePlayer.sendChatMessage(message)
		}
	}

}