package scr.draw;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;

import java.util.Iterator;
import java.util.Map;


class XYChartCreator extends Application {

    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    Map<String,Map<Double,Double>> series;
    public XYChartCreator(String title, String xAxisLabel, String yAxisLabel,Map<String, Map<Double, Double>> series){
        this.title=title;
        this.xAxisLabel=xAxisLabel;
        this.yAxisLabel=yAxisLabel;
        this.series=series;
    }
    @Override public void start(Stage stage) {
        stage.setTitle(title);
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(yAxisLabel);
        final javafx.scene.chart.LineChart<Number,Number> lineChart = new javafx.scene.chart.LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle(title);
        Iterator<String> iterator = series.keySet().iterator();
        while (iterator.hasNext()){
            String key=iterator.next();
            Map<Double, Double> seriesMap = series.get(key);
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
        Scene scene  = new Scene(lineChart,800,600);
        stage.setScene(scene);
        stage.show();
    }

    public void draw() {
        launch(new String[0]);
    }
}