package ecommerce.component;

import ecommerce.dto.UserDto;
import ecommerce.dao.User;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.*;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import ecommerce.connection.HibernateConnectionPoolImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReadUser.class})
public class ReadUserTest {
  HibernateConnectionPoolImpl hibernateConnectionPoolMock;
  SessionFactory sessionFactoryMock;
  Session sessionMock;
  UUID uuid = UUID.fromString("8d6b2d60-b3b9-40c9-be5e-ef112daba213");
  Transaction transaction;
  ObjectMapper objectMapper = new ObjectMapper();
  InputStream eventStream;
  APIGatewayProxyRequestEvent event;
  @Before
  public void prepareMock() throws Exception {
    User userDao = prepareUserDao();
    /* Prepare "mocks" */
    hibernateConnectionPoolMock = Mockito.mock(HibernateConnectionPoolImpl.class);
    sessionFactoryMock = Mockito.mock(SessionFactory.class);
    sessionMock = Mockito.mock(Session.class);
    transaction = Mockito.mock(Transaction.class);
    /* Stabbing */
    Mockito.when(hibernateConnectionPoolMock.createSessionFactory()).thenReturn(sessionFactoryMock);
    Mockito.when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
//    doNothing().when(sessionMock).beginTransaction();
    Mockito.when(sessionMock.get(User.class, UUID.class)).thenReturn(userDao);
    Mockito.when(sessionMock.getTransaction()).thenReturn(transaction);
    doNothing().when(transaction).commit();
    /* PowerMock */
    PowerMockito.whenNew(HibernateConnectionPoolImpl.class).withAnyArguments().thenReturn(hibernateConnectionPoolMock);
  }

  public User prepareUserDao() throws IOException {
    /* Prepare UserDao */
    eventStream = this.getClass().getResourceAsStream("event-test.json");
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    event = objectMapper.readValue(eventStream, APIGatewayProxyRequestEvent.class);
    UserDto userDto = objectMapper.readValue(event.getBody(), UserDto.class);
    return new User(userDto.firstName, userDto.lastName, userDto.userName);
  }

  @Test
  public void successfulResponse() {
    /* call method */
    ReadUser readUser = new ReadUser();
    APIGatewayProxyResponseEvent response = readUser.handleRequest(event, null);

    /* Assertions */
    verify(transaction,times(1)).commit();
//    verify(sessionMock,times(1)).save(readUser.userDao);
    assertEquals(200, response.getStatusCode().intValue());
    String content = response.getBody();
    assertEquals("application/json", response.getHeaders().get("Content-Type"));
    assertNotNull(content);
//    UserDto user = objectMapper.readValue(content, UserDto.class);
//    assertEquals(user.getFirstName(), "Bruce");
  }
}