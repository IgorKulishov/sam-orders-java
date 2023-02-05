package ecommerce;

//import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

//import static org.junit.Assert.assertTrue;
//import static org.mockito.Mockito.*;


import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import ecommerce.component.CreateUser;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;;
import java.sql.*;

public class AppTest {

  private System system;
  private Connection connMock;
  private PreparedStatement selectStatementMock;
  private ResultSet queryResultMock;

  @BeforeEach
  public void setup() throws SQLException {
    queryResultMock = mock(ResultSet.class);
    selectStatementMock = mock(PreparedStatement.class);
    connMock = mock(Connection.class);
//    mockStatic(System.class);
    mockStatic(DriverManager.class);
//    when(System.getenv("RDS_USERNAME")).thenReturn("user_test");
//    when(System.getenv("RDS_PASSWORD")).thenReturn("pass_test");
//    when(System.getenv("RDS_HOSTNAME")).thenReturn("host_test");
    when(selectStatementMock.executeQuery()).thenReturn(queryResultMock);
    when(connMock.isClosed()).thenReturn(false);
    when(connMock.isValid(0)).thenReturn(true);
    when(connMock.prepareStatement(any())).thenReturn(selectStatementMock);
    when(DriverManager.getConnection(any(), any(), any())).thenReturn(connMock);
  }

  @Test
  public void successfulResponse()  {

    CreateUser createUser = new CreateUser();
    APIGatewayProxyResponseEvent response = createUser.handleRequest(null, null);
    Assertions.assertEquals(200, response.getStatusCode().intValue());
    Assertions.assertEquals("application/json", response.getHeaders().get("Content-Type"));
    String content = response.getBody();
    Assertions.assertNotNull(content);
    Assertions.assertEquals(content, "[ ]");
//    assertTrue(content.contains("\"hello world\""));
//    assertTrue(content.contains("\"location\""));
  }
}
