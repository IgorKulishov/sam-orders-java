package ecommerce.component;

//import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

//import static org.mockito.Mockito.*;


import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import ecommerce.connection.HibernateConnectionPoolImpl;
import ecommerce.dao.User;
import ecommerce.dto.UserDto;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.junit.Test;;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CreateUser.class})
public class CreateUserTest {

  private System system;
  HibernateConnectionPoolImpl hibernateConnectionPoolMock;
  SessionFactory sessionFactoryMock;
  MetadataSources metadataSourcesMock;
  Session sessionMock;
  UUID uuid = UUID.fromString("8d6b2d60-b3b9-40c9-be5e-ef112daba213");
  User user;
  Transaction transaction;

  @Test
  public void successfulResponse() throws Exception {
    /* Prepare "event" */
    ObjectMapper objectMapper = new ObjectMapper();
    InputStream eventStream = this.getClass().getResourceAsStream("event-test.json");
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    APIGatewayProxyRequestEvent input = objectMapper.readValue(eventStream, APIGatewayProxyRequestEvent.class);
    /* Prepare "mocks" */
    hibernateConnectionPoolMock = Mockito.mock(HibernateConnectionPoolImpl.class);
    sessionFactoryMock = Mockito.mock(SessionFactory.class);
    sessionMock = Mockito.mock(Session.class);
    transaction = Mockito.mock(Transaction.class);

    Mockito.when(hibernateConnectionPoolMock.createSessionFactory()).thenReturn(sessionFactoryMock);
    Mockito.when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
    Mockito.when(sessionMock.save(any(Object.class))).thenReturn(uuid);
    Mockito.when(sessionMock.getTransaction()).thenReturn(transaction);
    doNothing().when(transaction).commit();
    PowerMockito.whenNew(HibernateConnectionPoolImpl.class).withAnyArguments().thenReturn(hibernateConnectionPoolMock);
    /* call method */
//    Assertions.assertEquals( sessionFactoryMock, hibernateConnectionPoolMock.createSessionFactory());
    CreateUser createUser = new CreateUser();
    APIGatewayProxyResponseEvent response = createUser.handleRequest(input, null);
    PowerMockito.verifyNew(HibernateConnectionPoolImpl.class).withNoArguments();
    verify(transaction,times(1)).commit();
    /* Assertions */
    assertEquals(200, response.getStatusCode().intValue());
    String content = response.getBody();
    assertEquals("application/json", response.getHeaders().get("Content-Type"));
    assertNotNull(content);
    UserDto user = objectMapper.readValue(content, UserDto.class);
    assertEquals(user.getFirstName(), "Bruce");
  }
}