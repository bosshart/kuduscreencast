package com.kuduscreencast.examples;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.*;
import org.apache.kudu.client.shaded.com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bosshart on 11/11/16.
 */
public class SimpleExample {

  private static final String KUDU_MASTER = System.getProperty(
          "kuduMaster", "quickstart.cloudera");

  public static void main(String[] args) {

    String tableName = "movietable";
    KuduClient client = new KuduClient.KuduClientBuilder(KUDU_MASTER).build();

    try {
      //Create our table
      List<ColumnSchema> columns = new ArrayList(3);
      columns.add(new ColumnSchema.ColumnSchemaBuilder("movieid", Type.INT32)
              .key(true)
              .build());
      columns.add(new ColumnSchema.ColumnSchemaBuilder("moviename", Type.STRING)
              .build());
      columns.add(new ColumnSchema.ColumnSchemaBuilder("rating", Type.INT32)
              .build());

      Schema schema = new Schema(columns);
      client.createTable(tableName, schema,
              new CreateTableOptions().addHashPartitions(ImmutableList.of("movieid"), 3));

      KuduTable table = client.openTable(tableName);
      KuduSession session = client.newSession();

      // Insert some data
      List<String> movies = ImmutableList.of("Ghost", "Tombstone", "Star Wars");

      for (int i = 0; i < movies.size(); i++) {
        Insert insert = table.newInsert();
        PartialRow row = insert.getRow();
        row.addInt(0, i);
        row.addString(1, movies.get(i));
        row.addInt("rating", 5) ;
        session.apply(insert);
      }

      // Read some data
      List<String> projectionColumns = ImmutableList.of("moviename","rating");
      KuduScanner scanner = client.newScannerBuilder(table)
              .setProjectedColumnNames(projectionColumns)
              .build();
      while(scanner.hasMoreRows()) {
        for(RowResult movieResult: scanner.nextRows()) {
          System.out.println("The rating for " + movieResult.getString(0) + " is " + movieResult.getInt(1));
        }
      }
      scanner.close();
      session.close();
      
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        client.close();
      } catch(KuduException kuduE) {
        kuduE.printStackTrace();
      }
    }

  }
}
