package io.legado.app.ui.book.searchContent

object SearchUtil {
    fun searchPosition(content: String, pattern: String, useRegex: Boolean): List<Pair<Int, Int>> {
        if (pattern.isBlank()) return emptyList()
        val positions = mutableListOf<Pair<Int, Int>>()
        if (useRegex) {
            try {
                val regex = pattern.toRegex()
                for (match in regex.findAll(content)) {
                    positions.add(Pair(match.range.first, match.range.last + 1))
                }
            } catch (_: Exception) {
                var index = content.indexOf(pattern)
                while (index >= 0) {
                    positions.add(Pair(index, index + pattern.length))
                    index = content.indexOf(pattern, index + pattern.length)
                }
            }
        } else {
            var index = content.indexOf(pattern)
            while (index >= 0) {
                positions.add(Pair(index, index + pattern.length))
                index = content.indexOf(pattern, index + pattern.length)
            }
        }
        return positions
    }
}
