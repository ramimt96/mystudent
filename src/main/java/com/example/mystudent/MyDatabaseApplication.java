package com.example.mystudent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

public class MyDatabaseApplication extends Application {
    Integer N;              // Number of events [chars] to display
    Integer M;              // Max of events [chars] to display [26]
    double startAngle;      // Starting angle of the pie chart

    String Title;           // Book title
    String filename;        // name of text file containing book or database
    String URL;             // URL of DBMS driver
    Scanner input;          // Scanner for file reading

    //Inputs to dialog boxes
    boolean isBook, isSQL;
    List<String> piechartInputs = new ArrayList();
    List<String> sqlInputs = new ArrayList<>();

    public VBox addLeftVBox(double widthLeftCanvas, double heightCanvas, TilePane TP, MyColor color){
        VBox VB = new VBox();
        VB.setPrefWidth(widthLeftCanvas);
        VB.setPadding(new Insets(5));

        Label lblMyColorPalette = new Label("MyColor Palette");
        lblMyColorPalette.setPrefWidth(widthLeftCanvas);
        lblMyColorPalette.setTextFill(MyColor.WHITE.getJavaFXColor());
        lblMyColorPalette.setBackground(new Background(new BackgroundFill(Optional.ofNullable(color)
                .orElse(MyColor.GREY).getJavaFXColor(), CornerRadii.EMPTY, Insets.EMPTY)));

        VB.getChildren().addAll(lblMyColorPalette, TP);

        return VB;
    }

