package io.legado.app.ui.book.searchContent


import android.app.Application
import io.legado.app.base.BaseViewModel
import io.legado.app.data.entities.Book
import io.legado.app.data.entities.BookChapter
import io.legado.app.help.IntentData
import io.legado.app.help.book.BookHelp
import io.legado.app.help.book.ContentProcessor
import io.legado.app.help.config.AppConfig
import io.legado.app.utils.ChineseUtils
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive


class SearchContentViewModel(application: Application) : BaseViewModel(application) {
    var bookUrl: String = ""
    var book: Book? = null
    private var contentProcessor: ContentProcessor? = null
    var lastQuery: String = ""
    var searchResultCounts = 0
    val cacheChapterNames = hashSetOf<String>()
    val searchResultList: MutableList<SearchResult> = mutableListOf()
    var replaceEnabled = false
    var useRegex = false

    fun initBook(success: () -> Unit) {
        execute {
            book = IntentData.book as Book
            book?.let {
                bookUrl = it.bookUrl
                contentProcessor = ContentProcessor.get(it.name, it.origin)
            }
        }.onSuccess {
            success.invoke()
        }
    }

    suspend fun searchChapter(
        query: String,
        chapter: BookChapter
    ): List<SearchResult> {
        val searchResultsWithinChapter: MutableList<SearchResult> = mutableListOf()
        val book = book ?: return searchResultsWithinChapter
        val chapterContent = BookHelp.getContent(book, chapter) ?: return searchResultsWithinChapter
        currentCoroutineContext().ensureActive()
        chapter.title = when (AppConfig.chineseConverterType) {
            1 -> ChineseUtils.t2s(chapter.title)
            2 -> ChineseUtils.s2t(chapter.title)
            else -> chapter.title
        }
        currentCoroutineContext().ensureActive()
        val mContent = contentProcessor!!.getContent(
            book, chapter, chapterContent, useReplace = replaceEnabled
        ).toString()
        val positions = SearchUtil.searchPosition(mContent, query, useRegex)
        positions.forEachIndexed { index, (startPos, endPos) ->
            currentCoroutineContext().ensureActive()
            val matchedText = if (useRegex) {
                mContent.substring(startPos, endPos)
            } else {
                query
            }
            val construct = getResultAndQueryIndex(mContent, startPos, matchedText)
            val result = SearchResult(
                resultCountWithinChapter = index,
                resultText = construct.second,
                chapterTitle = chapter.title,
                query = query,
                matchedText = matchedText,
                chapterIndex = chapter.index,
                queryIndexInResult = construct.first,
                queryIndexInChapter = startPos
            )
            searchResultsWithinChapter.add(result)
        }
        searchResultCounts += searchResultsWithinChapter.size
        return searchResultsWithinChapter
    }

    private fun getResultAndQueryIndex(
        content: String,
        queryIndexInContent: Int,
        matchedText: String
    ): Pair<Int, String> {
        // 左右移动20个字符，构建关键词周边文字，在搜索结果里显示
        // 判断段落，只在关键词所在段落内分割
        // 利用标点符号分割完整的句
        // length和设置结合，自由调整周边文字长度
        val length = 20
        var po1 = queryIndexInContent - length
        var po2 = queryIndexInContent + matchedText.length + length
        if (po1 < 0) {
            po1 = 0
        }
        if (po2 > content.length) {
            po2 = content.length
        }
        val queryIndexInResult = queryIndexInContent - po1
        val newText = content.substring(po1, po2)
        return queryIndexInResult to newText
    }

}