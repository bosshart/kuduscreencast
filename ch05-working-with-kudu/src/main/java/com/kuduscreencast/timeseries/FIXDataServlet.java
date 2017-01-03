package com.kuduscreencast.timeseries;

import com.cloudera.impala.jdbc41.DataSource;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FIXDataServlet extends HttpServlet {
  private Connection connection;
  private static String connectionUrl = System.getProperty(
      "connection.url", "jdbc:impala://quickstart.cloudera:21050");

  private static String jdbcDriverName = System.getProperty(
      "jdbc.driver.name", "com.cloudera.impala.jdbc41.DataSource");


  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    try {
      // Load the driver
      Class.forName(jdbcDriverName);
      DataSource ds = new DataSource();
      ds.setURL(connectionUrl);
      connection = ds.getConnection();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setContentType("text/plain");
    response.setStatus(HttpServletResponse.SC_OK);
    ResultSet rs = null;
    try {
      Statement stmt = connection.createStatement();

      rs = stmt.executeQuery("SELECT stocksymbol, max(orderqty) AS max_order, cast((cast(transacttime/10000 as int)*10000)/1000 as timestamp) AS 10_s_time_window FROM fixdata\n" +
              "    WHERE cast(transacttime/1000 as timestamp) > date_sub(to_utc_timestamp(now(),'PDT'), interval 10 minutes)\n" +
              "    AND cast(transacttime/1000 as timestamp) < date_sub(to_utc_timestamp(now(),'PDT'), interval 11 seconds)\n" +
              "    GROUP BY stocksymbol, 10_s_time_window\n" +
              "    ORDER BY stocksymbol, 10_s_time_window;");

      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
      writer.append("symbol,orderqty,timestamp");
      writer.newLine();
      while (rs.next()) {
        writer.append(rs.getString(1) + "," + rs.getInt(2) + "," + rs.getTimestamp(3));
        writer.newLine();
      }
      writer.flush();
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        rs.close();
      } catch (Exception e) {
        // swallow
      }
    }
  }

  public void destroy(  ) {
    // Close the connection
    if (connection != null)
      try { connection.close(  ); } catch (SQLException ignore) { }
  }
}
