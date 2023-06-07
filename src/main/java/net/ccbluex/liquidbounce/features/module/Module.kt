/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.features.module

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.features.module.modules.misc.AutoDisable.DisableEvent
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.ColorUtils.stripColor
import net.ccbluex.liquidbounce.value.Value
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

open class Module : MinecraftInstance(), Listenable {

    // Module information
    // TODO: Remove ModuleInfo and change to constructor (#Kotlin)
    var name: String
    var spacedName: String
    var description: String
    var category: ModuleCategory
    var keyBind = Keyboard.CHAR_NONE
        set(keyBind) {
            field = keyBind

            if (!LiquidBounce.isStarting)
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.modulesConfig)
        }
    var array = true
        set(array) {
            field = array

            if (!LiquidBounce.isStarting)
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.modulesConfig)
        }
    private val canEnable: Boolean
    private val onlyEnable: Boolean
    private val forceNoSound: Boolean

    var slideStep = 0F
    var animation = 0F
    var autoDisables = mutableListOf<DisableEvent>()

    init {
        val moduleInfo = javaClass.getAnnotation(ModuleInfo::class.java)!!

        name = moduleInfo.name
        spacedName = if (moduleInfo.spacedName == "") name else moduleInfo.spacedName
        description = moduleInfo.description
        category = moduleInfo.category
        keyBind = moduleInfo.keyBind
        array = moduleInfo.array
        canEnable = moduleInfo.canEnable
        onlyEnable = moduleInfo.onlyEnable
        forceNoSound = moduleInfo.forceNoSound
    }

    // Current state of module
    var state = false
        set(value) {
            if (field == value || !canEnable) return

            // Call toggle
            onToggle(value)

            // Play sound and add notification
            if (!LiquidBounce.isStarting && !forceNoSound) {
                when (LiquidBounce.moduleManager.toggleSoundMode) {
                    1 -> mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("random.click"),
                        1F))
                    2 -> (if (value) LiquidBounce.tipSoundManager.enableSound else LiquidBounce.tipSoundManager.disableSound).asyncPlay(LiquidBounce.moduleManager.toggleVolume)
                }
                if (LiquidBounce.moduleManager.shouldNotify)
                    LiquidBounce.hud.addNotification(Notification("${if (value) "Enabled" else "Disabled"} §r$name", if (value) Notification.Type.SUCCESS else Notification.Type.ERROR, 1000L))
            }

            // Call on enabled or disabled
            if (value) {
                onEnable()

                if (!onlyEnable)
                    field = true
            } else {
                onDisable()
                field = false
            }

            // Save module state
            LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.modulesConfig)
        }


    // HUD
    val hue = Math.random().toFloat()
    var slide = 0F
    var arrayY = 0F

    // Tag
    open val tag: String?
        get() = null
/*
    val tagName: String
        get() = "$name${if (tag == null) "" else "§7 - $tag"}"

    val colorlessTagName: String
        get() = "$name${if (tag == null) "" else " - " + stripColor(tag)}"
*/
    /**
     * Toggle module
     */
    fun toggle() {
        state = !state
    }

    /**
     * Print [msg] to chat
     */
    protected fun chat(msg: String) = ClientUtils.displayChatMessage("§8[§9§l${LiquidBounce.CLIENT_NAME}§8] §3$msg")

    /**
     * Called when module toggled
     */
    open fun onToggle(state: Boolean) {}

    /**
     * Called when module enabled
     */
    open fun onEnable() {}

    /**
     * Called when module disabled
     */
    open fun onDisable() {}

    /**
     * Called when module initialized
     */
    open fun onInitialize() {}

    /**
     * Get module by [valueName]
     */
    open fun getValue(valueName: String) = values.find { it.name.equals(valueName, ignoreCase = true) }

    /**
     * Get all values of module
     */
    open val values: List<Value<*>>
        get() = javaClass.declaredFields.map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>()

    /**
     * Events should be handled when module is enabled
     */
    override fun handleEvents() = state
}
