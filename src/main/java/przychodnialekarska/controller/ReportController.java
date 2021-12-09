package przychodnialekarska.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import przychodnialekarska.DatabaseManager;
import przychodnialekarska.objectClass.Usluga;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    @FXML
    private PieChart mostDonePieChart;

    @FXML
    private ListView listView;

    @FXML
    private Label totalProfitLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDatabase();

    }

    private void loadDatabase(){
        HashMap<Integer, Usluga> hashMapService = new HashMap<Integer, Usluga>();
        HashMap<Integer, Integer> hashMapServiceCount = new HashMap<Integer, Integer>();
        try{
            Connection c = DatabaseManager.getConnection();
            Statement statement = c.createStatement();
            String sql = "SELECT * from USLUGI";
            ResultSet res = statement.executeQuery(sql);
            while(res.next()){
                hashMapService.put(res.getInt("id_uslugi"), new Usluga(res.getInt("id_uslugi"), res.getString("nazwa"),res.getString("opis"),res.getDouble("koszt")));
                hashMapServiceCount.put(res.getInt("id_uslugi"), 0);
            }
            statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM SZCZEGOLY_WIZYT");
            while(resultSet.next()){
                increment(hashMapServiceCount, resultSet.getInt("id_uslugi"));
            }
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            double profit = 0;
            for(Map.Entry me : hashMapService.entrySet()){
                listView.getItems().add(hashMapService.get(me.getKey()).getNameService() + " - " + (hashMapService.get(me.getKey()).getCostService() * hashMapServiceCount.get(me.getKey())));
                profit += hashMapService.get(me.getKey()).getCostService() * hashMapServiceCount.get(me.getKey());
                pieChartData.add(new PieChart.Data(hashMapService.get(me.getKey()).getNameService() + " (" + hashMapServiceCount.get(me.getKey()) + ")" ,hashMapServiceCount.get(me.getKey())));
            }
            mostDonePieChart.getData().addAll(pieChartData);
            totalProfitLabel.setText(profit + " PLN");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static<K> void increment(Map<K, Integer> map, K key)
    {
        Integer count = map.getOrDefault(key, 0);
        map.put(key, count + 1);
    }
}
