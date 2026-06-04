package io.legado.app.help.http

import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import splitties.init.appCtx
import java.util.Stack

object WebViewPool {

    private val pool = Stack<PooledWebView>()
    private const val MAX_SIZE = 8
    private val cleanupHandler = Handler(Looper.getMainLooper())
    private val idleTimeout = 5 * 60 * 1000L

    private val cleanupTask = object : Runnable {
        override fun run() {
            synchronized(pool) {
                val now = System.currentTimeMillis()
                val iter = pool.iterator()
                while (iter.hasNext()) {
                    val pwv = iter.next()
                    if (now - pwv.idleSince > idleTimeout) {
                        pwv.webView.destroy()
                        iter.remove()
                    }
                }
            }
            cleanupHandler.postDelayed(this, 60_000L)
        }
    }

    init {
        cleanupHandler.postDelayed(cleanupTask, 60_000L)
    }

    fun checkout(): PooledWebView {
        synchronized(pool) {
            if (pool.isNotEmpty()) {
                return pool.pop().also { it.idleSince = 0L }
            }
        }
        return PooledWebView(createWebView())
    }

    fun checkin(pwv: PooledWebView) {
        synchronized(pool) {
            if (pool.size < MAX_SIZE) {
                pwv.idleSince = System.currentTimeMillis()
                pool.push(pwv)
            } else {
                pwv.webView.destroy()
            }
        }
    }

    fun clear() {
        synchronized(pool) {
            while (pool.isNotEmpty()) {
                pool.pop().webView.destroy()
            }
        }
        cleanupHandler.removeCallbacks(cleanupTask)
    }

    private fun createWebView(): WebView {
        val webView = WebView(appCtx)
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.blockNetworkImage = true
        settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        return webView
    }
}

class PooledWebView(val webView: WebView) {
    var idleSince: Long = 0L
}
