package com.example.myshapeagglomerated;

// Interface StudentsDatabaseInterface
interface StudentsDatabaseInterface {

    // Constant: Schema name
    String SCHEMA = "Students";

    // Interface CONSTANT Strings specific to Database Students
    String ddlCreateTableSchedule = "CREATE TABLE Schedule(" +
            "courseId CHAR(12) NOT NULL UNIQUE, " +
            "sectionNumber VARCHAR(8) NOT NULL UNIQUE, " +
            "title VARCHAR(64), " +
            "year INT, " +
            "semester CHAR(6), " +
            "instructor VARCHAR(24), " +
            "department CHAR(16), " +
            "program VARCHAR(48), " +
            "PRIMARY KEY(courseId, sectionNumber))";

    String ddlCreateTableStudents = "CREATE TABLE Students(" +
            "emplId INT PRIMARY KEY, " +
            "name VARCHAR(32) NOT NULL, " +
            "gender CHAR CHECK (gender = 'F' OR gender = 'M' OR gender = 'U'), " +
            "dob DATE)";

    String ddlCreateTableCourses = "CREATE TABLE Courses(" +
            "courseId CHAR(12) PRIMARY KEY, " +
            "title VARCHAR(64), " +
            "department CHAR(16)," +
            "program VARCHAR(48))";

    String ddlCreateTableClasses = "CREATE TABLE Classes(" +
            "emplId INT REFERENCES Student(emplId), " +
            "courseId CHAR(12) REFERENCES Schedule(courseId), " +
            "sectionNumber VARCHAR(8) REFERENCES Schedule(sectionNumber), " +
            "year INT, " +
            "semester CHAR(6), " +
            "grade CHAR CHECK(grade = 'A' OR grade = 'B' OR grade = 'C' OR grade = 'D' OR grade = 'F' OR grade = 'W'), " +
            "PRIMARY KEY(emplId, courseId, sectionNumber))";

    String ddlCreateTableAggregateGrades = "CREATE TABLE AggregateGrades(grade CHAR, numberStudents INT)";

    String ddlInsertTableStudents = "INSERT INTO Students VALUES (9991001, 'Name-1', 'M', NULL), " +
            "(9991002, 'Name-2', 'F', NULL), " +
            "(9991003, 'Name-3', 'M', NULL), " +
            "(9991004, 'Name-4', 'F', NULL), " +
            "(9991005, 'Name-5', 'M', NULL), " +
            "(9991006, 'Name-6', 'F', NULL), " +
            "(9991007, 'Name-7', 'M', NULL), " +
            "(9991008, 'Name-8', 'F', NULL), " +
            "(9991009, 'Name-9', 'M', NULL), " +
            "(9991010, 'Name-10', 'M', NULL), " +
            "(9991011, 'Name-11', 'M', NULL), " +
            "(9991012, 'Name-12', 'F', NULL), " +
            "(9991013, 'Name-13', 'F', NULL), " +
            "(9991014, 'Name-14', 'M', NULL), " +
            "(9991015, 'Name-15', 'F', NULL), " +
            "(9991016, 'Name-16', 'F', NULL), " +
            "(9991017, 'Name-17', 'F', NULL), " +
            "(9991018, 'Name-18', 'F', NULL), " +
            "(9991019, 'Name-19', 'F', NULL), " +
            "(9991020, 'Name-20', 'F', NULL), " +
            "(9991021, 'Name-21', 'F', NULL), " +
            "(9991022, 'Name-22', 'M', NULL), " +
            "(9991023, 'Name-23', 'M', NULL), " +
            "(9991024, 'Name-24', 'M', NULL), " +
            "(9991025, 'Name-25', 'F', NULL), " +
            "(9991026, 'Name-26', 'F', NULL), " +
            "(9991027, 'Name-27', 'F', NULL)";

    String ddlInsertTableClasses = "INSERT INTO Classes VALUES (999101, '22100 F', '32131', 2021, 'Spring', 'B'), " +
            "(9991015, '22100 P', '32132', 2021, 'Spring', 'A'), " +
            "(999103, '22100 R', '32150', 2021, 'Spring', 'C'), " +
            "(999104, '22100 F', '32131', 2021, 'Spring', 'C'), " +
            "(999106, '22100 R', '32150', 2021, 'Spring', 'C'), " +
            "(9991012, '22100 R', '32150', 2021, 'Spring', 'D'), " +
            "(999108, '22100 P', '32132', 2021, 'Spring', 'F'), " +
            "(9991011, '22100 R', '32150', 2021, 'Spring', 'W'), " +
            "(9991027, '22100 P', '32132', 2021, 'Spring', 'A'), " +
            "(999109, '22100 P', '32132', 2021, 'Spring', 'B'), " +
            "(999107, '22100 P', '32132', 2021, 'Spring', 'B'), " +
            "(999105, '22100 P', '32132', 2021, 'Spring', 'F'), " +
            "(9991013, '22100 R', '32150', 2021, 'Spring', 'D')";

    String sqlAggregateGrades = "SELECT	grade, count(grade) FROM Classes GROUP BY grade";

    // Static methods

    static String ddlUpDateCourseInstructor(String courseId, String sectionNumber, String nameInstructor){

        return  "UPDATE Schedule" +
                " SET instructor = " + nameInstructor +
                " WHERE courseId = " + courseId + " AND  + " + "sectionNumber = " + sectionNumber;
    }

    static String ddlUpDateInstructor(String nameInstructor, String nameNewInstructor){

        return  "UPDATE Schedule " +
                " SET instructor = " + nameInstructor +
                " WHERE instructor = " + nameNewInstructor;
    }

    static String ddlInsertTableCourses(String nameToTable, String nameFromTable){

        return  "INSERT INTO " + nameToTable +
                " SELECT courseId, title, department, program" +
                " FROM " + nameFromTable;
    }

    static String ddlInsertTableAggregateGrades(String nameToTable, String nameFromTable){

        return "INSERT INTO " + nameToTable +
                " SELECT grade, count(grade) FROM " + nameFromTable +
                " Group By grade ORDER BY grade";
    }
}
