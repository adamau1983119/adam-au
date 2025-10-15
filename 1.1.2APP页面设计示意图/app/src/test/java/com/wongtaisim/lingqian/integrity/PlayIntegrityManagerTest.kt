package com.wongtaisim.lingqian.integrity

import android.content.Context
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Play Integrity Manager 測試
 */
class PlayIntegrityManagerTest {
    
    private lateinit var context: Context
    private lateinit var playIntegrityManager: PlayIntegrityManager
    
    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        playIntegrityManager = PlayIntegrityManager(context)
    }
    
    @Test
    fun `test isPlayIntegrityAvailable returns true when manager is available`() {
        // 在測試環境中，這個方法應該返回true
        assertTrue(playIntegrityManager.isPlayIntegrityAvailable())
    }
    
    @Test
    fun `test verifyIntegrity returns appropriate result`() = runTest {
        val result = playIntegrityManager.verifyIntegrity("TEST_OPERATION")
        
        assertNotNull(result)
        assertTrue(result.status in listOf(
            PlayIntegrityManager.INTEGRITY_SUCCESS,
            PlayIntegrityManager.INTEGRITY_FAILED,
            PlayIntegrityManager.INTEGRITY_ERROR
        ))
        assertTrue(result.message.isNotEmpty())
        assertTrue(result.timestamp > 0)
    }
    
    @Test
    fun `test IntegrityResult data class properties`() {
        val result = PlayIntegrityManager.IntegrityResult(
            status = PlayIntegrityManager.INTEGRITY_SUCCESS,
            message = "Test message"
        )
        
        assertEquals(PlayIntegrityManager.INTEGRITY_SUCCESS, result.status)
        assertEquals("Test message", result.message)
        assertTrue(result.timestamp > 0)
    }
}
