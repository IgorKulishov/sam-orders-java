package ecommerce.component;

import java.util.*;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import ecommerce.connection.HibernateConnectionPoolImpl;
import ecommerce.dao.User;
import ecommerce.dto.UserDto;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateUser implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    public User userDao = new User();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {
//        LambdaLogger logger = context.getLogger();
        // Prepare response
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        // Persist block
        try {
            HibernateConnectionPoolImpl connectionPool = new HibernateConnectionPoolImpl();
            SessionFactory sessionFactory = connectionPool.createSessionFactory();
            Session session = sessionFactory.getCurrentSession();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                UserDto userDto = objectMapper.readValue(event.getBody(), UserDto.class);

                session.beginTransaction();
                userDao.setFirstName(userDto.getFirstName());
                userDao.setLastName(userDto.getLastName());
                userDao.setUserName(userDto.getUserName());

                UUID id = (UUID) session.save(userDao);
                session.getTransaction().commit();
                userDto.setId(id);

                //  Alternative way
                //  List<UserEntry> users = session.createQuery("from UserEntry").list();
                //  users.stream().forEach( u -> System.out.println(u.getFirstName()));

                String responseJsonStrBody = prepareResponseObject(userDto);
                return response.withBody(responseJsonStrBody).withStatusCode(200);

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String prepareResponseObject(UserDto userDto) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper
                    .writerWithView(UserDto.class)
                    .writeValueAsString(userDto);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}