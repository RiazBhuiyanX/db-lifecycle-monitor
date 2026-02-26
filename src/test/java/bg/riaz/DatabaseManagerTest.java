package bg.riaz;

import org.junit.jupiter.api.Test;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseManagerTest {

    @Test
    void testManagerInitialization() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "user";
        String pass = "pass";

        DatabaseManager dbManager = new DatabaseManager(url, user, pass);

        assertNotNull(dbManager, "Мениджърът трябва да бъде успешно инстанциран.");
    }

    @Test
    void testPerformFullCheckHandlesInvalidUrl() {
        String url = "jdbc:invalid:host:9999/null";
        String user = "fake";
        String pass = "fake";

        DatabaseManager dbManager = new DatabaseManager(url, user, pass);

        assertDoesNotThrow(dbManager::performFullCheck, "Програмата не трябва да спира (crash), дори когато URL адресът е невалиден.");
    }

    @Test
    void testCheckVersionReturnsCorrectString() throws SQLException {
        Connection mockConn = mock(Connection.class);
        Statement mockStmt = mock(Statement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConn.createStatement()).thenReturn(mockStmt);
        when(mockStmt.executeQuery("SELECT version()")).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString(1)).thenReturn("PostgreSQL 16.0");

        DatabaseManager dbManager = new DatabaseManager("url", "user", "pass");

        String result = dbManager.checkVersion(mockConn);

        assertEquals("PostgreSQL 16.0", result, "Методът трябва да върне версията, подадена от базата.");
    }

    @Test
    void testCheckUserCountReturnsCorrectInt() throws SQLException {
        Connection mockConn = mock(Connection.class);
        Statement mockStmt = mock(Statement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConn.createStatement()).thenReturn(mockStmt);
        when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(5); // Симулираме 5 активни потребители

        DatabaseManager dbManager = new DatabaseManager("url", "user", "pass");

        int count = dbManager.checkUserCount(mockConn);

        assertEquals(5, count, "Методът трябва правилно да превърне резултата от SQL в числото 5.");
    }

    @Test
    void testSaveHeartbeatVerifiesExecution() throws SQLException {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPstmt = mock(PreparedStatement.class);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockPstmt);

        DatabaseManager dbManager = new DatabaseManager("url", "user", "pass");

        dbManager.saveHeartbeat(mockConn, 10, "PostgreSQL 16");


        verify(mockPstmt, times(1)).executeUpdate();
        verify(mockPstmt).setInt(1, 10);
        verify(mockPstmt).setString(2, "PostgreSQL 16");
    }

    @Test
    void testCheckVersionHandlesSQLException() throws SQLException {
        Connection mockConn = mock(Connection.class);
        when(mockConn.createStatement()).thenThrow(new SQLException("Database connection lost"));

        DatabaseManager dbManager = new DatabaseManager("url", "user", "pass");

        String result = dbManager.checkVersion(mockConn);

        assertEquals("Unknown Version", result, "Методът трябва да върне 'Unknown Version' при SQL грешка.");
    }
}
