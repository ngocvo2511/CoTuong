package com.example.cotuong.utils;

import com.example.cotuong.chesslogic.PieceType;
import com.example.cotuong.chesslogic.Player;
import com.example.cotuong.chesslogic.pieces.Piece;
import javafx.scene.image.Image;

import java.util.EnumMap;
import java.util.Map;

public class Images {
    private static final Map<PieceType, Image> redSources = new EnumMap<>(PieceType.class);
    private static final Map<PieceType, Image> blackSources = new EnumMap<>(PieceType.class);
    private static final double PIECE_WIDTH = 70; // Điều chỉnh kích thước theo nhu cầu
    private static final double PIECE_HEIGHT = 70;
    static {
        redSources.put(PieceType.CANNON, loadImage("/com/example/cotuong/images/PhaoDo.png"));
        redSources.put(PieceType.CHARIOT, loadImage("/com/example/cotuong/images/XeDo.png"));
        redSources.put(PieceType.GENERAL, loadImage("/com/example/cotuong/images/TuongDo.png"));
        redSources.put(PieceType.ADVISOR, loadImage("/com/example/cotuong/images/SiDo.png"));
        redSources.put(PieceType.ELEPHANT, loadImage("/com/example/cotuong/images/TinhDo.png"));
        redSources.put(PieceType.SOLDIER, loadImage("/com/example/cotuong/images/TotDo.png"));
        redSources.put(PieceType.HORSE, loadImage("/com/example/cotuong/images/MaDo.png"));

        blackSources.put(PieceType.CANNON, loadImage("/com/example/cotuong/images/PhaoXanh.png"));
        blackSources.put(PieceType.CHARIOT, loadImage("/com/example/cotuong/images/XeXanh.png"));
        blackSources.put(PieceType.GENERAL, loadImage("/com/example/cotuong/images/TuongXanh.png"));
        blackSources.put(PieceType.ADVISOR, loadImage("/com/example/cotuong/images/SiXanh.png"));
        blackSources.put(PieceType.ELEPHANT, loadImage("/com/example/cotuong/images/TinhXanh.png"));
        blackSources.put(PieceType.SOLDIER, loadImage("/com/example/cotuong/images/TotXanh.png"));
        blackSources.put(PieceType.HORSE, loadImage("/com/example/cotuong/images/MaXanh.png"));
    }

    private static Image loadImage(String path) {
        return new Image(Images.class.getResourceAsStream(path), PIECE_WIDTH, PIECE_HEIGHT, true, true);
    }

    public static Image getImage(Player color, PieceType type) {
        if (color == Player.RED) {
            return redSources.get(type);
        } else if (color == Player.BLACK) {
            return blackSources.get(type);
        } else {
            return null;
        }
    }

    public static Image getImage(Piece piece) {
        if (piece == null) return null;
        return getImage(piece.getColor(), piece.getType());
    }
}