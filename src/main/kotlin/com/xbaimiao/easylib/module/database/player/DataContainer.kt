package com.xbaimiao.easylib.module.database.player

import com.xbaimiao.easylib.module.utils.submit

/**
 * Zaphkiel
 * ink.ptms.zaphkiel.module.Vars
 *
 * @author sky
 * @since 2021/8/22 7:51 下午
 */
class DataContainer(val user: String, val database: Database) {

    private val source = database.getMap(user)

    operator fun set(key: String, value: Any) {
        source[key] = value.toString()
        save(key)
    }

    operator fun get(key: String): String? {
        return source[key]
    }

    fun keys(): Set<String> {
        return source.keys
    }

    fun values(): Map<String, String> {
        return source
    }

    fun size(): Int {
        return source.size
    }

    private fun save(key: String) {
        submit(async = true) { database[user, key] = source[key]!! }
    }

    override fun toString(): String {
        return "DataContainer(user='$user', source=$source)"
    }

}