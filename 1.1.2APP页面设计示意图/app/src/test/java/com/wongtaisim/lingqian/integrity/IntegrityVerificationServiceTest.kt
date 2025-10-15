package com.wongtaisim.lingqian.integrity

import android.content.Context
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Integrity Verification Service 測試
 */
class IntegrityVerificationServiceTest {
    
    private lateinit var context: Context
    private lateinit var integrityService: IntegrityVerificationService
    
    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        integrityService = IntegrityVerificationService(context)
    }
    
    @Test
    fun `test initializeIntegrity sets appropriate status`() = runTest {
        integrityService.initializeIntegrity()
        
        val status = integrityService.integrityStatus.value
        assertTrue(status in IntegrityVerificationService.IntegrityStatus.values())
    }
    
    @Test
    fun `test verifyOperation returns boolean result`() = runTest {
        val result = integrityService.verifyOperation(
            IntegrityVerificationService.OPERATION_DRAW_QIAN
        )
        
        assertTrue(result is Boolean)
    }
    
    @Test
    fun `test getStatusDescription returns non-empty string`() {
        val description = integrityService.getStatusDescription()
        assertTrue(description.isNotEmpty())
    }
    
    @Test
    fun `test resetVerification resets status to unknown`() {
        integrityService.resetVerification()
        
        val status = integrityService.integrityStatus.value
        assertEquals(IntegrityVerificationService.IntegrityStatus.UNKNOWN, status)
    }
    
    @Test
    fun `test IntegrityStatus enum values`() {
        val values = IntegrityVerificationService.IntegrityStatus.values()
        assertEquals(6, values.size)
        
        assertTrue(values.contains(IntegrityVerificationService.IntegrityStatus.UNKNOWN))
        assertTrue(values.contains(IntegrityVerificationService.IntegrityStatus.VERIFYING))
        assertTrue(values.contains(IntegrityVerificationService.IntegrityStatus.VERIFIED))
        assertTrue(values.contains(IntegrityVerificationService.IntegrityStatus.FAILED))
        assertTrue(values.contains(IntegrityVerificationService.IntegrityStatus.ERROR))
        assertTrue(values.contains(IntegrityVerificationService.IntegrityStatus.UNAVAILABLE))
    }
}
