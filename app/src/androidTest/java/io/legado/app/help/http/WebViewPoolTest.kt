package io.legado.app.help.http

import android.os.Handler
import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class WebViewPoolTest {

    private fun <T> onMain(action: () -> T): T {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return action()
        }
        val latch = CountDownLatch(1)
        var result: T? = null
        var error: Throwable? = null
        Handler(Looper.getMainLooper()).post {
            try {
                result = action()
            } catch (t: Throwable) {
                error = t
            } finally {
                latch.countDown()
            }
        }
        latch.await(5, TimeUnit.SECONDS)
        error?.let { throw it }
        return result!!
    }

    @Test
    fun checkout_returnsNonNullPooledWebView() {
        val pwv = onMain { WebViewPool.checkout() }
        assertNotNull(pwv)
        assertNotNull(pwv.webView)
        onMain { WebViewPool.checkin(pwv) }
    }

    @Test
    fun checkinThenCheckout_reusesInstance() {
        val first = onMain { WebViewPool.checkout() }
        onMain { WebViewPool.checkin(first) }
        val second = onMain { WebViewPool.checkout() }
        assertSame(first, second)
        onMain { WebViewPool.checkin(second) }
    }

    @Test
    fun multipleCheckout_returnsDistinctInstances() {
        val first = onMain { WebViewPool.checkout() }
        val second = onMain { WebViewPool.checkout() }
        assertNotSame(first, second)
        onMain { WebViewPool.checkin(first) }
        onMain { WebViewPool.checkin(second) }
    }

    @Test
    fun clear_destroysAllPooledInstances() {
        val pwv = onMain { WebViewPool.checkout() }
        onMain { WebViewPool.checkin(pwv) }
        onMain { WebViewPool.clear() }
    }
}
