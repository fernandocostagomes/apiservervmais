package fernandocostagomes.schemas

import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class ServiceActionTest {

    private val mockConnection = mockk<Connection>()
    private val mockStatement = mockk<PreparedStatement>(relaxed = true)
    @Test
    fun createTest() {
        val mockGeneratedKeys = mockk<ResultSet>()
        every { mockConnection.createStatement() } returns mockStatement
        every { mockConnection.prepareStatement(any(), eq(Statement.RETURN_GENERATED_KEYS)) } returns mockStatement
        every { mockStatement.executeUpdate() } returns 1
        every { mockStatement.generatedKeys } returns mockGeneratedKeys
        every { mockGeneratedKeys.next() } returns true
        every { mockGeneratedKeys.getInt(1) } returns 1

        val serviceAction = ServiceAction(mockConnection)
        val action = Action("Test Action", "Test Description")
        val result = runBlocking { serviceAction.create(action) }

        assertEquals(1, result)
    }

    @Test
    fun readTest() {

        every { mockConnection.createStatement() } returns mockStatement

        val serviceAction = ServiceAction(mockConnection)
        val result = runBlocking { serviceAction.read(1) }

        assertEquals("Test Action", result.nameAction)
        assertEquals("Test Description", result.descriptionAction)
    }

    @Test
    fun updateTest() {
        every { mockConnection.createStatement() } returns mockStatement
        every { mockStatement.executeUpdate() } returns 1

        val serviceAction = ServiceAction(mockConnection)
        val action = Action("Test Action", "Test Description")
        val result = runBlocking { serviceAction.update(1, action) }

        assertEquals(1, result)
    }

    @Test
    fun deleteTest() {
        every { mockConnection.createStatement() } returns mockStatement
        every { mockStatement.executeUpdate() } returns 1

        val serviceAction = ServiceAction(mockConnection)
        val result = runBlocking { serviceAction.delete(1) }

        assertEquals(1, result)
    }
}
