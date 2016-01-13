package com.edgars.db;/*
package com.edgars;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

*/
/**
 * Class works with HBase.
 * <p/>
 * Created by Edgars on 18/11/2015.
 * Email: edgars_fjodorovs@inbox.lv
 *//*

public class Manager {
    public static class HbaseManager implements HBase {

        */
/**
         * IP address to server to connect.
         *//*

        private static final String SERVER_IP = "158.129.140.188:60000";
        */
/**
         * Master pc to connect.
         *//*

        private static final String MASTER_PC = "master.pc";
        */
/**
         * Zookeeper's parent node.
         *//*

        private static final String ZOOKEEPER_PARENT_NODE = "/hbase-unsecure";
        */
/**
         * Connection client port.
         *//*

        private static final String CLIENT_PORT = "2181";

        */
/**
         * HBase admin variable, used to help close connection.
         *//*

        HBaseAdmin admin;
        */
/**
         * HBase configuration variable used to store configurations.
         *//*

        Configuration conf;
        */
/**
         * HBase table variable to operate with database table.
         *//*

        HTable table;

        */
/**
         * Method to insert/update data into HBase.
         *
         * @param put    Put object with data to insert.
         * @param hTable HBase table into which to insert data.
         *//*

        @Override
        public void put(Put put, HTable hTable) {
            try {
                hTable.put(put);
            } catch (RetriesExhaustedWithDetailsException | InterruptedIOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Data successfully added into table: " + hTable.getTableName());
        }

        */
/**
         * Helper method to simplify getting a table.
         *
         * @param tableName Name of the table.
         * @return Returns table object.
         *//*

        @Override
        public HTable getTable(String tableName) {
            try {
                table = new HTable(getConf(), tableName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return table;
        }

        */
/**
         * Helper method to get a configuration.
         *
         * @return configuration.
         *//*

        public Configuration getConf() {
            return conf;
        }

        */
/**
         * Method to delete data from HBase.
         *
         * @param delete Delete object with items to delete.
         * @param table  Name of the table.
         *//*

        @Override
        public void delete(Delete delete, HTable table) {
            System.out.println("Calling delete()");
            try {
                table.delete(delete);
                System.out.println("Record deleted!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        */
/**
         * Creates and sets up configuration to connect to HBase.
         *//*

        @Override
        public void connect() {
            System.out.println("Establishing connection with database..");

            conf = HBaseConfiguration.create();
            conf.set("hbase.master", SERVER_IP);
            conf.set("hbase.zookeeper.quorum", MASTER_PC);
            conf.set("zookeeper.znode.parent", ZOOKEEPER_PARENT_NODE);
            conf.set("hbase.zookeeper.property.clientPort", CLIENT_PORT);
        }

        */
/**
         * Close connection with HBase.
         *//*

        @Override
        public void close() {
            try {
                admin = new HBaseAdmin(conf);
                admin.close();
                System.out.println("Connection closed..");
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }

        @Override
        public void drop() {

        }

        */
/**
         * Helper method to get results of specific data.
         *
         * @param tableName    Name of the table.
         * @param columnFamily Column family.
         * @param column       Column.
         * @return ResultScanner object with found data.
         * @throws IOException
         *//*

        public ResultScanner scan(String tableName, String columnFamily, String column) throws IOException {
            connect();
            Scan scan = new Scan();
            scan.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
            //scan.addFamily(Bytes.toBytes(columnFamily));
            ResultScanner resultName = getTable(tableName).getScanner(scan);
            close();
            return resultName;
        }

        */
/**
         * Get specific value of column.
         *
         * @param row       ID of record.
         * @param tableName Name of the table.
         * @return Returns data what was search for.
         * @throws IOException
         *//*

        public String getById(String row, String tableName, String columnFamily, String column) throws IOException {
            Get get = new Get(Bytes.toBytes(row));
            Result result = getTable(tableName).get(get);
            byte[] value = result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(column));
            String valueString = Bytes.toString(value);

            return valueString;
        }

        public ArrayList getAllIds(String tableName, String columnFamily) throws IOException {
            ArrayList list = new ArrayList();
connect();
            Scan scan = new Scan();
            scan.addFamily(Bytes.toBytes(columnFamily));
            ResultScanner rs = getTable(tableName).getScanner(scan);
            for (Result r : rs) {
                list.add(Bytes.toString(r.getRow()));
            }
close();
            return list;
        }

        */
