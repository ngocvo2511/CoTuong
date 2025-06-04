package com.example.cotuong.controller;

import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.chesslogic.EndReason;
import com.example.cotuong.chesslogic.gamestate.GameState;
import com.example.cotuong.chesslogic.Result;
import com.example.cotuong.utils.Sounds;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class GameOverController {

    @FXML private Label winnerText;
    @FXML private Label reasonText;
    @FXML private Button newGameButton;
    @FXML private Button mainMenuButton;
    @FXML private Button replayButton;

    private AnchorPane gameOverPane;
    private OfflineGameController offlineGameController;
    private OnlineGameController onlineGameController;
    private GameOverCallback callback;

    public interface GameOverCallback {
        void onNewGame();
        void onMainMenu();
        void onReplay() throws Exception;
    }

    @FXML
    public void initialize() {
        // Optional: kiểm tra inject thành công
        assert winnerText != null : "winnerText was not injected!";
        assert newGameButton != null : "newGameButton was not injected!";
    }

    public void initialize(GameState gameState, GameOverCallback callback) {
        this.callback = callback;
        Result result = gameState.getResult();
        winnerText.setText(getWinnerText(result.getWinner()));
        reasonText.setText(getReasonText(result.getReason(), gameState.currentPlayer));
    }

    public void initialize(Result result, Player currentPlayer, GameOverCallback callback) {
        this.callback = callback;
        winnerText.setText(getWinnerText(result.getWinner()));
        reasonText.setText(getReasonText(result.getReason(), currentPlayer));
    }

    public void setGameOverPane(AnchorPane gameOverPane) {
        this.gameOverPane = gameOverPane;
    }

    public void setOfflineController(OfflineGameController parentController) {
        this.offlineGameController = parentController;
    }

    public void setOnlineController(OnlineGameController parentController) {
        this.onlineGameController = parentController;
    }



    private String getWinnerText(Player winner) {
        String text = switch (winner) {
            case RED -> "ĐỎ THẮNG!";
            case BLACK -> "ĐEN THẮNG!";
            default -> "HÒA!";
        };

        // Set the appropriate style class
        if (winner == Player.RED) {
            winnerText.getStyleClass().add("red-winner");
            winnerText.getStyleClass().remove("black-winner");
            winnerText.getStyleClass().remove("draw-result");
        } else if (winner == Player.BLACK) {
            winnerText.getStyleClass().add("black-winner");
            winnerText.getStyleClass().remove("red-winner");
            winnerText.getStyleClass().remove("draw-result");
        } else {
            winnerText.getStyleClass().add("draw-result");
            winnerText.getStyleClass().remove("red-winner");
            winnerText.getStyleClass().remove("black-winner");
        }

        return text;
    }

    private String playerString(Player player) {
        return switch (player) {
            case RED -> "Đỏ";
            case BLACK -> "Đen";
            default -> "";
        };
    }

    private String getReasonText(EndReason reason, Player currentPlayer) {
        return switch (reason) {
            case STALEMATE -> playerString(currentPlayer) + " hết nước đi";
            case CHECKMATE -> playerString(currentPlayer) + " bị chiếu bí";
            case INSUFFICIENT_MATERIAL -> "Hòa vì thiếu quân";
            case FIFTY_MOVE_RULE -> "Hòa vì 50 nước không ăn quân";
            case THREEFOLD_REPETITION -> "Hòa vì lặp lại nước đi 3 lần";
            case TIMEFORFEIT -> playerString(currentPlayer) + " hết thời gian";
            case PLAYER_DISCONNECTED -> playerString(currentPlayer) + " đã thoát";
            default -> "";
        };
    }

    @FXML
    private void handleNewGame() {
        Sounds.playButtonClickSound();
        if (callback != null) {
            callback.onNewGame();
        }
    }

    @FXML
    private void handleMainMenu() {
        Sounds.playButtonClickSound();
        if (callback != null) {
            callback.onMainMenu();
        }
    }

    @FXML
    private void handleReplay() throws Exception {
        Sounds.playButtonClickSound();
        if (callback != null) {
            callback.onReplay();
        }
    }
}