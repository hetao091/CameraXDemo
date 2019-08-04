package com.github.cameraxdemo






/**
 * =====================================
 * author   : hetao
 * version  ：V1.0
 * time     ：2019-05-23 17:17
 * desc     ：
 * revise   :
 * =====================================
 */
class Test {
companion object{

    fun getString(list: List<Map<String, String>>): String {
        var str = ""
        // 遍历list
        for (map in list) {
            val entrySet = map.entries
            // 格式化map输出
            for ((key, value) in entrySet) {
                str += "$key=$value;"
            }
            str = str.substring(0, str.length - 1)
            str = "$str\\n"
        }

        str = str.substring(0, str.length - 2)
        return str
    }

    fun load(string: String): List<Map<String, String>> {
        val split = string.split("\\\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val list = ArrayList<Map<String,String>>()
        for (string2 in split) {
            val split2 = string2.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val map = HashMap<String,String>()
            for (string3 in split2) {
                val split3 = string3.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                map[split3[0]] = split3[1]
            }
            list.add(map)
        }
        return list
    }
}



}