package com.ahmoneam.instabug.remote.parser

interface IHtmlParser {
    fun removeScriptAndStyleTags(html: String): String
    fun getOnlyBodyTagContent(html: String): String?
    fun parseBodyToText(html: String): String?
    fun parseBodyToOnlyWords(html: String): Sequence<String>
    fun parseBodyToWordsWithCount(html: String): Map<String, Int>
}