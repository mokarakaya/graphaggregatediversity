package scr.draw;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * java fx 2d graph
 */
public class XYChartCreator extends Application implements Runnable {

    XYChartModel model;
    public XYChartCreator(){}
    public XYChartCreator(XYChartModel model){
        this.model=model;
    }

    @Override public void start(Stage stage) throws IOException {

        stage.setTitle(model.title);
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(model.xAxisLabel);
        yAxis.setLabel(model.yAxisLabel);
        final javafx.scene.chart.LineChart<Number,Number> lineChart = new javafx.scene.chart.LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setAnimated(false);
        lineChart.setTitle(model.title);
        Iterator<String> iterator = model.series.keySet().iterator();
        while (iterator.hasNext()){
            String key=iterator.next();
            Map<Double, Double> seriesMap = model.series.get(key);
            javafx.scene.chart.XYChart.Series series = new javafx.scene.chart.XYChart.Series();
            series.setName(key);
            Iterator<Double> seriesMapIterator = seriesMap.keySet().iterator();
            while(seriesMapIterator.hasNext()){
                Double xValue= seriesMapIterator.next();
                Double yValue=seriesMap.get(xValue);
                series.getData().add(new javafx.scene.chart.XYChart.Data(xValue,yValue));
            }
            lineChart.getData().add(series);
        }
        Scene scene  = new Scene(lineChart,800, 600);
        stage.setScene(scene);
        stage.show();
        WritableImage wim = new WritableImage((int) stage.getWidth(),  (int) stage.getHeight());
        scene.snapshot(wim);
        String fileName=model.title+model.xAxisLabel+model.yAxisLabel;
        fileName=fileName.replaceAll(" ","");
        File file = new File("c:/javafx/"+fileName+".png");
        ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);


    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void run() {
        main(null);
    }
}