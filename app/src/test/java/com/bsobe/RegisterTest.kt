package com.bsobe

import org.junit.Assert
import org.junit.Test

class RegisterTest {

    @Test
    fun `shouldShowErrorSnackbar should return true if there are some items`() {
        val register = Register("teeeeeeee")
        val comp = register.comp()

        Assert.assertEquals(comp, register.text.length)
    }
}