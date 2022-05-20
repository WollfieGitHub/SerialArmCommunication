package fr.wollfie.serial_arm_com.apps.components;

import fr.wollfie.serial_arm_com.apps.InverseKinApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;

public class RightPanel {

    private final VBox root;

    public RightPanel(Canvas canvas, Canvas gripCanvas) {
        this.root = initRightPanel(canvas, gripCanvas);
    }

    private VBox initRightPanel(Canvas canvas, Canvas gripCanvas) {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(canvas, initGripBox(gripCanvas));

        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));


        return vBox;
    }

    private VBox initGripBox(Canvas gripCanvas) {
        VBox vBox = new VBox();

        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setBackground(new Background(new BackgroundFill(InverseKinApp.BACKGROUND_COLOR, null, null)));
        vBox.getChildren().addAll(gripCanvas);

        return vBox;
    }

    public Node getRoot() {
        return root;
    }
}
