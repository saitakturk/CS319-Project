package client.Menus.RoomLobbyMenu;

import client.Menus.MenuController;
import client.QBitzApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class RoomLobbyMenuController extends MenuController {

    @FXML
    private GridPane playersGridPane;

    @FXML
    private Label roomName;

    @FXML
    private Button startButton;

    @FXML
    private Label roomCodeText;

    @Override
    public void onMessageReceived(String message) {
        JSONObject responseJSON = new JSONObject(message);
        if(responseJSON.getString("responseType").equals("userAnnouncement")){
            Platform.runLater(() -> {
                addPlayers(responseJSON.getJSONArray("userList"));
                if (responseJSON.getBoolean("isStartable")){
                    startButton.setDisable(false);
                }
            });
        }
        else if(responseJSON.getString("responseType").equals("startCounter")){
            Platform.runLater(() -> {
                roomName.setText("Game Starts in " + Integer.toString(responseJSON.getInt("count")));
            });
        }
        else if(responseJSON.getString("responseType").equals("ownerExit")){
            Platform.runLater(() -> {
                QBitzApplication.getSceneController().gotoMenu("RoomMenu");
            });
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() ->{
            addPlayers(payload.getJSONArray("userList"));
            roomName.setText(payload.getString("name"));
            startButton.setVisible(payload.getBoolean("isOwner") ? true : false);
            roomCodeText.setText(payload.getString("roomCode"));
        });
    }

    private void addPlayers(JSONArray playersList){
        int players = playersList.length();
        playersGridPane.getChildren().clear();
        Player player;
        HBox hBox;
        int id;
        int level;
        String name;
        for (int i = 0; i < players; i++){
            JSONObject playerJSON = (JSONObject) playersList.get(i);
            id = playerJSON.getInt("id");
            level = playerJSON.getInt("level");
            name = playerJSON.getString("name");
            player = new Player(id,level,name);
            hBox = new HBox();
            hBox.setStyle("-fx-background-color: #ffffff;");
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().add(new Label(player.getName()));
            playersGridPane.add(hBox,i/4 , i);
        }
    }

    @FXML
    private void sendStartRequest(){
        JSONObject startJSON = new JSONObject();
        startJSON.put("requestType", "startCounter");
        startJSON.put("roomID", payload.getInt("roomID"));
        QBitzApplication.getSceneController().sendMessageToServer(startJSON);
    }

    @FXML
    private void goBack(){
        JSONObject quitJSON = new JSONObject();
        quitJSON.put("requestType", "exitRoom");
        quitJSON.put("roomID", payload.getInt("roomID"));
        QBitzApplication.getSceneController().sendMessageToServer(quitJSON);
        QBitzApplication.getSceneController().gotoMenu("RoomMenu");
    }

    private class Player{
        private int id;
        private int level;
        private String name;

        public Player(int id, int level, String name) {
            this.id = id;
            this.level = level;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public int getLevel() {
            return level;
        }

        public String getName() {
            return name;
        }
    }
}