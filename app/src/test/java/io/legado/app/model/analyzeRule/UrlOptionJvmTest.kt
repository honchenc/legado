package io.legado.app.model.analyzeRule

import io.legado.app.utils.GSON
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class UrlOptionJvmTest {

    @Test
    fun parseDnsIp_fromJson() {
        val json = """{"dnsIp":"1.2.3.4","method":"POST"}"""
        val option = GSON.fromJson(json, AnalyzeUrl.UrlOption::class.java)
        assertEquals("1.2.3.4", option.getDnsIp())
        assertEquals("POST", option.getMethod())
    }

    @Test
    fun dnsIpAbsent_returnsNull() {
        val json = """{"method":"GET"}"""
        val option = GSON.fromJson(json, AnalyzeUrl.UrlOption::class.java)
        assertNull(option.getDnsIp())
        assertEquals("GET", option.getMethod())
    }

    @Test
    fun otherFields_unaffectedByDnsIp() {
        val json = """{"dnsIp":"192.168.1.1","charset":"utf-8","retry":3}"""
        val option = GSON.fromJson(json, AnalyzeUrl.UrlOption::class.java)
        assertEquals("192.168.1.1", option.getDnsIp())
        assertEquals("utf-8", option.getCharset())
        assertEquals(3, option.getRetry())
    }

    @Test
    fun emptyDnsIp_treatedAsNull() {
        val json = """{"dnsIp":""}"""
        val option = GSON.fromJson(json, AnalyzeUrl.UrlOption::class.java)
        assertNull(option.getDnsIp())
    }

    @Test
    fun dnsIp_ignoresExtraFieldsGracefully() {
        val json = """{"dnsIp":"10.0.0.1","unknownField":"value"}"""
        val option = GSON.fromJson(json, AnalyzeUrl.UrlOption::class.java)
        assertEquals("10.0.0.1", option.getDnsIp())
    }

    @Test
    fun setter_convertsBlankToNull() {
        val option = AnalyzeUrl.UrlOption()
        option.setDnsIp("  ")
        assertNull(option.getDnsIp())
    }

    @Test
    fun setter_acceptsValidIp() {
        val option = AnalyzeUrl.UrlOption()
        option.setDnsIp("10.0.0.1")
        assertEquals("10.0.0.1", option.getDnsIp())
    }

    @Test
    fun setter_convertsNull() {
        val option = AnalyzeUrl.UrlOption()
        option.setDnsIp(null)
        assertNull(option.getDnsIp())
    }

    @Test
    fun getDnsIp_afterConstruction_defaultIsNull() {
        val option = AnalyzeUrl.UrlOption()
        assertNull(option.getDnsIp())
    }

    @Test
    fun methodRoundTrip() {
        val option = AnalyzeUrl.UrlOption()
        assertNull(option.getMethod())
        option.setMethod("POST")
        assertEquals("POST", option.getMethod())
        option.setMethod(null)
        assertNull(option.getMethod())
    }
}
