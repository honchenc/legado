package io.legado.app.ui.book.source.manage

import io.legado.app.data.entities.BookSource
import io.legado.app.data.entities.BookSourcePart
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SourceSorterTest {

    private val a = BookSourcePart(
        bookSourceUrl = "http://a.com", bookSourceName = "A源",
        lastUpdateTime = 100, respondTime = 50, weight = 5, customOrder = 3
    )
    private val b = BookSourcePart(
        bookSourceUrl = "http://b.com", bookSourceName = "B源",
        lastUpdateTime = 200, respondTime = 150, weight = 3, customOrder = 1
    )
    private val c = BookSourcePart(
        bookSourceUrl = "http://c.com", bookSourceName = "C源",
        lastUpdateTime = 300, respondTime = 250, weight = 1, customOrder = 2
    )

    @Test
    fun `default sort preserves insertion order`() {
        val items = listOf(a, b, c)
        val result = SourceSorter.sort(items, BookSourceSort.Default, true)
        // Default sorts by customOrder: b(1), c(2), a(3)
        assertEquals("http://b.com", result[0].bookSourceUrl)
        assertEquals("http://c.com", result[1].bookSourceUrl)
        assertEquals("http://a.com", result[2].bookSourceUrl)
    }

    @Test
    fun `default sort all null customOrder keeps original order`() {
        val x = BookSourcePart(bookSourceUrl = "http://x.com")
        val y = BookSourcePart(bookSourceUrl = "http://y.com")
        val items = listOf(x, y)
        val result = SourceSorter.sort(items, BookSourceSort.Default, true)
        assertEquals("http://x.com", result[0].bookSourceUrl)
        assertEquals("http://y.com", result[1].bookSourceUrl)
    }

    @Test
    fun `default sort zero vs default customOrder is stable`() {
        val x = BookSourcePart(bookSourceUrl = "http://x.com", customOrder = 0)
        val y = BookSourcePart(bookSourceUrl = "http://y.com")
        // Both have customOrder=0 (default), so original order is preserved
        val items = listOf(y, x)
        val result = SourceSorter.sort(items, BookSourceSort.Default, true)
        assertEquals("http://y.com", result[0].bookSourceUrl)
        assertEquals("http://x.com", result[1].bookSourceUrl)
    }

    @Test
    fun `default sort descending reverses customOrder`() {
        val items = listOf(a, b, c)
        val result = SourceSorter.sort(items, BookSourceSort.Default, false)
        assertEquals("http://a.com", result[0].bookSourceUrl)
        assertEquals("http://c.com", result[1].bookSourceUrl)
        assertEquals("http://b.com", result[2].bookSourceUrl)
    }

    @Test
    fun `default sort null customOrder descending`() {
        val x = BookSourcePart(bookSourceUrl = "http://x.com")
        val y = BookSourcePart(bookSourceUrl = "http://y.com", customOrder = 5)
        val items = listOf(x, y)
        val result = SourceSorter.sort(items, BookSourceSort.Default, false)
        assertEquals("http://y.com", result[0].bookSourceUrl)
        assertEquals("http://x.com", result[1].bookSourceUrl)
    }

    @Test
    fun `sort by name ascending`() {
        val items = listOf(c, a, b)
        val result = SourceSorter.sort(items, BookSourceSort.Name, true)
        assertEquals("A源", result[0].bookSourceName)
        assertEquals("B源", result[1].bookSourceName)
        assertEquals("C源", result[2].bookSourceName)
    }

    @Test
    fun `sort by name descending`() {
        val items = listOf(a, b, c)
        val result = SourceSorter.sort(items, BookSourceSort.Name, false)
        assertEquals("C源", result[0].bookSourceName)
        assertEquals("B源", result[1].bookSourceName)
        assertEquals("A源", result[2].bookSourceName)
    }

    @Test
    fun `sort by name empty name sorts before non-empty ascending`() {
        val named = BookSourcePart(bookSourceName = "Z", bookSourceUrl = "http://z.com")
        val unnamed = BookSourcePart(bookSourceUrl = "http://n.com")
        val items = listOf(named, unnamed)
        val result = SourceSorter.sort(items, BookSourceSort.Name, true)
        // Empty string collates before any non-empty string
        assertEquals("", result[0].bookSourceName)
        assertEquals("Z", result[1].bookSourceName)
    }

    @Test
    fun `sort by name empty name sorts before non-empty descending`() {
        val named = BookSourcePart(bookSourceName = "A", bookSourceUrl = "http://a.com")
        val unnamed = BookSourcePart(bookSourceUrl = "http://n.com")
        val items = listOf(named, unnamed)
        val result = SourceSorter.sort(items, BookSourceSort.Name, false)
        // Empty string collates before "A" even in descending (just reversed)
        assertEquals("A", result[0].bookSourceName)
        assertEquals("", result[1].bookSourceName)
    }

    @Test
    fun `sort by url ascending`() {
        val items = listOf(c, a, b)
        val result = SourceSorter.sort(items, BookSourceSort.Url, true)
        assertEquals("http://a.com", result[0].bookSourceUrl)
        assertEquals("http://b.com", result[1].bookSourceUrl)
        assertEquals("http://c.com", result[2].bookSourceUrl)
    }

    @Test
    fun `sort by url empty string sorted as empty`() {
        val hasUrl = BookSourcePart(bookSourceUrl = "http://z.com", bookSourceName = "Z")
        val emptyUrl = BookSourcePart(bookSourceName = "Empty")
        val items = listOf(hasUrl, emptyUrl)
        val result = SourceSorter.sort(items, BookSourceSort.Url, true)
        assertEquals("", result[0].bookSourceUrl)
        assertEquals("http://z.com", result[1].bookSourceUrl)
    }

    @Test
    fun `sort by weight ascending`() {
        val items = listOf(a, b, c)
        val result = SourceSorter.sort(items, BookSourceSort.Weight, true)
        assertTrue(result[0].weight <= result[1].weight)
        assertTrue(result[1].weight <= result[2].weight)
    }

    @Test
    fun `sort by weight equal values is stable`() {
        val x = BookSourcePart(bookSourceUrl = "http://x.com", bookSourceName = "X", weight = 1)
        val y = BookSourcePart(bookSourceUrl = "http://y.com", bookSourceName = "Y", weight = 1)
        val items = listOf(x, y)
        val result = SourceSorter.sort(items, BookSourceSort.Weight, true)
        assertEquals("X", result[0].bookSourceName)
        assertEquals("Y", result[1].bookSourceName)
    }

    @Test
    fun `sort by update time`() {
        val items = listOf(a, b, c)
        // Update mode sorts descending (latest first) when ascending=true
        val result = SourceSorter.sort(items, BookSourceSort.Update, true)
        assertTrue(result[0].lastUpdateTime >= result[1].lastUpdateTime)
        assertTrue(result[1].lastUpdateTime >= result[2].lastUpdateTime)
    }

    @Test
    fun `sort by update time descending`() {
        val items = listOf(a, b, c)
        val result = SourceSorter.sort(items, BookSourceSort.Update, false)
        assertTrue(result[0].lastUpdateTime <= result[1].lastUpdateTime)
        assertTrue(result[1].lastUpdateTime <= result[2].lastUpdateTime)
    }

    @Test
    fun `sort by update time equal values`() {
        val x = BookSourcePart(bookSourceUrl = "http://x.com", bookSourceName = "X", lastUpdateTime = 100)
        val y = BookSourcePart(bookSourceUrl = "http://y.com", bookSourceName = "Y", lastUpdateTime = 100)
        val items = listOf(x, y)
        val result = SourceSorter.sort(items, BookSourceSort.Update, true)
        assertEquals("X", result[0].bookSourceName)
        assertEquals("Y", result[1].bookSourceName)
    }

    @Test
    fun `sort by respond time ascending`() {
        val items = listOf(c, a, b)
        val result = SourceSorter.sort(items, BookSourceSort.Respond, true)
        assertEquals(50, result[0].respondTime)
        assertEquals(150, result[1].respondTime)
        assertEquals(250, result[2].respondTime)
    }

    @Test
    fun `sort by respond time equal values`() {
        val x = BookSourcePart(bookSourceUrl = "http://x.com", bookSourceName = "X", respondTime = 100)
        val y = BookSourcePart(bookSourceUrl = "http://y.com", bookSourceName = "Y", respondTime = 100)
        val items = listOf(y, x)
        val result = SourceSorter.sort(items, BookSourceSort.Respond, true)
        assertEquals("Y", result[0].bookSourceName)
        assertEquals("X", result[1].bookSourceName)
    }

    @Test
    fun `chinese pinyin ordering`() {
        val 湖北 = BookSourcePart(bookSourceName = "湖北", bookSourceUrl = "http://hb.com")
        val 湖南 = BookSourcePart(bookSourceName = "湖南", bookSourceUrl = "http://hn.com")
        val 广东 = BookSourcePart(bookSourceName = "广东", bookSourceUrl = "http://gd.com")
        val items = listOf(湖南, 湖北, 广东)
        val result = SourceSorter.sort(items, BookSourceSort.Name, true)
        assertEquals("广东", result[0].bookSourceName)
        assertEquals("湖北", result[1].bookSourceName)
        assertEquals("湖南", result[2].bookSourceName)
    }

    @Test
    fun `empty list does not crash`() {
        val items = emptyList<BookSourcePart>()
        for (mode in BookSourceSort.entries) {
            val result = SourceSorter.sort(items, mode, true)
            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `BookSource overload works identically`() {
        val items = listOf(
            BookSource(bookSourceUrl = "http://b.com", bookSourceName = "B源"),
            BookSource(bookSourceUrl = "http://a.com", bookSourceName = "A源"),
        )
        val result = SourceSorter.sort(items, BookSourceSort.Name, true)
        assertEquals("A源", result[0].bookSourceName)
    }

    @Test
    fun `BookSource overload descending`() {
        val items = listOf(
            BookSource(bookSourceUrl = "http://a.com", bookSourceName = "A源"),
            BookSource(bookSourceUrl = "http://b.com", bookSourceName = "B源"),
        )
        val result = SourceSorter.sort(items, BookSourceSort.Name, false)
        assertEquals("B源", result[0].bookSourceName)
    }

    @Test
    fun `enable sort puts enabled before disabled`() {
        val enabled = BookSourcePart(bookSourceName = "启用", bookSourceUrl = "http://e.com", enabled = true)
        val disabled = BookSourcePart(bookSourceName = "禁用", bookSourceUrl = "http://d.com", enabled = false)
        val items = listOf(disabled, enabled)
        val result = SourceSorter.sort(items, BookSourceSort.Enable, true)
        assertTrue(result[0].enabled)
        assertTrue(!result[1].enabled)
    }

    @Test
    fun `enable sort all enabled stable by name`() {
        val z = BookSourcePart(bookSourceName = "Z源", bookSourceUrl = "http://z.com", enabled = true)
        val a = BookSourcePart(bookSourceName = "A源", bookSourceUrl = "http://a.com", enabled = true)
        val items = listOf(z, a)
        val result = SourceSorter.sort(items, BookSourceSort.Enable, true)
        assertEquals("A源", result[0].bookSourceName)
        assertEquals("Z源", result[1].bookSourceName)
    }

    @Test
    fun `enable sort all disabled stable by name`() {
        val z = BookSourcePart(bookSourceName = "Z源", bookSourceUrl = "http://z.com", enabled = false)
        val a = BookSourcePart(bookSourceName = "A源", bookSourceUrl = "http://a.com", enabled = false)
        val items = listOf(z, a)
        val result = SourceSorter.sort(items, BookSourceSort.Enable, true)
        assertEquals("A源", result[0].bookSourceName)
        assertEquals("Z源", result[1].bookSourceName)
    }

    @Test
    fun `sort by weight zero values`() {
        val zero = BookSourcePart(bookSourceUrl = "http://0.com", weight = 0)
        val pos = BookSourcePart(bookSourceUrl = "http://1.com", weight = 1)
        val neg = BookSourcePart(bookSourceUrl = "http://-1.com", weight = -1)
        val items = listOf(zero, pos, neg)
        val result = SourceSorter.sort(items, BookSourceSort.Weight, true)
        assertEquals(-1, result[0].weight)
        assertEquals(0, result[1].weight)
        assertEquals(1, result[2].weight)
    }
}
