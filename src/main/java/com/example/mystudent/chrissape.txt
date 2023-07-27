package com.example.database;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

public class MyShapeAgglomerated extends Application{

    // Instant variables
    Integer N;          // Number of events [characters] to display
    Integer M;          // Maximum of events [characters] to display [26]

    double startAngle;  // Starting angle of the pie chart
    double scale;       // Percentage of the canvas width := width of the drawing area
    // of the bar chart
    String Title;       // Book title
    String filename;    // Name of the text file containing the book or database source
    String URL;         // URL of the DBMS driver
    Scanner input;      // Scanner for file reading

    // Inputs to the dialog boxes
    Boolean isPiechart;
    List<String> barchartInputs = new ArrayList<>();
    List<String> piechartInputs = new ArrayList<>();
    List<String> sqlInputs = new ArrayList<>();

    // Set up the left region of the BorderPane
    public VBox addLeftVBox(double widthLeftCanvas, double heightLeftCanvas, TilePane TP, MyColor color){

        // Create a VBox node
        VBox VB = new VBox();
        VB.setPrefWidth(widthLeftCanvas);
        VB.setPadding(new Insets(1));

        // Create label "MyColor Palette"
        Label lblMyColorPalette = new Label("MyColor Palette");
        lblMyColorPalette.setPrefWidth(widthLeftCanvas);
        lblMyColorPalette.setTextFill(MyColor.WHITE.getJavaFXColor());
        lblMyColorPalette.setStyle("-fx-background-color: " + color);   // Hexadecimal code has no opacity component

        // Alternatively
        // lblMyColorPalette.setBackground(new Background(new BackgroundFill(Optional.ofNullable(color).orElse(MyColor.GREY).getJavaFXColor(), CornerRadii.EMPTY, Insets.EMPTY)));

        // Create a MyColorPalette of all MyColor objects and add into the VBox together with the label
        VB.getChildren().addAll(lblMyColorPalette, TP);

        return VB;
    }

