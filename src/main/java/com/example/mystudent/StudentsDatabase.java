package com.example.mystudent;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;

public class StudentsDatabase implements TableInterface, StudentsDatabaseInterface{

    // Instance variables
    String url, username, password;
    Connection connection;

    // Constructor
    StudentsDatabase(String url, String username, String password) {
        // Constructor: connect to the database
        this.url = url;
        this.username = username;
        this.password = password;
        this.connection = getConnection(url, username, password);
    }

    // Abstract method of TableInterface returns a Connection object to the database
    public Connection getConnection(String url, String username, String password) {
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("\nConnection to the database server successful!");
        }
        catch(SQLException e){ System.out.println(e); }

        return connection;
    }

    // Nested class Schedule
    class Schedule {
        String ddlCreateTable, ddlPopulateTable;
        String ddlUpDateCourseInstructor, ddlUpDateInstructor;
        String filename, nameTable;

        // Constructor
        Schedule(String filename, String nameTable) throws SQLException {

            this.filename = filename; this.nameTable = nameTable;
            this.ddlCreateTable = StudentsDatabaseInterface.ddlCreateTableSchedule;
            this.ddlPopulateTable = TableInterface.loadDataInFileTable(filename, nameTable);

            // Create Table
            TableInterface.dropTable(connection, nameTable);
            TableInterface.createTable(connection, ddlCreateTable);
            System.out.println("\nTable Schedule created successfully");

            // Populate Table
            TableInterface.setLocalInFileLoading(connection);
            TableInterface.populateTable(connection, ddlPopulateTable);
            System.out.println("\nTable Schedule populated successfully");
            ResultSet RS = TableInterface.getTable(connection, nameTable);
            System.out.println("\nQuery on Schedule executed successfully");
        }

        // Update course instructor
        public void upDateCourseInstructor(String courseID, String sectionNumber, String nameInstructor) throws SQLException {

            this.ddlUpDateCourseInstructor = StudentsDatabaseInterface.ddlUpDateCourseInstructor(courseID, sectionNumber, nameInstructor);
            TableInterface.updateField(connection, ddlUpDateCourseInstructor);
        }

        // Update instructor
        public void upDateInstructor(String nameInstructor, String nameNewInstructor) throws SQLException {

            this.ddlUpDateInstructor = StudentsDatabaseInterface.ddlUpDateInstructor(nameInstructor, nameNewInstructor);
            TableInterface.updateField(connection, ddlUpDateInstructor);
        }
    }

    // Nested class Courses
    class Courses {
        String ddlCreateTable, ddlPopulateTable;
        String nameToTable, nameFromTable;

        // Constructor: Build and populate Table Courses in DBMS
        Courses(String nameToTable, String nameFromTable) throws SQLException {

            this.nameToTable = nameToTable; this.nameFromTable = nameFromTable;
            this.ddlCreateTable = StudentsDatabaseInterface.ddlCreateTableCourses;
            this.ddlPopulateTable = StudentsDatabaseInterface.ddlInsertTableCourses(nameToTable, nameFromTable);

            // Create Table
            TableInterface.dropTable(connection, nameToTable);
            TableInterface.createTable(connection, ddlCreateTable);
            System.out.println("\nTable Courses created successfully");

            // Populate Table
            TableInterface.insertFromSelect(connection, ddlPopulateTable);
            System.out.println("\nTable Courses populated successfully");
            ResultSet RS = TableInterface.getTable(connection, nameToTable);
            System.out.println("\nQuery on Courses executed successfully");
        }
    }

    // Nested class Students
    class Students {
        String ddlCreateTable, ddlPopulateTable;
        String nameTable;

        // Constructor
        Students(String nameTable) throws SQLException {

            this.nameTable = nameTable;
            this.ddlCreateTable = StudentsDatabaseInterface.ddlCreateTableStudents;
            this.ddlPopulateTable = StudentsDatabaseInterface.ddlInsertTableStudents;
            System.out.println(ddlPopulateTable);

            // Create Table
            TableInterface.dropTable(connection, nameTable);
            TableInterface.createTable(connection, ddlCreateTable);
            System.out.println("\nTable Students created successfully");

            // Populate Table
            TableInterface.populateTable(connection, ddlPopulateTable);
            System.out.println("\nTable Students populated successfully");
            ResultSet RS = TableInterface.getTable(connection, nameTable);
            System.out.println("\nQuery on Students executed successfully");
        }
    }

    // Nested class Classes
    class Classes {
        String ddlCreateTable, ddlPopulateTable;
        String nameTable;

        // Constructor
        Classes(String nameTable) throws SQLException {

            this.nameTable = nameTable;
            this.ddlCreateTable = StudentsDatabaseInterface.ddlCreateTableClasses;
            this.ddlPopulateTable = StudentsDatabaseInterface.ddlInsertTableClasses;
            System.out.println(ddlPopulateTable);

            // Create Table
            TableInterface.dropTable(connection, nameTable);
            TableInterface.createTable(connection, ddlCreateTable);
            System.out.println("\nTable Classes created successfully");

            // Populate Table
            TableInterface.populateTable(connection, ddlPopulateTable);
            System.out.println("\nTable Classes populated successfully");
            ResultSet RS = TableInterface.getTable(connection, nameTable);
            System.out.println("\nQuery on Classes executed successfully");
        }
    }

    // Nested class AggregateGrades
    class AggregateGrades {
        String ddlCreateTable, ddlPopulateTable;
        String nameToTable, nameFromTable;

        // Constructor
        AggregateGrades(String nameToTable, String nameFromTable) throws SQLException {

            this.nameToTable = nameToTable; this.nameFromTable = nameFromTable;
            this.ddlCreateTable = StudentsDatabaseInterface.ddlCreateTableAggregateGrades;
            this.ddlPopulateTable = StudentsDatabaseInterface.ddlInsertTableAggregateGrades(nameToTable, nameFromTable);

            // Create Table
            TableInterface.dropTable(connection, nameToTable);
            TableInterface.createTable(connection, ddlCreateTable);
            System.out.println("\nTable Courses created successfully");

            // Populate Table
            TableInterface.insertFromSelect(connection, ddlPopulateTable);
            System.out.println("\nTable AggregateGrades populated successfully");
            ResultSet RS = getAggregateGrades(connection, nameToTable);
            System.out.println("\nQuery on AggregateGrades executed successfully");
        }

        // Get the aggregate grades
        public ResultSet getAggregateGrades(Connection connection, String nameTable) throws SQLException{
            return TableInterface.getTable(connection, nameTable);
        }

        // Get map of aggregate grades
        public Map<Character, Integer> getAggregateGrades(String nameTable) {
            Map<Character, Integer> mapAggregateGrades = new HashMap<Character, Integer>();
            try {
                ResultSet RS = TableInterface.getTable(connection, nameTable);

                while (RS.next()) {
                    mapAggregateGrades.put(RS.getString("grade").charAt(0), RS.getInt("numberStudents"));
                    // System.out.println(RS.getString("grade").charAt(0) + "\t" +  RS.getInt("numberStudents"));
                }
            }
            catch(SQLException e){ System.out.println(e); }

            return mapAggregateGrades;
        }
    }
}