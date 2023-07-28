package com.example.mystudent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

interface TableInterface {
    // Abstract method returns a Connection object to database
    Connection getConnection(String url, String username, String password);

    // Static methods
    static void dropSchema(Connection connection, String nameSchema) throws SQLException {
        PreparedStatement psDropTable = connection.prepareStatement("DROP SCHEMA IF EXISTS " + nameSchema);
        try{
            psDropTable.executeUpdate();
        }
        catch(SQLException e){ System.out.println(e); }
    }

    static void createSchema(Connection connection, String nameSchema) throws SQLException {
        PreparedStatement psCreateSchema = connection.prepareStatement("CREATE SCHEMA " + nameSchema);
        try{
            psCreateSchema.executeUpdate();
        }
        catch(SQLException e){ System.out.println(e); }
    }

    static void dropTable(Connection connection, String nameTable) throws SQLException {
        PreparedStatement psDropTable = connection.prepareStatement("DROP TABLE IF EXISTS " + nameTable);
        try{
            psDropTable.executeUpdate();
        }
        catch(SQLException e){ System.out.println(e); }
    }

    static void createTable(Connection connection, String ddlCreateTable) throws SQLException {
        PreparedStatement psCreateTable = connection.prepareStatement(ddlCreateTable);
        try{
            psCreateTable.executeUpdate();
        }
        catch(SQLException e){ System.out.println(e); }
    }

    static void setLocalInFileLoading(Connection connection) throws SQLException {
        // Set variable for local file loading
        PreparedStatement psSetLocalInFileLoading = connection.prepareStatement("SET GLOBAL local_infile = 1" );
        try{
            psSetLocalInFileLoading.executeUpdate();
            System.out.println("\nGlobal local-infile set successfully");
        }
        catch(SQLException e){ System.out.println(e); }
    }

    static String loadDataInFileTable(String nameFile, String nameTable) {
        return  "LOAD DATA LOCAL INFILE '" + nameFile + "' INTO TABLE " + nameTable +
                " COLUMNS TERMINATED BY '\t'" +
                " LINES TERMINATED BY  '\n'" +
                " IGNORE 1 LINES";
    }

    static void populateTable(Connection connection, String ddlPopulateTable) throws SQLException {
        PreparedStatement psPopulateTable = connection.prepareStatement(ddlPopulateTable);
        try{
            psPopulateTable.executeUpdate();
        }
        catch(SQLException e){ System.out.println(e); }
    }

    static void insertFromSelect(Connection connection, String nameToTable, String nameFromTable) throws SQLException {
        PreparedStatement psInsertFromSelect
                = connection.prepareStatement("INSERT INTO " + nameToTable + " SELECT * FROM " + nameFromTable);
        try{
            psInsertFromSelect.executeUpdate();
        }
        catch(SQLException e){ System.out.println(e); }
    }

    static void insertFromSelect(Connection connection, String ddlInsertFromSelect) throws SQLException {
        PreparedStatement psInsertFromSelect = connection.prepareStatement(ddlInsertFromSelect);
        try{
            psInsertFromSelect.executeUpdate();
        }
        catch(SQLException e){ System.out.println(e); }
    }

    static void insertRecord(Connection connection, String ddlInsertRecord) throws SQLException {
        PreparedStatement psInsertRecord = connection.prepareStatement(ddlInsertRecord);
        try{
            psInsertRecord.executeUpdate();
        }
        catch(SQLException e){ System.out.println(e); }
    }

    static void updateField(Connection connection, String ddlUpdateField) throws SQLException {
        PreparedStatement psUpdateField = connection.prepareStatement(ddlUpdateField);
        try{
            psUpdateField.executeUpdate();
        }
        catch(SQLException e){ System.out.println(e); }
    }

    static void deleteRecord(Connection connection, String ddlDeleteRecord) throws SQLException {
        PreparedStatement psDeleteRecord = connection.prepareStatement(ddlDeleteRecord);
        try{
            psDeleteRecord.executeUpdate();
        }
        catch(SQLException e){ System.out.println(e); }
    }

    static ResultSet getTable(Connection connection, String nameTable) throws SQLException {
        ResultSet RS = null;
        PreparedStatement psGetTable = connection.prepareStatement("SELECT * FROM " + nameTable);
        try{
            RS = psGetTable.executeQuery();
        }
        catch(SQLException e){ System.out.println(e); }

        return RS;
    }
}
