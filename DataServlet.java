import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.sql.DataSource;

@WebServlet(name = "DataServlet", urlPatterns = {"/","/*"})
public class DataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Data Servlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Data Servlet</h1>");
        out.println("<table border='1'>");
        out.println("<tr><th>ID</th><th>BusName</th><th>BusStop</th></tr>");
        try {
        // Look up the JDBC resource
        InitialContext cxt = new InitialContext();
        DataSource ds = (DataSource) cxt.lookup( "java:/comp/env/jdbc/mydatabase" );
        // Get a connection to the database
        Connection con = ds.getConnection();
        // Use the connection to execute a query and process the results
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM busdetails");
        while (rs.next()) {
            int id = rs.getInt("id");
            String busname = rs.getString("busname");
            String busstop = rs.getString("busstop");
            out.println("<tr><td>" + id + "</td><td>" + busname + "</td><td>" + busstop + "</td></tr>");
        }
        // Close the connection
        con.close();
        } catch (Exception e) {
        e.printStackTrace();
        }
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }
}
