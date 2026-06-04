package io.legado.app.ui.book.source.manage

import io.legado.app.data.entities.BookSource
import io.legado.app.data.entities.BookSourcePart
import io.legado.app.utils.cnCompare

object SourceSorter {

    fun sort(
        data: List<BookSourcePart>,
        mode: BookSourceSort,
        ascending: Boolean
    ): List<BookSourcePart> {
        val result = when (mode) {
            BookSourceSort.Default -> data.sortedBy { it.customOrder }
            BookSourceSort.Name -> data.sortedWith { a, b ->
                a.bookSourceName.cnCompare(b.bookSourceName)
            }
            BookSourceSort.Url -> data.sortedBy { it.bookSourceUrl }
            BookSourceSort.Weight -> data.sortedBy { it.weight }
            BookSourceSort.Update -> data.sortedByDescending { it.lastUpdateTime }
            BookSourceSort.Respond -> data.sortedBy { it.respondTime }
            BookSourceSort.Enable -> data.sortedWith { a, b ->
                var r = -a.enabled.compareTo(b.enabled)
                if (r == 0) r = a.bookSourceName.cnCompare(b.bookSourceName)
                r
            }
        }
        return if (ascending) result else result.reversed()
    }

    @JvmName("sortSources")
    fun sort(
        data: List<BookSource>,
        mode: BookSourceSort,
        ascending: Boolean
    ): List<BookSource> {
        val result = when (mode) {
            BookSourceSort.Default -> data.sortedBy { it.customOrder }
            BookSourceSort.Name -> data.sortedWith { a, b ->
                a.bookSourceName.cnCompare(b.bookSourceName)
            }
            BookSourceSort.Url -> data.sortedBy { it.bookSourceUrl }
            BookSourceSort.Weight -> data.sortedBy { it.weight }
            BookSourceSort.Update -> data.sortedByDescending { it.lastUpdateTime }
            BookSourceSort.Respond -> data.sortedBy { it.respondTime }
            BookSourceSort.Enable -> data.sortedWith { a, b ->
                var r = -a.enabled.compareTo(b.enabled)
                if (r == 0) r = a.bookSourceName.cnCompare(b.bookSourceName)
                r
            }
        }
        return if (ascending) result else result.reversed()
    }
}
