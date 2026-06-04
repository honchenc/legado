package io.legado.app.ui.book.searchContent

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchResultTest {

    @Test
    fun plainQuery_highlightsCorrectly() {
        val result = SearchResult(
            resultText = "这是包含关键词的句子",
            query = "关键词",
            chapterTitle = "第一章",
            queryIndexInResult = 2
        )
        val spanned = result.getHtmlCompat("000000", "FF0000")
        assertNotNull(spanned)
        assertTrue(spanned.length > 0)
    }

    @Test
    fun regexMatchedText_highlightsCorrectly() {
        val result = SearchResult(
            resultText = "价格是123元",
            query = "\\d+",
            matchedText = "123",
            chapterTitle = "第一章",
            queryIndexInResult = 3,
            queryIndexInChapter = 3
        )
        val spanned = result.getHtmlCompat("000000", "FF0000")
        assertNotNull(spanned)
    }

    @Test
    fun matchedTextNull_fallsBackToQuery() {
        val result = SearchResult(
            resultText = "这是一个测试",
            query = "测试",
            matchedText = null,
            chapterTitle = "第一章"
        )
        val spanned = result.getHtmlCompat("000000", "FF0000")
        assertNotNull(spanned)
    }

    @Test
    fun emptyQuery_returnsNonNull() {
        val result = SearchResult(
            resultText = "没有搜索",
            query = ""
        )
        val spanned = result.getHtmlCompat("000000", "FF0000")
        assertNotNull(spanned)
    }

    @Test
    fun queryFound_returnsNonNull() {
        val result = SearchResult(
            resultText = "这里有一个缺失词语",
            query = "缺失",
            chapterTitle = "第一章",
            queryIndexInResult = 3
        )
        val spanned = result.getHtmlCompat("000000", "FF0000")
        assertNotNull(spanned)
    }
}
