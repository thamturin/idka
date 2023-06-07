package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.value.Value
import org.apache.logging.log4j.core.config.plugins.ResolverUtil
import java.lang.reflect.Modifier

object ClassUtils {

    private val cachedClasses = mutableMapOf<String, Boolean>()

    fun hasClass(className: String): Boolean {
        return if (cachedClasses.containsKey(className)) {
            cachedClasses[className]!!
        } else try {
            Class.forName(className)
            cachedClasses[className] = true

            true
        } catch (e: ClassNotFoundException) {
            cachedClasses[className] = false

            false
        }
    }

    fun getObjectInstance(clazz: Class<*>): Any {
        clazz.declaredFields.forEach {
            if (it.name.equals("INSTANCE")) {
                return it.get(null)
            }
        }
        throw IllegalAccessException("This class not a kotlin object")
    }

    fun getValues(clazz: Class<*>, instance: Any) = clazz.declaredFields.map { valueField ->
        valueField.isAccessible = true
        valueField[instance]
    }.filterIsInstance<Value<*>>()


    fun <T : Any> resolvePackage(packagePath: String, klass: Class<T>): List<Class<out T>> {

        val resolver = ResolverUtil()

        resolver.classLoader = klass.classLoader

        resolver.findInPackage(object : ResolverUtil.ClassTest() {
            override fun matches(type: Class<*>): Boolean {
                return true
            }
        }, packagePath)

        val list = mutableListOf<Class<out T>>()

        for(resolved in resolver.classes) {
            resolved.declaredMethods.find {
                Modifier.isNative(it.modifiers)
            }?.let {
                val klass1 = it.declaringClass.typeName+"."+it.name
                throw UnsatisfiedLinkError(klass1+"\n\tat ${klass1}(Native Method)") // we don't want native methods
            }
            if(klass.isAssignableFrom(resolved) && !resolved.isInterface && !Modifier.isAbstract(resolved.modifiers)) {
                list.add(resolved as Class<out T>)
            }
        }

        return list
    }
    fun hasForge() = hasClass("net.minecraftforge.common.MinecraftForge")
}