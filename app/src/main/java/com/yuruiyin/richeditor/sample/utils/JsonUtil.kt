package com.yuruiyin.richeditor.sample.utils

import java.lang.StringBuilder

/**
 * Title: json工具类（包括json格式化）
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-14
 */
class JsonUtil {

    companion object {

        /**
         * 获取格式化后的json字符串
         */
        fun getFormatJson(json: String): String {
            val jsonFormat = StringBuilder()
            var tabNum = 0
            json.forEachIndexed { index, c ->
                if (c == '{') {
                    tabNum++
                    jsonFormat.append(c + "\n")
                    jsonFormat.append(getSpaceOrTab(tabNum))
                } else if (c == '}') {
                    tabNum--
                    jsonFormat.append("\n")
                    jsonFormat.append(getSpaceOrTab(tabNum))
                    jsonFormat.append(c)
                } else if (c == ',') {
                    jsonFormat.append(c + "\n")
                    jsonFormat.append(getSpaceOrTab(tabNum))
                } else if (c == ':') {
                    jsonFormat.append("$c ")
                } else if (c == '[') {
                    tabNum++
                    val nextChar = json[index + 1]
                    if (nextChar == ']') {
                        jsonFormat.append(c)
                    } else {
                        jsonFormat.append(c + "\n")
                        jsonFormat.append(getSpaceOrTab(tabNum))
                    }
                } else if (c == ']') {
                    tabNum--
                    val lastChar = json[index - 1]
                    if (lastChar == '[') {
                        jsonFormat.append(c)
                    } else {
                        jsonFormat.append("\n")
                        jsonFormat.append(getSpaceOrTab(tabNum))
                        jsonFormat.append(c)
                    }
                } else {
                    jsonFormat.append(c)
                }
            }

            return jsonFormat.toString()
        }

        private fun getSpaceOrTab(tabNum: Int): String {
            val sbTab = StringBuilder()
            for (i in 0 until tabNum) {
                sbTab.append('\t')
            }
            return sbTab.toString()
        }

    }

}