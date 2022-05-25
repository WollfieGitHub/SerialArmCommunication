package fr.wollfie.serial_arm_com.apps;

import fr.wollfie.serial_arm_com.apps.components.RightPanel;
import fr.wollfie.serial_arm_com.apps.components.canvas_3d.Scene3D;
import fr.wollfie.serial_arm_com.movement_sequence.MovementSequencer;
import fr.wollfie.serial_arm_com.sim.ArmSimulation;
import fr.wollfie.serial_arm_com.graphics.canvases.GraphicHandler;
import fr.wollfie.serial_arm_com.apps.components.LeftPanel;
import fr.wollfie.serial_arm_com.apps.components.MainPanel;
import fr.wollfie.serial_arm_com.graphics.Renderer;
import fr.wollfie.serial_arm_com.maths.RobotArmController;
import fr.wollfie.serial_arm_com.mechanism.ServoControl;
import fr.wollfie.serial_arm_com.sim.CarController;
import fr.wollfie.serial_arm_com.sim.VirtualServoControl;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Objects;

public class InverseKinApp extends Application {

    public static final Color BACKGROUND_COLOR = rgb255To1(131, 168, 146);
    public static final double ANGLE_TO_GRIP_RATIO = 0.5;
    private final TextArea logger = new TextArea();
    private ArmSimulation armSimulation;
    private GraphicHandler graphicHandler;
    private MovementSequencer movementSequencer;

    private static Color rgb255To1(int r, int g, int b) {
        return new Color(r / 255.0, g / 255.0, b / 255.0, 1);
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {

        double l1 = 30, l2 = 15, l3 = 10;

        logger.setMinHeight(220);

        ServoControl servoControl = new ServoControl(l1, l2, l3, logger);
        VirtualServoControl virtualServoControl = new VirtualServoControl();
        RobotArmController armController = RobotArmController.of(l1, l2, l3, servoControl, virtualServoControl);
        CarController carController = new CarController(servoControl);

        this.armSimulation = new ArmSimulation(armController, carController);
        graphicHandler = new GraphicHandler(armController, servoControl, armSimulation);
        this.movementSequencer = new MovementSequencer(armController);

        Scene3D scene3D = new Scene3D(500, armController);
        LeftPanel leftPanel = new LeftPanel(logger, graphicHandler.getRotCanvas());
        MainPanel mainPanel = new MainPanel(graphicHandler.getArmCanvas(), armSimulation, armController,
                movementSequencer, scene3D, virtualServoControl);
        RightPanel rightPanel = new RightPanel(graphicHandler.getCarCanvas(), graphicHandler.getGripCanvas());


        HBox root = new HBox();
        var scene = new Scene(root);

        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(leftPanel.getRoot(), mainPanel.getRoot(), rightPanel.getRoot(), scene3D.getRootScene());
        root.setSpacing(10.0);
        root.setBackground(new Background(new BackgroundFill(rgb255To1(64, 64, 64), null, null)));

        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.getIcons().add(new Image(Objects.requireNonNull(InverseKinApp.class.getResourceAsStream("/Logo.png"))));
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> System.exit(0));

        Renderer renderer = new Renderer(this);
        renderer.start();
    }

    public void updateArm() {
        if (this.movementSequencer.isPlaying()) {
            this.movementSequencer.update();
        } else {
            this.armSimulation.update();
        }
        this.graphicHandler.updateArm();
    }
}
