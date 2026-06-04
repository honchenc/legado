package io.legado.app.ui.book.searchContent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchUtilTest {

    @Test
    fun `plain search finds all occurrences`() {
        val content = "hello world hello"
        val result = SearchUtil.searchPosition(content, "hello", false)
        assertEquals(2, result.size)
        assertEquals(Pair(0, 5), result[0])
        assertEquals(Pair(12, 17), result[1])
    }

    @Test
    fun `regex search finds matches`() {
        val content = "abc 123 def 456"
        val result = SearchUtil.searchPosition(content, "\\d+", true)
        assertEquals(2, result.size)
        assertEquals(Pair(4, 7), result[0])
        assertEquals(Pair(12, 15), result[1])
    }

    @Test
    fun `invalid regex falls back to plain search`() {
        val content = "test (abc) test"
        val result = SearchUtil.searchPosition(content, "test", true)
        assertEquals(2, result.size)
    }

    @Test
    fun `truly invalid regex falls back to plain search`() {
        val content = "test\\test"
        val result = SearchUtil.searchPosition(content, "\\", true)
        assertEquals(1, result.size)
    }

    @Test
    fun `no match returns empty list`() {
        val content = "xyz"
        val result = SearchUtil.searchPosition(content, "abc", false)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `empty content returns empty list`() {
        val result = SearchUtil.searchPosition("", "test", false)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `empty query returns empty list`() {
        val result = SearchUtil.searchPosition("some content", "", false)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `blank query returns empty list`() {
        val result = SearchUtil.searchPosition("some content", "   ", false)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `regex with alternation matches correctly`() {
        val content = "cat dog bird"
        val result = SearchUtil.searchPosition(content, "cat|bird", true)
        assertEquals(2, result.size)
        assertEquals(Pair(0, 3), result[0])
        assertEquals(Pair(8, 12), result[1])
    }

    @Test
    fun `overlapping regex does not double count`() {
        val content = "aaaa"
        val result = SearchUtil.searchPosition(content, "aa", true)
        assertEquals(2, result.size)
        assertEquals(Pair(0, 2), result[0])
        assertEquals(Pair(2, 4), result[1])
    }

    @Test
    fun `unicode content plain search`() {
        val content = "这是一段中文测试文本"
        val result = SearchUtil.searchPosition(content, "中文", false)
        assertEquals(1, result.size)
        assertEquals(Pair(4, 6), result[0])
    }

    @Test
    fun `unicode content regex search`() {
        val content = "价格是123元"
        val result = SearchUtil.searchPosition(content, "\\d+", true)
        assertEquals(1, result.size)
        assertEquals(Pair(3, 6), result[0])
    }

    @Test
    fun `search with special regex chars treated as plain`() {
        val content = "price is $10.00"
        val result = SearchUtil.searchPosition(content, "$10.00", false)
        assertEquals(1, result.size)
        assertEquals(Pair(9, 15), result[0])
    }

    @Test
    fun `multiple lines plain search`() {
        val content = "line1\nline2\nline1"
        val result = SearchUtil.searchPosition(content, "line1", false)
        assertEquals(2, result.size)
        assertEquals(Pair(0, 5), result[0])
        assertEquals(Pair(12, 17), result[1])
    }

    @Test
    fun `case sensitive search`() {
        val content = "Hello HELLO hello"
        val result = SearchUtil.searchPosition(content, "hello", false)
        assertEquals(1, result.size)
    }

    @Test
    fun `regex case sensitive`() {
        val content = "Hello HELLO hello"
        val result = SearchUtil.searchPosition(content, "hello", true)
        assertEquals(1, result.size)
    }
}