/**
         * Select method made similar to relation database SELECT * WHERE.
         *
         * @param tableName    Name of the table.
         * @param columnFamily Column family.
         * @param column       Column.
         * @param where        WHERE. What exactly we are searching.
         * @return JSONObject with all data of found column. *So far getting only ID*
         * @throws IOException
         *//*

        public JSONObject select(String tableName, String columnFamily, String column, String where) throws IOException {
            System.out.println("select() called");
            JSONObject jsonObject = new JSONObject();
            String id = "";
            ResultScanner resultScanner = scan(tableName, columnFamily, column);

            for (Result result = resultScanner.next(); result != null; result = resultScanner.next()) {
                if (where.equals(Bytes.toString(result.value()))) {
                    id = Bytes.toString(result.getRow());
*/
/*                    for (KeyValue keyValue : result.list()) {
                        jsonObject.put(Bytes.toString(keyValue.getKey()), Bytes.toString(keyValue.getValue()));
                        System.out.println(keyValue.getKeyString() + " : " + Bytes.toString(keyValue.getValue()));
                    }*//*

                    break;
                }
            }
            jsonObject.put("id", id);
            return jsonObject;
        }

        */
/**
         * Update record in Hbase.
         *
         * @param tableName    Name of the table.
         * @param columnFamily Column family.
         * @param id           ID of record to update.
         * @param dataJSON     JSONObject with data to update.
         * @throws InterruptedIOException
         * @throws RetriesExhaustedWithDetailsException
         *//*

        public void update(String tableName, String columnFamily, String id, JSONObject dataJSON) throws InterruptedIOException, RetriesExhaustedWithDetailsException {
            System.out.println("update() called");
            doPut(tableName, columnFamily, id, dataJSON);
        }

        */
/**
         * Insert new record into Hbaase.
         *
         * @param tableName    Name of the table.
         * @param columnFamily Column family.
         * @param dataJSON     JSONObject with data to insert.
         * @return
         * @throws IOException
         *//*

        public String insert(String tableName, String columnFamily, JSONObject dataJSON) throws IOException {
            System.out.println("insert() called");
            String id = makeId(tableName);
            doPut(tableName, columnFamily, id, dataJSON);

            return id;
        }

        */
/**
         * Helper method for insert and update.
         *
         * @param tableName    Name of the table.
         * @param columnFamily Column family.
         * @param id           ID of record to update.
         * @param dataJSON     JSONObject with data to insert.
         * @throws InterruptedIOException
         * @throws RetriesExhaustedWithDetailsException
         *//*

        private void doPut(String tableName, String columnFamily, String id, JSONObject dataJSON) throws InterruptedIOException, RetriesExhaustedWithDetailsException {
            Put p = new Put(Bytes.toBytes(id));
            String key;
            String value;
            Iterator<?> keys = dataJSON.keys();
            while (keys.hasNext()) {
                key = (String) keys.next();
                dataJSON.getString(key);
                value = dataJSON.get(key).toString();
                p.add(Bytes.toBytes(columnFamily), Bytes.toBytes(key), Bytes.toBytes(value));
            }
            table = getTable(tableName);
            table.put(p);
        }

        */
/**
         * Method makes new ID into table. Works as autoincrement.
         *
         * @param tableName Name of the table.
         * @return Returns highest ID into table.
         * @throws IOException
         *//*

        public String makeId(String tableName) throws IOException {
            Scan scan = new Scan();
            scan.setFilter(new FirstKeyOnlyFilter());
            ResultScanner rs = getTable(tableName).getScanner(scan);

            List idList = new ArrayList();
            for (Result r : rs) {
                byte[] row = r.getRow();
                idList.add(Integer.parseInt(Bytes.toString(row)));
            }

            int maxId = 0;
            if (!idList.isEmpty()) {
                maxId = Integer.parseInt(Collections.max(idList).toString());
            }
            Integer newId = maxId + 1;

            return newId.toString();
        }

        public JSONObject getBooks(String tableName, String columnFamily, JSONArray books) {
connect();
            JSONArray bookArray = new JSONArray();

            for (int i = 0; i < books.length(); i++) {
                try {
                    String id = books.get(i).toString();
                    Get g = new Get(Bytes.toBytes(id));
                    Result result = getTable(tableName).get(g);

                    String title = Bytes.toString(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes("title")));
                    String price = Bytes.toString(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes("price")));
                    String img = Bytes.toString(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes("img")));
                    String url = Bytes.toString(result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes("url")));
                    JSONObject book = new JSONObject();
                    book.put("title", title);
                    book.put("price", price);
                    book.put("img", img);
                    book.put("url", url);
                    bookArray.put(book);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            JSONObject finalJSONObject = new JSONObject();
            finalJSONObject.put("book", bookArray);
close();
            return finalJSONObject;
        }

    }

}

*/
