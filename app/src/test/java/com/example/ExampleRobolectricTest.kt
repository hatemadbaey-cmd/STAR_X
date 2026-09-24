package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DeviceEntity
import com.example.domain.model.DeviceStatus
import com.example.domain.model.DeviceType
import com.example.domain.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `test app name Arabic string resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("نظام البث", appName)
    }

    @Test
    fun `test device type and status domain models`() {
        assertEquals(DeviceType.BROADCAST, DeviceType.fromString("BROADCAST"))
        assertEquals(DeviceType.RECEIVER, DeviceType.fromString("RECEIVER"))
        assertEquals(DeviceType.MODEM, DeviceType.fromString("MODEM"))

        assertEquals(DeviceStatus.ONLINE, DeviceStatus.fromString("ONLINE"))
        assertEquals(DeviceStatus.OFFLINE, DeviceStatus.fromString("OFFLINE"))
        assertEquals(DeviceStatus.MAINTENANCE, DeviceStatus.fromString("MAINTENANCE"))
        assertEquals(DeviceStatus.IN_HOUSE, DeviceStatus.fromString("IN_HOUSE"))
    }

    @Test
    fun `test user role enum mappings`() {
        assertEquals(UserRole.ADMIN, UserRole.fromString("ADMIN"))
        assertEquals(UserRole.USER, UserRole.fromString("USER"))
        assertEquals("مدير النظام", UserRole.ADMIN.titleAr)
        assertEquals("فني شبكة", UserRole.USER.titleAr)
    }

    @Test
    fun `test device entity to domain transformation`() {
        val entity = DeviceEntity(
            id = 10L,
            modemName = "Rocket M5 Main Tower",
            ip = "192.168.1.50",
            type = "BROADCAST",
            status = "ONLINE",
            location = "Main Tower East",
            notes = "Omni Antenna 15dBi",
            lastPingMs = 12L,
            lastPingSuccess = true
        )

        val domain = entity.toDomain()
        assertEquals(10L, domain.id)
        assertEquals("Rocket M5 Main Tower", domain.modemName)
        assertEquals("192.168.1.50", domain.ip)
        assertEquals(DeviceType.BROADCAST, domain.type)
        assertEquals(DeviceStatus.ONLINE, domain.status)
        assertEquals("Main Tower East", domain.location)
        assertEquals(12L, domain.lastPingMs)
        assertTrue(domain.lastPingSuccess == true)
    }
}