    public HBox addTopHBox(double widthTopCanvas, double heightTopCanvas, double widthCenterCanvas,
                           double heightCenterCanvas, double widthRightCanvas ,BorderPane BP, MyColorPalette CP,
                           TilePane TP) throws FileNotFoundException {

        //Create a HBox node
        HBox HB = new HBox();
        HB.setPrefWidth(widthTopCanvas);
        HB.setPadding(new Insets(5, 5, 5, 5));
        HB.setSpacing(120);

        String[] nameImages = new String[] {"Circle", "Rectangle", "Intersection", "Book", "SQL"};
        String pathFile = "C:\\Users\\rtara\\OneDrive\\Documents\\CCNY\\2023 Summer Term\\CSC 221 Software Design" +
                "\\Assignment 4 - mystudent\\mystudent\\Shapes\\";

        Deque<MyShape> stackMyShapes = new ArrayDeque<>();
        for (String nameImage : nameImages) {
            String nameFile = pathFile + nameImage + ".PNG";
            ImageView geometricImage = new ImageView(new Image(new FileInputStream(nameFile), heightTopCanvas,
                    heightTopCanvas, true, false));
            geometricImage.setPreserveRatio(true);
            geometricImage.setFitHeight(70);

            //draw a geometric shape on mouse click; lambda expression
            geometricImage.setOnMouseClicked(e -> {
                switch (nameImage) {
                    case "Circle":
                        dialogCircle(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;

                    case "Rectangle":
                        dialogRectangle(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;

                    case "Intersection":
                        dialogIntersection(widthCenterCanvas, heightCenterCanvas, BP, CP, TP, stackMyShapes);
                        break;

                    case "Book":
                        dialogBookAnalysis(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, BP);
                        break;

                    case "SQL":
                        dialogSQL(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, BP);
                }
            });
            HB.getChildren().add(geometricImage);
        }
        return HB;
    }

    public Canvas addCenterCanvas(double widthCenterCanvas, double heightCenterCanvas,
                                  MyShape S1, MyShape S2, MyColor color){
        return S1.drawIntersectMyShapes(widthCenterCanvas, heightCenterCanvas, S1, S2, color);
    }

    public Canvas addCanvasPieChart(double widthCanvas, double heightCanvas, HistogramAlphaBet H){
        Canvas CV = new Canvas(widthCanvas, heightCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();

        MyPoint center = new MyPoint(0.5 * widthCanvas, 0.5 * heightCanvas, null);
        double diameterPieChart = 0.75 * Math.min(widthCanvas, heightCanvas);

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

    public Canvas addCanvasLegend(double widthCanvas, double heightCanvas, HistogramAlphaBet H, String hType){
        String information;

        Map<Character, Integer> sortedFrequency;
        if ("Book".equals(hType)) {
            sortedFrequency = H.sortDownFrequency();
        } else if ("SQL".equals(hType)) {
            sortedFrequency = H.sortAlphabetically();
        } else {
            sortedFrequency = H.sortDownFrequency();
        }


        // Map<Character, Integer> sortedFrequency = H.sortDownFrequency();

        Canvas CV = new Canvas(widthCanvas, heightCanvas);
        GraphicsContext GC = CV.getGraphicsContext2D();

        //paint background of canvas
        MyColor colorRightCanvas = MyColor.LINEN;
        GC.setFill(colorRightCanvas.getJavaFXColor());
        GC.fillRect(0.0, 0.0, widthCanvas, heightCanvas);

        //output character frequencies
        double xText = 20; double yText = 0.03625 * heightCanvas;
        MyColor colorStroke = MyColor.GRAY;
        GC.setStroke(colorStroke.invertColor());
        GC.setFont(Font.font ("Calibri", 13));
        GC.strokeText("Frequency: Cumulative " + H.getCumulativeFrequency(), xText, yText);

        //output calculated and sum of frequencies
        System.out.println("\nFrequency of Characters");
        sortedFrequency.forEach((K, V) -> System.out.println(K + ": " + V));
        System.out.println("\nCumulative Frequency: " + H.getCumulativeFrequency());

        //output calculated and sum of probabilities
        System.out.println("\nSorted Probability of Characters");
        System.out.println(H.sortDownProbability());
        System.out.println("\nSum of Probabilities: " + H.getSumOfProbability());

        double yStep = yText;
        for(Character K : sortedFrequency.keySet()){
            yText += yStep;
            information = K + ":\t" + sortedFrequency.get(K);
            GC.strokeText(information, xText, yText);
        }

        return CV;
    }


    public void dialogRectangle(double widthCenterCanvas, double heightCenterCanvas, BorderPane BP, MyColorPalette CP,
                                TilePane TP, Deque<MyShape> stackMyShapes) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("My Rectangle");
        dialog.setHeaderText(null);

        //set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        //create the dialog labels and fields
        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 100, 10, 10));

        TextField xPTLC = new TextField();
        TextField yPTLC = new TextField();
        TextField width = new TextField();
        TextField height = new TextField();

        gridDialog.add(new Label("Top Left Corner Point"), 0, 0);
        gridDialog.add(xPTLC, 1, 0);
        gridDialog.add(new Label("X-Coordinate as fraction of canvas width"), 2, 0);
        gridDialog.add(yPTLC, 1, 1);
        gridDialog.add(new Label("Y-Coordinate as fraction of canvas height"), 2, 1);
        gridDialog.add(new Label("Width"), 0, 2);
        gridDialog.add(width, 1, 2);
        gridDialog.add(new Label("Width as fraction of canvas width"), 2, 2);
        gridDialog.add(new Label("Height"), 0, 3);
        gridDialog.add(height, 1, 3);
        gridDialog.add(new Label("Height as fraction of canvas height"), 2, 3);

        dialog.getDialogPane().setContent(gridDialog);

        //request focus on xTLCP field by default
        Platform.runLater(() -> xPTLC.requestFocus());

        //convert the result to a list when the login button is clicked
        List<String> geometricImageInputs =  new ArrayList();
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                geometricImageInputs.add(xPTLC.getText());
                geometricImageInputs.add(yPTLC.getText());
                geometricImageInputs.add(width.getText());
                geometricImageInputs.add(height.getText());
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
            double w = Double.parseDouble(geometricImageInputs.get(2)) * widthCenterCanvas;
            double h = Double.parseDouble(geometricImageInputs.get(3)) * heightCenterCanvas;

            //on mouse click: lambda expression
            TP.setOnMouseClicked(e -> {
                //pick color from MyColorPalette
                MyColor color = CP.getColorPicked();
                MyRectangle R = new MyRectangle(pTLC, w, h, color);

                GC.clearRect(0, 0, widthCenterCanvas, heightCenterCanvas);
                R.draw(GC);                                 //draw R
                R.getMyBoundingRectangle().stroke(GC);      //stroke the bounding rectangle of R

                stackMyShapes.push(R);
            });
            BP.setCenter(CV);
        });
    }

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

    public void dialogIntersection(double widthCenterCanvas, double heightCenterCanvas, BorderPane BP,
                                   MyColorPalette CP, TilePane TP, Deque<MyShape> stackMyShapes) {
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Intersection of 2 MyShape Objects");
        dialog.setHeaderText(null);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 100, 10, 10));

        gridDialog.add(new Label("Draw the intersection of the last two MyShape objects"), 0, 0);

        dialog.getDialogPane().setContent(gridDialog);
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                TP.setOnMouseClicked(e -> {
                    MyColor color = CP.getColorPicked();
                    String tileId = color.toString();

                    for (Node tile : TP.getChildren()) {
                        if (tile.getId() == tileId) {
                            Pane centerPane = new Pane();

                            MyShape S1 = stackMyShapes.pop();
                            MyShape S2 = stackMyShapes.pop();
                            centerPane.getChildren().add(addCenterCanvas(widthCenterCanvas, heightCenterCanvas,
                                    S1, S2, color));
                            BP.setCenter(centerPane);
                            break;
                        }
                    }
                });
            }
        });
    }

    public void dialogBookAnalysis(double widthCenterCanvas, double heightCenterCanvas, double widthRightCanvas, BorderPane BP){
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

        ComboBox<String> title = new ComboBox();
        title.getItems().addAll("Alice in Wonderland", "A Tale of Two Cities", "David Copperfield",
                "Emma", "Moby Dick", "Oliver Twist", "Pride and Prejudice", "War and Peace", "xWords");

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
            this.filename = "C:\\Users\\rtara\\OneDrive\\Documents\\CCNY\\2023 Summer Term\\CSC 221 Software Design" +
                    "\\Assignment 4 - mystudent\\mystudent\\Texts\\" + Title + ".txt";
            this.Title = "Book Title: " + this.Title;

            // Open, read, and close file
            openFile();
            String w = readFile();
            closeFile();

            HistogramAlphaBet H = new HistogramAlphaBet(w);

            System.out.println(H);

            // Custom dialog for chart selection
            dialogPiechart(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, H, BP, "Book");

            /*try{
                dialogPiechart(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, H, BP);
            }
            catch(FileNotFoundException e) { throw new RuntimeException(e); }*/
        });
    }

    public void dialogSQL(double widthCenterCanvas, double heightCenterCanvas, double widthRightCanvas, BorderPane BP) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("SQL");
        dialog.setHeaderText(null);

        ToggleGroup groupSQL = new ToggleGroup();

        RadioButton radioMySQL = new RadioButton("MySQL");
        radioMySQL.setToggleGroup(groupSQL);

        /*
        RadioButton radioMSSQL = new RadioButton("MS SQL");
        radioMSSQL.setToggleGroup(groupSQL);
        RadioButton radioPostgreSQL = new RadioButton("Postgre SQL");
        radioPostgreSQL.setToggleGroup(groupSQL);
        */

        TextField userName = new TextField();
        PasswordField passWord = new PasswordField();
        ComboBox<String> schema = new ComboBox<>();
        schema.getItems().addAll("Students");

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the dialog's grid
        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 150, 10, 10));

        gridDialog.add(new Label("DBMS"), 0, 1);
        //gridDialog.add(radioMSSQL, 2, 1);
        gridDialog.add(radioMySQL, 1, 1);
        //gridDialog.add(radioPostgreSQL, 4, 1);
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

                    /*
                    case "MS SQL":
                        URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:52188";
                        break;

                    case "Postgre SQL":
                        URL = "jdbc:postgresql://localhost:5432/postgres";
                    */
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
                    filename = "\"C:\\Users\\rtara\\OneDrive\\Documents\\CCNY\\2023 Summer Term\\" +
                            "CSC 221 Software Design\\Assignment 4 - mystudent\\mystudent\\Texts\\Schedule.txt\"";
                    HistogramAlphaBet H = MySQLStudentsDatabase(url, username, password, filename);

                    // Custom dialog for chart selection
                    /*try{
                        dialogPiechart(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, H, BP);
                    }
                    catch(FileNotFoundException e) { throw new RuntimeException(e); }*/

                    dialogPiechart(widthCenterCanvas, heightCenterCanvas, widthRightCanvas, H, BP, "SQL");
                    break;
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

        // Create-return a histogram
        return new HistogramAlphaBet(AG);

        /*
        // Alternatively apply the constructor using the ResultSet object
        ResultSet RS = aggregateGrades.getAggregateGrades(connection, nameToTable);
        return new HistogramAlphaBet(RS);
        */
    }


    public void dialogPiechart(double widthCenterCanvas, double heightCenterCanvas, double widthRightCanvas,
                               HistogramAlphaBet H, BorderPane BP, String hType) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Pie Chart");
        dialog.setHeaderText(null);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridDialog = new GridPane();
        gridDialog.setHgap(10);
        gridDialog.setVgap(10);
        gridDialog.setPadding(new Insets(20, 150, 10, 10));

        TextField numberEvents = new TextField();
        TextField totalNumberEvents = new TextField();
        TextField startingAngle = new TextField();

        gridDialog.add(new Label("Display"), 0, 0);
        gridDialog.add(numberEvents, 1, 0);
        gridDialog.add(new Label("Number of events to display"), 2, 0);
        gridDialog.add(new Label("Total"), 0, 1);
        gridDialog.add(totalNumberEvents, 1, 1);
        gridDialog.add(new Label("Max number of events"), 2, 1);
        gridDialog.add(new Label("Starting Angle"), 0, 2);
        gridDialog.add(startingAngle, 1, 2);
        gridDialog.add(new Label("Starting angle of first slice [in degrees]"), 2, 2);

        dialog.getDialogPane().setContent(gridDialog);

        //Request focus on numberEvents field by default
        Platform.runLater(() -> numberEvents.requestFocus());

        //Convert result to Boolean when OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == dialogButton.OK) {
                piechartInputs.add(numberEvents.getText());
                piechartInputs.add(totalNumberEvents.getText());
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

            BP.setRight(addCanvasLegend(widthRightCanvas, heightCenterCanvas, H, hType));
        });
    }


    public void openFile() {
        try {
            input = new Scanner(Paths.get(filename));
        } catch (IOException ioException) {
            System.err.println("File is not found");
        }
    }

    public String readFile()  {
        String w = "";

        try {
            //Read in file, taking out all non-alphabet characters
            while (input.hasNext()) {
                w += input.nextLine().replaceAll("[^a-zA-Z]", "").toLowerCase();
            }
        }
        catch (NoSuchElementException elementException) {
            System.err.println("Invalid input! Terminating...");
        }
        catch (IllegalStateException stateException) {
            System.err.println("Error processing file! Terminating...");
        }
        return w;
    }

    public void closeFile() {
        if (input != null) input.close();
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        BorderPane BP = new BorderPane();

        double widthCanvas = 1000.0;
        double heightCanvas = 500.0;

        double widthLeftCanvas = 0.3 * widthCanvas;
        double heightTopCanvas = 0.15 * heightCanvas;
        double widthRightCanvas = 0.25 * widthCanvas;

        double widthCenterCanvas = widthCanvas - widthLeftCanvas - widthRightCanvas;
        double heightCenterCanvas = heightCanvas - heightTopCanvas;

        MyColorPalette CP = new MyColorPalette(widthLeftCanvas, heightCenterCanvas);
        TilePane TP = CP.getPalette();

        BP.setTop(addTopHBox(widthCanvas, heightTopCanvas, widthCenterCanvas, heightCenterCanvas, widthRightCanvas, BP, CP, TP));
        BP.setLeft(addLeftVBox(widthLeftCanvas, heightCenterCanvas, TP, MyColor.BLACK));

        Scene SC = new Scene(BP, widthCanvas, heightCanvas, null);
        stage.setTitle("MyApplication!");
        stage.setScene(SC);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}


