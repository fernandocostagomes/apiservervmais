package fernandocostagomes.schemas

import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class SchemaActionTest {

//    private val mockConnection = mockk<Connection>()
//    private val mockStatement = mockk<PreparedStatement>(relaxed = true)
//    private val mockServiceAction = mockk<ServiceAction>(relaxed = true)
//
//    @Test
//    suspend fun testCreate() {
//        val generatedKeys = mockk<ResultSet>()
//
//        every { mockConnection.prepareStatement(any(), eq(Statement.RETURN_GENERATED_KEYS)) } returns mockStatement
//        every { mockStatement.executeUpdate() } returns 1
//        every { mockStatement.generatedKeys } returns generatedKeys
//        every { generatedKeys.next() } returns true
//        every { generatedKeys.getInt(1) } returns 1
//
//        val action = Action( "Test Action", "This is a test action" )
//
//        val expectedId = 1
//
//        val actualId = mockServiceAction.create( action )
//
//        assertEquals(expectedId, actualId)
//    }
}