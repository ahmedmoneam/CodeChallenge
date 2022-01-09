package com.ahmoneam.instabug.remote.parser

import com.ahmoneam.instabug.core.di.SL
import com.ahmoneam.instabug.remote.di.RemoteDI
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Test

class HtmlParserTest {
    private val testData = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Document</title>
                    <script>
                        var x = "test"
                    </script>
                </head>
                <body>
                    <div>test1</div>
                    <div>for the parser</div>
                    <div>to check if its good</div>
                    <div>or not good</div>
                    <div>or not good at all</div>
                    <div>so lets check the parser with data</div>
                    <div>test1</div>
                    <div>notTest</div>
                    <div>WithTest</div>
                    
                     <script>
                        var anotherX = "test"
                    </script>
                    
                    <style>
                        css stuff
                    </style>
                    
                </body>
                </html>
            """.trimIndent()
    private lateinit var htmlParser: IHtmlParser

    @Before
    fun setUp() {
        RemoteDI.initHtmlParser(HtmlParser())
        htmlParser = SL[IHtmlParser::class.java]
    }

    @Test
    fun removeScriptAndStyleTags() {
        val result = htmlParser.removeScriptAndStyleTags(testData)
        assertThat(
            result, allOf(
                not(containsString("<script>")),
                not(containsString("</script>")),
                not(containsString("<style>")),
                not(containsString("</style>")),
            )
        )
    }

    @Test
    fun getOnlyBodyTagContent() {
        val result = htmlParser.getOnlyBodyTagContent(testData)
        assertThat(
            result, allOf(
                containsString("<div>test1</div>"),
                containsString("<div>for the parser</div>"),
                containsString("<div>to check if its good</div>"),
                containsString("<div>or not good</div>"),
                containsString("<div>or not good at all</div>"),
                containsString("<div>so lets check the parser with data</div>"),
                containsString("<div>test1</div>"),
                containsString("<div>notTest</div>"),
                containsString("<div>WithTest</div>"),
            )
        )
    }

    @Test
    fun parseBodyToText() {
        val result = htmlParser.parseBodyToText(testData)
        assertThat(
            result, allOf(
                containsString("test1"),
                containsString("for the parser"),
                containsString("to check if its good"),
                containsString("or not good"),
                containsString("or not good at all"),
                containsString("so lets check the parser with data"),
                containsString("test1"),
                containsString("notTest"),
                containsString("WithTest"),
                not(containsString("<div>")),
                not(containsString("</div>")),
            )
        )
    }

    @Test
    fun parseBodyToOnlyWords() {
        val result = htmlParser.parseBodyToOnlyWords(testData).toList()
        assertThat(result, hasItems("TEST", "FOR", "THE", "PARSER"))
        assertThat(result.size, equalTo(27))
    }

    @Test
    fun parseBodyToWordsWithCount() {
        val result = htmlParser.parseBodyToWordsWithCount(testData)
        assertThat(result["TEST"], equalTo(2))
        assertThat(result["PARSER"], equalTo(2))
        assertThat(result["GOOD"], equalTo(3))
        assertThat(result["DATA"], equalTo(1))
    }


}