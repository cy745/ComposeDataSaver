package com.funny.data_saver.core

import android.util.Log
import java.lang.StringBuilder

object DataSaverConverter {
    val typeSaveConverters: MutableMap<Class<*>, (Any?) -> String> by lazy(LazyThreadSafetyMode.PUBLICATION) { mutableMapOf() }
    val typeRestoreConverters: MutableMap<Class<*>, (String) -> Any?> by lazy(LazyThreadSafetyMode.PUBLICATION) { mutableMapOf() }

    /**
     * Use this function to convert your entity class into basic data type to store.
     * Check the example of this repository to see how to use it.
     * [Example](https://github.com/FunnySaltyFish/ComposeDataSaver/blob/master/app/src/main/java/com/funny/composedatasaver/ExampleActivity.kt)
     *
     * @param save Function1<T, Any>? save your entity bean into [String]
     * @param restore Function1<Any, T>? restore your entity bean from the saved [String] value
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any?> registerTypeConverters(
        noinline save: ((T) -> String)? = null,
        noinline restore: ((String) -> T)? = null
    ) {
        save?.let { typeSaveConverters[T::class.java] = it as (Any?) -> String }
        restore?.let { typeRestoreConverters[T::class.java] = it }
    }

    @Deprecated(
        "Use the function with both save and restore instead.",
        replaceWith = ReplaceWith("registerTypeConverters(noinline save: ((T)->String)? = null, noinline restore: ((String)->T)? = null)")
    )
    fun registerTypeConverters(clazz: Class<*>, save: (Any?) -> String) {
        typeSaveConverters[clazz] = save
    }

    fun convertListToString(list: List<*>): String {
        val sb = StringBuilder("[")
        for (each in list) {
            when (each) {
                is List<*> -> sb.append(convertListToString(each))
                null -> error("unable to save data: some part of list is null! ")
                else -> run {
                    val typeConverter = typeSaveConverters[each::class.java]
                    typeConverter ?: unsupportedType(each)
                    sb.append("${typeConverter(each)}${DataSaverConfig.LIST_SEPARATOR}")
                }
            }
        }
        if (sb.length > DataSaverConfig.LIST_SEPARATOR.length + 2) sb.delete(
            sb.length - DataSaverConfig.LIST_SEPARATOR.length,
            sb.length
        )
        sb.append("]")
        return sb.toString()
    }

    inline fun <reified T> convertStringToList(str: String): List<T> {
        if (str.length < 2) error("Invalid text($str), it should be like [a${DataSaverConfig.LIST_SEPARATOR}b${DataSaverConfig.LIST_SEPARATOR}c] instead.")
        val restoreConverter = typeRestoreConverters[T::class.java]
        restoreConverter
            ?: error("Unable to restore $str: ${T::class.java} is not supported, please call [registerTypeConverters] at first!")
        val s = str.substring(1, str.length - 1)
        return try {
            val arr = s.split(DataSaverConfig.LIST_SEPARATOR)
            arr.map {
                restoreConverter(it) as T
            }
        } catch (e: Exception) {
            Log.e("DataConverter", "error while parsing $str to list")
            e.printStackTrace()
            emptyList()
        }
    }

    private fun unsupportedType(data: Any?): Nothing =
        error("Unable to save data: type of $data (class: ${if (data == null)"null" else data::class.java} is not supported, please call [registerTypeConverters] at first!")

}