    // Set up the top region of the BorderPane
    public HBox addTopHBox(double widthTopCanvas, double heightTopCanvas, double widthCenterCanvas, double heightCenterCanvas,
                           double widthRightCanvas, BorderPane BP, MyColorPalette CP, TilePane TP) throws FileNotFoundException {

        // Create a HBox node
        HBox HB = new HBox();
        HB.setPrefWidth(widthTopCanvas);
        HB.setPadding(new Insets(5, 5, 5, 5));

        String[] nameImages = new String[] {"Circle", "Rectangle", "Intersection", "Pie", "Book", "SQL"};
        String pathFile = "/Users/christianrasmussen/School/Summer 2023/CSC 221/Shapes/";

        Deque<MyShape> stackMyShapes = new ArrayDeque<>();
        for (String nameImage : nameImages){
            String nameFile = pathFile + nameImage + ".PNG";
            ImageView geometricImage = new ImageView(new Image(new FileInputStream(nameFile), heightTopCanvas, heightTopCanvas,true,false));

            // Draw a geometric shape on mouse click: Lambda expression
            geometricImage.setOnMouseClicked(e -> {
                switch (nameImage) {
                    /*case "Arc":     // MyArc object
                        dialogArc(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;

                    case "Line":    // MyLine object
                        dialogLine(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;

                    case "Oval":    // MyOval object
                        dialogOval(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;

                    case "Polygon": // MyPolygon object
                        dialogPolygon(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;*/

                    case "Rectangle":   // MyRectangle object
                        dialogRectangle(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;

                    case "Circle":
                        dialogCircle(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes); break;

                    case "Intersection":// Intersection of 2 MyShape objects
                        dialogIntersection(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;

                    /*case "Subtraction":// Subtraction of 2 MyShape objects
                        dialogSubtraction(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;*/

                    case "Book":        // Book analytics
                        dialogBookAnalytics(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, BP);
                        break;

                    case "SQL":         // Database
                        dialogSQL(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, BP);
                }
            });

            HB.getChildren().add(geometricImage);
        }

        return HB;
    }

    // Create canvases with drawings for the center region of the BorderPane
    public Canvas addCanvasIntersection(double widthCenterCanvas, double heightCenterCanvas, MyShape S1, MyShape S2, MyColor color){

        return S1.drawIntersectMyShapes(widthCenterCanvas, heightCenterCanvas, S1, S2, color);
    }

    /*public Canvas addCanvasBarChart(double widthCanvas, double heightCanvas, HistogramAlphaBet H){

        Canvas CV = new Canvas(widthCanvas, heightCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();

        // Top left corner, width, and height of the drawing area
        MyPoint pTLC = new MyPoint(0.5 * (1.0 - scale) * widthCanvas, 0.375 * heightCanvas, null);
        double widthBarChart = scale * widthCanvas;
        double heightBarChart = 0.575 * heightCanvas;

        // Build the bar chart
        HistogramAlphaBet.MyBarChart barChart = H.new MyBarChart(N, M, pTLC, widthBarChart, heightBarChart);

        // Output the bar chart
        System.out.println("\nBar Chart");
        barChart.bars.forEach((K, V) -> System.out.println(K +  ": " + barChart.bars.get(K)));

        // Draw the bar chart
        barChart.draw(GC);

        double xText = 5.0; double yText = 20.0;
        GC.setStroke(MyColor.LINEN.getJavaFXColor());
        GC.setFont(Font.font ("Georgia", FontPosture.ITALIC, 15));
        GC.strokeText(Title, xText, yText);

        return CV;
    }*/

    public Canvas addCanvasPieChart(double widthCanvas, double heightCanvas, HistogramAlphaBet H){

        Canvas CV = new Canvas(widthCanvas, heightCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();

        // Center point of the canvas and diameter of the pie chart
        MyPoint center = new MyPoint(0.5 * widthCanvas, 0.5 * heightCanvas, null);
        double diameterPieChart = 0.75 * Math.min(widthCanvas, heightCanvas);

        // Canvas color
        MyColor colorCanvas = MyColor.DARKGRAY;

        // Build the pie chart
        HistogramAlphaBet.MyPieChart pieChart = H.new MyPieChart(N, M, center, diameterPieChart, diameterPieChart, startAngle);

        // Output the pie chart
        System.out.println("\nPie Chart");
        pieChart.slices.forEach((K, V) -> System.out.println(K +  ": " + pieChart.slices.get(K)));

        // Check for the sum of slice angles -- must be equal to 360.0
        double sumOfAngles = 0.0;
        for (Character key : pieChart.slices.keySet()){
            sumOfAngles += pieChart.slices.get(key).getArcAngle();
        }
        System.out.println("\nSum of Angles: " + sumOfAngles);

        // Draw the pie chart
        pieChart.draw(GC);

        double xText = 5.0; double yText = 20.0;
        GC.setStroke(MyColor.LINEN.getJavaFXColor());
        GC.setFont(Font.font ("Georgia", FontPosture.ITALIC, 15));
        GC.strokeText(Title, xText, yText);

        return CV;
    }

    // Create canvas for chart legends in the right region of the BorderPane
    public Canvas addCanvasLegend(double widthCanvas, double heightCanvas, HistogramAlphaBet H){

        String information;

        Canvas CV = new Canvas(widthCanvas, heightCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();

        // Paint the background of the canvas
        MyColor colorLeftCanvas = MyColor.LINEN;
        GC.setFill(colorLeftCanvas.getJavaFXColor());
        GC.fillRect(0.0, 0.0, widthCanvas, heightCanvas);

        // Output the character frequencies
        double xText = 20; double yText = 0.03625 * heightCanvas;
        MyColor colorStroke = MyColor.GRAY;
        GC.setStroke(colorStroke.invertColor());
        GC.setFont(Font.font ("Calibri", 13));
        GC.strokeText("Frequency: Cumulative " + H.getCumulativeFrequency(), xText, yText);

        Map<Character, Integer> sortedFrequency = H.sortDownFrequency();

        double yStep = yText;
        for(Character K : sortedFrequency.keySet()){
            yText += yStep;
            information = K + ":\t" + sortedFrequency.get(K);
            GC.strokeText(information, xText, yText);
        }

        return CV;
    }

    // Dialogs

    public void dialogCircle(double widthCenterCanvas, double heightCenterCanvas, BorderPane BP, MyColorPalette CP,
                             TilePane TP, Deque<MyShape> stackMyShapes) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("My Circle");
        dialog.setHeaderText(null);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 100, 10, 10));

        TextField xCenter = new TextField();
        TextField yCenter = new TextField();
        TextField radius = new TextField();

        gridDialog.add(new Label("Center"), 0, 0);
        gridDialog.add(xCenter, 1, 0);
        gridDialog.add(new Label("X-Coordinate as fraction of canvas width"), 2, 0);
        gridDialog.add(yCenter, 1, 1);
        gridDialog.add(new Label("Y-Coordinate as fraction of canvas height"), 2, 1);
        gridDialog.add(new Label("Radius"), 0, 2);
        gridDialog.add(radius, 1, 2);
        gridDialog.add(new Label("Radius as fraction of canvas height"), 2, 2);

        dialog.getDialogPane().setContent(gridDialog);

        Platform.runLater(() -> xCenter.requestFocus());

        List<String> geometricImageInputs =  new ArrayList();
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                geometricImageInputs.add(xCenter.getText());
                geometricImageInputs.add(yCenter.getText());
                geometricImageInputs.add(radius.getText());

                return geometricImageInputs;
            }
            return null;
        });

        Optional<List<String>> Result = dialog.showAndWait();
        Canvas CV = new Canvas(widthCenterCanvas, heightCenterCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();

        Result.ifPresent(event -> {
            MyPoint pTLC = new MyPoint(Double.parseDouble(geometricImageInputs.get(0)) * widthCenterCanvas,
                    Double.parseDouble(geometricImageInputs.get(1)) * heightCenterCanvas, null);
            double rad = Double.parseDouble(geometricImageInputs.get(2)) * heightCenterCanvas;

            TP.setOnMouseClicked(e -> {
                MyColor color = CP.getColorPicked();
                MyCircle C = new MyCircle(pTLC, rad, color);

                GC.clearRect(0, 0, widthCenterCanvas, heightCenterCanvas);
                C.draw(GC);
                C.getMyBoundingRectangle().stroke(GC);

                stackMyShapes.push(C);
            });
            BP.setCenter(CV);
        });
    }


    public void dialogRectangle(double widthCenterCanvas, double heightCenterCanvas, BorderPane BP, MyColorPalette CP, TilePane TP, Deque<MyShape> stackMyShapes){

        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("MyRectangle");
        dialog.setHeaderText(null);

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the dialog's grid
        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 100, 10, 10));

        TextField xPTLC = new TextField(); TextField yPTLC = new TextField();
        TextField width = new TextField();
        TextField height = new TextField();

        gridDialog.add(new Label("Top Left Corner Point"), 0, 0);
        gridDialog.add(xPTLC, 1, 0);
        gridDialog.add(new Label("x-Coordinate as fraction of canvas  width"), 2, 0);
        gridDialog.add(yPTLC, 1, 1);
        gridDialog.add(new Label("y-Coordinate as fraction of canvas height"), 2, 1);
        gridDialog.add(new Label("Width"), 0, 2);
        gridDialog.add(width, 1, 2);
        gridDialog.add(new Label("As fraction of canvas  width"), 2, 2);
        gridDialog.add(new Label("Height"), 0, 3);
        gridDialog.add(height, 1, 3);
        gridDialog.add(new Label("As fraction of canvas height"), 2, 3);

        dialog.getDialogPane().setContent(gridDialog);

        // Request focus on xTLCP field by default
        Platform.runLater(() -> xPTLC.requestFocus());

        // Convert the result to a list when the login button is clicked
        List<String> geometricImageInputs = new ArrayList<>();
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK){
                geometricImageInputs.add(xPTLC.getText()); geometricImageInputs.add(yPTLC.getText());
                geometricImageInputs.add(width.getText()); geometricImageInputs.add(height.getText());
                return geometricImageInputs;
            }
            return null;
        });

        Optional<List<String>> Result = dialog.showAndWait();

        Canvas CV = new Canvas(widthCenterCanvas, heightCenterCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();
        Result.ifPresent(event -> {
            MyPoint pTLC = new MyPoint(Double.parseDouble(geometricImageInputs.get(0))  * widthCenterCanvas, Double.parseDouble(geometricImageInputs.get(1)) * heightCenterCanvas, null);
            double w = Double.parseDouble(geometricImageInputs.get(2)) * widthCenterCanvas;
            double h = Double.parseDouble(geometricImageInputs.get(3)) * heightCenterCanvas;

            // On mouse click: Lambda expression
            TP.setOnMouseClicked(e -> {

                // Pick a color from MyColorPallet
                MyColor color = CP.getColorPicked();
                MyRectangle R =  new MyRectangle(pTLC, w, h, color);

                GC.clearRect(0, 0, widthCenterCanvas, heightCenterCanvas);
                R.draw(GC);                             // Draw R
                R.getMyBoundingRectangle().stroke(GC);  // Stroke the bounding rectangle of R

                stackMyShapes.push(R);
            });

            BP.setCenter(CV);
        });
    }

    public void dialogIntersection(double widthCenterCanvas, double heightCenterCanvas, BorderPane BP, MyColorPalette CP, TilePane TP, Deque<MyShape> stackMyShapes){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Intersection of 2 MyShape Objects");
        dialog.setHeaderText(null);

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the dialog's grid
        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 100, 10, 10));

        gridDialog.add(new Label("Draw the intersection of the last two MyShape objects"), 0, 0);

        dialog.getDialogPane().setContent(gridDialog);
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // On mouse click: Lambda expression
                TP.setOnMouseClicked(e -> {

                    // Pick a color from MyColorPallet
                    MyColor color = CP.getColorPicked();
                    // Pane centerPane = new Pane();

                    MyShape S1 = stackMyShapes.pop();
                    MyShape S2 = stackMyShapes.pop();
                    BP.setCenter(addCanvasIntersection(widthCenterCanvas, heightCenterCanvas, S1, S2, color));
                });
            }
        });
    }


    public void dialogBookAnalytics(double widthCenterCanvas, double heightCenterCanvas, double widthRightCanvas, BorderPane BP){

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Book Analytics");
        dialog.setHeaderText(null);

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the dialog's grid
        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> title = new ComboBox<>();
        title.getItems().addAll("Alice in Wonderland",
                "A Tale of Two Cities",
                "David Copperfield",
                "Oliver Twist",
                "Emma",
                "Pride and Prejudice",
                "Moby Dick",
                "War and Peace",
                "xWords");

        gridDialog.add(new Label("Title"), 0, 0);
        gridDialog.add(title, 1, 0);

        dialog.getDialogPane().setContent(gridDialog);

        // Request focus on numberEvents field by default.
        Platform.runLater(() -> title.requestFocus());

        // Convert the result to a list when the login button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK){
                this.Title =  title.getValue(); return Title;
            }
            return null;
        });

        Optional<String> Result = dialog.showAndWait();

        Result.ifPresent(event -> {
            this.filename = "/Users/christianrasmussen/Downloads/" + Title + ".txt";
            this.Title = "Book Title: " + this.Title;

            // Open, read, and close file
            openFile();
            String w = readFile();
            closeFile();

            HistogramAlphaBet H = new HistogramAlphaBet(w);

            System.out.println(H);

            // Custom dialog for chart selection
            try{
                toggleGroupChart(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, H, BP);
            }
            catch(FileNotFoundException e) { throw new RuntimeException(e); }
        });
    }

    public void dialogSQL(double widthCenterCanvas, double heightCenterCanvas, double widthRightCanvas, BorderPane BP){

        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("SQL");
        dialog.setHeaderText(null);

        ToggleGroup groupSQL = new ToggleGroup();

        RadioButton radioMSSQL = new RadioButton("MS SQL");
        radioMSSQL.setToggleGroup(groupSQL);

        RadioButton radioMySQL = new RadioButton("MySQL");
        radioMySQL.setToggleGroup(groupSQL);

        RadioButton radioPostgreSQL = new RadioButton("Postgre SQL");
        radioPostgreSQL.setToggleGroup(groupSQL);

        TextField userName = new TextField();
        TextField passWord = new TextField();
        ComboBox<String> schema = new ComboBox<>();
        schema.getItems().addAll("Students",
                "New York CityBike",
                "Family Relations");

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the dialog's grid
        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 150, 10, 10));

        gridDialog.add(new Label("DBMS"), 0, 1);
        gridDialog.add(radioMSSQL, 1, 1);
        gridDialog.add(radioMySQL, 2, 1);
        gridDialog.add(radioPostgreSQL, 4, 1);
        gridDialog.add(new Label("Schema"), 0, 2);
        gridDialog.add(schema, 1, 2);

        gridDialog.add(new Label("Username"), 0, 4);
        gridDialog.add(userName, 1, 4);
        gridDialog.add(new Label("Password"), 0, 5);
        gridDialog.add(passWord, 1, 5);

        dialog.getDialogPane().setContent(gridDialog);

        // Request focus on radioMySQL button by default
        Platform.runLater(() -> radioMySQL.requestFocus());

        // Convert the result to a list when the login button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK){
                String DBMS = ((RadioButton) groupSQL.getSelectedToggle()).getText();
                switch (DBMS){
                    case "MySQL":
                        URL = "jdbc:mysql://localhost:3306/Students?allowLoadLocalInfile=true";
                        break;

                    case "MS SQL":
                        URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:52188";
                        break;

                    case "Postgre SQL":
                        URL = "jdbc:postgresql://localhost:5432/postgres";
                }
                System.out.println(URL);

                sqlInputs.add(URL); sqlInputs.add(userName.getText()); sqlInputs.add(passWord.getText()); sqlInputs.add(schema.getValue());
                return sqlInputs;
            }
            return null;
        });

        Optional<List<String>> Result = dialog.showAndWait();

        Result.ifPresent(event -> {
            // Create StudentsDatabase and Connection objects
            String url =  sqlInputs.get(0);
             String username = sqlInputs.get(1);
            String password = sqlInputs.get(2);

            System.out.println(url + " " + username + " " + password);

            String Schema = sqlInputs.get(3);
            this.Title = "Database: " + Schema;

            switch (Schema){
                case "Students":
                    filename = "/Users/christianrasmussen/School/Summer 2023/CSC 221/TXT files/Class Schedule Computer Science Department Spring 2021.txt";
                    HistogramAlphaBet H = MySQLStudentsDatabase(url, username, password, filename);
                    //System.out.println(H);
                    // Custom dialog for chart selection
                    try{
                        toggleGroupChart(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, H, BP);
                    }
                    catch(FileNotFoundException e) { throw new RuntimeException(e); }
                    break;

                case "NY CityBke":
                    break;

                case "Family Relations":
            }
        });
    }

    public HistogramAlphaBet MySQLStudentsDatabase(String url, String username, String password, String filename){

        // Create a StudentDatabase object
        StudentsDatabase DB = new StudentsDatabase(url, username, password);

        // Create-populate Table Schedule
        String nameTable;
        nameTable = StudentsDatabaseInterface.SCHEMA + ".Schedule";
        try{
            StudentsDatabase.Schedule schedule = DB.new Schedule(filename, nameTable);
        }
        catch(SQLException e) { throw new RuntimeException(e); }

        // Create-populate Table Courses
        String nameToTable = StudentsDatabaseInterface.SCHEMA + ".Courses";
        String nameFromTable = StudentsDatabaseInterface.SCHEMA + ".Schedule";
        try{
            StudentsDatabase.Courses courses = DB.new Courses(nameToTable, nameFromTable);
        }
        catch(SQLException e) { throw new RuntimeException(e); }

        // Create-populate Table Students
        nameTable = StudentsDatabaseInterface.SCHEMA + ".Students";
        try{
            StudentsDatabase.Students students = DB.new Students(nameTable);
        }
        catch(SQLException e) { throw new RuntimeException(e); }

        // Create-populate Table Classes
        nameTable = StudentsDatabaseInterface.SCHEMA + ".Classes";
        try{
            StudentsDatabase.Classes classes = DB.new Classes(nameTable);
        }
        catch(SQLException e) { throw new RuntimeException(e); }

        // Create-populate Table AggregateGrades
        nameToTable = StudentsDatabaseInterface.SCHEMA + ".AggregateGrades";
        nameFromTable = StudentsDatabaseInterface.SCHEMA + ".Classes";
        StudentsDatabase.AggregateGrades aggregateGrades;
        try{
            aggregateGrades = DB.new AggregateGrades(nameToTable, nameFromTable);
        }
        catch(SQLException e) { throw new RuntimeException(e); }

        Map<Character, Integer> AG =  aggregateGrades.getAggregateGrades(nameToTable);
        System.out.println("\nAggregate Grades: " + AG);

        System.out.println(AG.toString());
        // Create-return a histogram
        return new HistogramAlphaBet(AG);

        /*
        // Alternatively apply the constructor using the ResultSet object
        ResultSet RS = aggregateGrades.getAggregateGrades(connection, nameToTable);
        return new HistogramAlphaBet(RS);
        */
    }

    public void toggleGroupChart(double widthCenterCanvas, double heightCenterCanvas, double widthRightCanvas,
                                 HistogramAlphaBet H, BorderPane BP) throws FileNotFoundException {

        ToggleGroup groupChart = new ToggleGroup();

        RadioButton radioBarchart = new RadioButton();
        radioBarchart.setToggleGroup(groupChart);

        RadioButton radioPiechart = new RadioButton();
        radioPiechart.setToggleGroup(groupChart);

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Chart Picker");
        dialog.setHeaderText(null);

        // Create the dialog's grid
        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 150, 10, 10));

        /*String pathFile = "C:\\Users\\Admin\\Desktop\\CCNY Classes\\CSc 22100 Software Design Laboratory\\Lectures\\Intellij Projects\\MyShape\\Geometric Shapes\\";
        String nameFile = pathFile + "Barchart" + ".PNG";
        gridDialog.add(new ImageView(new Image(new FileInputStream(nameFile), 100, 100,true,false)), 0, 1);
        gridDialog.add(radioBarchart, 1, 1);

        nameFile = pathFile + "Piechart" + ".PNG";
        gridDialog.add(new ImageView(new Image(new FileInputStream(nameFile), 100, 100,true,false)), 2, 1);*/
        gridDialog.add(radioPiechart, 3, 1);

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(gridDialog);

        // Request focus on and select radioPieChart by default
        Platform.runLater(() -> radioPiechart.setSelected(true));

        // Convert the result to a Boolean when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK){
                this.isPiechart = radioPiechart.isSelected();
                if (this.isPiechart) {
                    dialogPiechart(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, H, BP);
                }
            }
            return null;
        });

        Optional<Boolean> Result = dialog.showAndWait();
    }


    /*public void dialogBarchart(double widthCenterCanvas, double heightCenterCanvas, double widthRightCanvas, HistogramAlphaBet H, BorderPane BP){

        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Bar Chart");
        dialog.setHeaderText(null);

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the dialog's grid
        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 150, 10, 10));

        TextField numberEvents = new TextField();
        TextField totalNumberEvents = new TextField();
        TextField Scale = new TextField();

        gridDialog.add(new Label("Display"), 0, 0);
        gridDialog.add(numberEvents, 1, 0);
        gridDialog.add(new Label("Total"), 2, 0);
        gridDialog.add(totalNumberEvents, 3, 0);
        gridDialog.add(new Label("Scale"), 0, 1);
        gridDialog.add(Scale, 1, 1);

        dialog.getDialogPane().setContent(gridDialog);

        // Request focus on numberEvents field by default.
        Platform.runLater(() -> numberEvents.requestFocus());

        // Convert the result to a list when the login button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK){
                barchartInputs.add(numberEvents.getText()); barchartInputs.add(totalNumberEvents.getText());
                barchartInputs.add(Scale.getText());
                return barchartInputs;
            }
            return null;
        });

        Optional<List<String>> Result = dialog.showAndWait();

        Result.ifPresent(event -> {
            this.N = Integer.parseInt(barchartInputs.get(0));
            this.M = Integer.parseInt(barchartInputs.get(1));
            this.scale = Double.parseDouble(barchartInputs.get(2));

            Canvas CV = addCanvasBarChart(widthCenterCanvas, heightCenterCanvas, H);
            BP.setAlignment(CV, Pos. TOP_CENTER); BP.setCenter(CV);

            BP.setRight(addCanvasLegend(widthRightCanvas, heightCenterCanvas, H));
        });
    }
*/

    public void dialogPiechart(double widthCenterCanvas, double heightCenterCanvas, double widthRightCanvas, HistogramAlphaBet H, BorderPane BP){

        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Pie Chart");
        dialog.setHeaderText(null);

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the dialog's grid
        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 150, 10, 10));

        TextField numberEvents = new TextField();
        TextField totalNumberEvents = new TextField();
        TextField startingAngle = new TextField();

        gridDialog.add(new Label("Display"), 0, 0);
        gridDialog.add(numberEvents, 1, 0);
        gridDialog.add(new Label("Total"), 2, 0);
        gridDialog.add(totalNumberEvents, 3, 0);
        gridDialog.add(new Label("Starting Angle"), 0, 1);
        gridDialog.add(startingAngle, 1, 1);

        dialog.getDialogPane().setContent(gridDialog);

        // Request focus on numberEvents field by default.
        Platform.runLater(() -> numberEvents.requestFocus());

        // Convert the result to a list when the login button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK){
                piechartInputs.add(numberEvents.getText()); piechartInputs.add(totalNumberEvents.getText());
                piechartInputs.add(startingAngle.getText());
                return piechartInputs;
            }
            return null;
        });

        Optional<List<String>> Result = dialog.showAndWait();

        Result.ifPresent(event -> {
            this.N = Integer.parseInt(piechartInputs.get(0));
            this.M = Integer.parseInt(piechartInputs.get(1));
            this.startAngle = Double.parseDouble(piechartInputs.get(2));

            Canvas CV = addCanvasPieChart(widthCenterCanvas, heightCenterCanvas, H);
            BP.setAlignment(CV, Pos. TOP_CENTER); BP.setCenter(CV);

            BP.setRight(addCanvasLegend(widthRightCanvas, heightCenterCanvas, H));
        });
    }

    // Open, read, then close file
    public void openFile(){

        try{
            input = new Scanner(Paths.get(filename));
        }
        catch(IOException ioException){ System.err.println("File is not found"); }
    }

    public String readFile(){ // Read file

        String w = "";

        try{
            // Read-in the file, taking out all not alphabet character
            while (input.hasNext()) {
                w += input.nextLine().replaceAll("[^a-zA-Z]", "").toLowerCase();
            }
        }
        // Catch an exception
        catch(NoSuchElementException elementException) {
            System.err.println("Invalid input! Terminating....");
        }
        // Catch an exception
        catch(IllegalStateException stateException) {
            System.out.println("Error processing file! Terminating....");
        }

        return w;
    }

    public void closeFile(){

        if (input != null) input.close();
    }

    @Override
    public void start(Stage PS) throws FileNotFoundException {

        // Set up canvas and BorderPane dimensions
        double widthCenterCanvas = 500.00;
        double heightCenterCanvas = 400.0;

        BorderPane BP = new BorderPane();

        double widthLeftVBox = 0.50 * widthCenterCanvas;        //250
        double heightTopHBox = 0.225 * heightCenterCanvas;      //90
        double widthRightCanvas = 0.40 * widthCenterCanvas;     //200

        double widthBorderPane = widthCenterCanvas + widthLeftVBox + widthRightCanvas;      //950
        double heightBorderPane = heightCenterCanvas + heightTopHBox;                       //490

        MyColorPalette CP = new MyColorPalette(widthLeftVBox, heightCenterCanvas);
        TilePane TP = CP.getPalette();

        BP.setTop(addTopHBox(heightBorderPane, heightTopHBox, widthCenterCanvas, heightCenterCanvas, widthRightCanvas, BP, CP, TP));
        BP.setLeft(addLeftVBox(widthLeftVBox, heightCenterCanvas, TP, MyColor.BLACK));

        Scene SC = new Scene(BP, widthBorderPane, heightBorderPane, MyColor.WHITE.getJavaFXColor());
        PS.setTitle("MyShape!");
        PS.setResizable(false);
        PS.setScene(SC);
        PS.show();
    }

    public static void main(String [] args){ launch(args); }
}
