package ecommerce.component;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.connection.HibernateConnectionPool;
import ecommerce.connection.HibernateConnectionPoolImpl;
import ecommerce.dao.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReadUser implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> pathParameters = input.getPathParameters();
//        LambdaLogger logger = context.getLogger();
        // Prepare response
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        // Persist block
        try (SessionFactory sessionFactory = new HibernateConnectionPoolImpl().createSessionFactory()) {
            Session session = sessionFactory.getCurrentSession();

            session.beginTransaction();

            String id = pathParameters.get("id");

            User user = session.get(User.class, UUID.fromString(pathParameters.get("id")));

            String responseJsonStrBody = prepareResponseObject(user);

            session.getTransaction().commit();

            return response.withBody(responseJsonStrBody).withStatusCode(200);

        }

    }

    private String prepareResponseObject(User user) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper
                    .writerWithView(User.class)
                    .writeValueAsString(user);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}