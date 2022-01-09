package com.ahmoneam.instabug.core.di

import android.annotation.SuppressLint
import android.content.Context
import java.lang.ref.WeakReference
import java.util.*

@SuppressLint("StaticFieldLeak")
@SuppressWarnings("unused")
object SL {
    private val sServicesInstances: MutableMap<String, Any> = HashMap()
    private val sServicesImplementationsMapping: MutableMap<String, Class<*>> = HashMap()
    private val sServicesInstancesLock = Any()

    private lateinit var mContext: WeakReference<Context>

    fun init(context: Context) {
        mContext = WeakReference(context.applicationContext)
        sServicesInstances[Context::class.java.name] = context.applicationContext
    }

    /**
     * Return instance of desired class or object that implement desired interface.
     */
    operator fun <T> get(clazz: Class<T>): T = getService(clazz.name) as T

    /**
     * This method allows to bind a custom service implementation to an interface.
     *
     * @param interfaceClass      interface
     * @param implementationClass class which implement interface specified in first param
     */
    fun bindCustomServiceImplementation(interfaceClass: Class<*>, implementationClass: Class<*>) {
        synchronized(sServicesInstancesLock) {
            sServicesImplementationsMapping.put(
                interfaceClass.name,
                implementationClass
            )
        }
    }

    fun bindInstance(interfaceClass: Class<*>, any: Any) {
        synchronized(sServicesInstancesLock) {
            sServicesInstances.put(
                interfaceClass.name,
                any
            )
        }
    }

    private fun getService(name: String): Any {
        synchronized(sServicesInstancesLock) {
            val o = sServicesInstances[name]
            return o ?: try {
                val serviceInstance: Any
                val clazz: Class<*> = if (sServicesImplementationsMapping.containsKey(name)) {
                    sServicesImplementationsMapping[name]!!
                } else Class.forName(name)

                serviceInstance = try {
                    val constructor = clazz.constructors.first()
                    val params = constructor.parameterTypes
                    if (params.isEmpty()) constructor.newInstance()
                    else constructor.newInstance(*params.map { getService(it.name) }.toTypedArray())
                } catch (e: NoSuchMethodException) {
                    throw e
                }
                sServicesInstances[name] = serviceInstance
                serviceInstance
            } catch (e: ClassNotFoundException) {
                throw IllegalArgumentException(
                    "Requested service class was not found: $name", e
                )
            } catch (e: Exception) {
                throw IllegalArgumentException("Cannot initialize requested service: $name", e)
            }
        }
    }
}