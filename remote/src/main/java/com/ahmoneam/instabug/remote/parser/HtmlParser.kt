package com.ahmoneam.instabug.remote.parser

class HtmlParser : IHtmlParser {
    private val scriptAndStyleTagsRegex =
        "<(script|style)[^>]*>(?<content>[^<]*)</(script|style)[^>]*>".toRegex()
    private val onlyTextRegex = "[^A-Za-z]".toRegex()
    private val tagRegex = "<[^>]*>".toRegex()
    private val spacesRegex = "\\s+".toRegex()

    override fun removeScriptAndStyleTags(html: String): String {
        return html.replace(scriptAndStyleTagsRegex, "")
    }

    override fun getOnlyBodyTagContent(html: String): String? {
        val body = removeScriptAndStyleTags(html)

        val startTag = "<body>"
        val endTag = "</body>"
        val indexOfStartTag = body.indexOf(startTag)
        val indexOfEndTag = body.indexOf(endTag)

        val startIndex = indexOfStartTag + startTag.length

        return if (indexOfStartTag != -1 && indexOfEndTag != -1 && indexOfEndTag > startIndex)
            body.substring(startIndex, indexOfEndTag)
        else null
    }

    override fun parseBodyToText(html: String): String? {
        return getOnlyBodyTagContent(html)?.replace(tagRegex, " ")
    }

    override fun parseBodyToOnlyWords(html: String): Sequence<String> {
        return parseBodyToText(html)?.let { s ->
            s.splitToSequence(spacesRegex)
                .filterNot { it.contains(">") || it.contains("<") }
                .map { it.replace(onlyTextRegex, "") }
                .filterNot { it.isEmpty() }
        } ?: emptySequence()
    }

    override fun parseBodyToWordsWithCount(html: String): Map<String, Int> {
        return parseBodyToOnlyWords(html)
            .groupingBy { it }
            .eachCount()
    }
}