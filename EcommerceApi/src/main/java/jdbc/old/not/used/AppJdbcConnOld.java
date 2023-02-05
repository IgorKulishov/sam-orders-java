package jdbc.old.not.used;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.dao.User;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Example of JDBC connection (not used)
public class AppJdbcConnOld implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        ObjectMapper objectMapper = new ObjectMapper();
        //        Prepare response
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Connection conn = null;
        try {
            String output = null;

            List<User> users = new ArrayList<>();
            Class.forName("org.postgresql.Driver");;
            String jdbcUrl = getJDBCUrl();
            System.out.println("jdbcUrl : ");
            System.out.println(jdbcUrl);
            String userName = System.getenv("RDS_USERNAME");
            String password = System.getenv("RDS_PASSWORD");
            conn = DriverManager.getConnection(jdbcUrl, userName, password);
            if(conn.isClosed() || !conn.isValid(0)) {
                System.out.println("Unable to connect to " + jdbcUrl);
                System.exit(0);
            }
            System.out.println("Successfully connected to JDBC");
            PreparedStatement selectStatement = conn.prepareStatement("select * from users");
            ResultSet queryResult = selectStatement.executeQuery();
            while(queryResult.next()) {

//                UserEntry user = new UserEntry(user.getFirstName(), user.getLastName());
//                users.add(user);
            }
//            output = objectMapper.writeValueAsString(employee);
            output = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(users);
            System.out.printf("output : " + output);
            return response.withStatusCode(200).withBody(output);
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to connect to JDBC");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return response.withBody("{}").withStatusCode(500);
    }

    private String getJDBCUrl() {
        String JDBC_PREFIX = "jdbc:postgresql://";
        String dbEndpoint = System.getenv("RDS_ENDPOINTS");
        String port = System.getenv("RDS_PORT");
        String dbName = System.getenv("RDS_DB_NAME");
        return JDBC_PREFIX + dbEndpoint + ":" + port + "/" + dbName;
    }
}

