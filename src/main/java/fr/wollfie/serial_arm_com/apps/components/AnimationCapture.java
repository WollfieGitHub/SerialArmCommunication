package fr.wollfie.serial_arm_com.apps.components;

import fr.wollfie.serial_arm_com.apps.InverseKinApp;
import fr.wollfie.serial_arm_com.apps.components.canvas_3d.Scene3D;
import fr.wollfie.serial_arm_com.maths.RobotArmController;
import fr.wollfie.serial_arm_com.movement_sequence.InterpolatedAnimation;
import fr.wollfie.serial_arm_com.movement_sequence.MovementAnimation;
import fr.wollfie.serial_arm_com.movement_sequence.MovementFrame;
import fr.wollfie.serial_arm_com.movement_sequence.MovementSequencer;
import fr.wollfie.serial_arm_com.sim.VirtualServoControl;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;

public class AnimationCapture {

    private final VBox root;
    private BooleanProperty animationEditingMode = new SimpleBooleanProperty();
    private BooleanProperty animationPlaying = new SimpleBooleanProperty();
    private MovementAnimation.Builder animationBuilder;
    private RobotArmController robotController;
    private InterpolatedAnimation currentAnimation;

    public AnimationCapture(MovementSequencer sequencer, RobotArmController robotController, Scene3D scene3D,
                            VirtualServoControl virtualServoControl) {
        root = new VBox();
        this.robotController = robotController;

        Label animLbl = new Label("Animation");
        HBox animControls = new HBox();
        animControls.setAlignment(Pos.CENTER);
        animControls.setSpacing(10);
        HBox animPlay = new HBox();
        animPlay.setAlignment(Pos.CENTER);
        animPlay.setSpacing(10);


        Label animNameLbl = new Label("Animation Name : ");
        TextField animNameField = new TextField();

        Button createNew = new Button("+");
        Button capture = new Button("Capture");
        Button finish = new Button("Save");

        animControls.getChildren().addAll(animNameLbl,
                animNameField, createNew, capture, finish);

        animNameField.disableProperty().bind(animationEditingMode);
        capture.disableProperty().bind(animationEditingMode.not());
        finish.disableProperty().bind(animationEditingMode.not());


        createNew.setOnAction(e -> {
            animationEditingMode.setValue(true);
            animationBuilder = MovementAnimation.buildNew(animNameField.getText(), 1);
        });

        capture.setOnAction(e -> {
            if (!animationEditingMode.getValue()) return;

            animationBuilder.addKey(MovementFrame.capture(this.robotController));
        });

        finish.setOnAction(e -> {
            this.animationBuilder.build().save();
            animationEditingMode.setValue(false);
        });

        ChoiceBox<MovementAnimation> existingAnimations = new ChoiceBox<>();
        existingAnimations.itemsProperty().bind(MovementAnimation.ALL_ANIMATIONS);
        existingAnimations.setValue(MovementAnimation.ALL_ANIMATIONS.isEmpty() ? null : existingAnimations.getItems().get(0));
        existingAnimations.disableProperty().bind(sequencer.playingProperty());
        existingAnimations.setConverter(new StringConverter<>() {
            @Override
            public String toString(MovementAnimation movementAnimation) {
                return movementAnimation == null ? "" : movementAnimation.getName();
            }
            @Override
            public MovementAnimation fromString(String s) {
                return MovementAnimation.MAPPED_ANIM.get(s);
            }
        });

        existingAnimations.valueProperty().addListener(v -> {
            InterpolatedAnimation interpolatedAnimation = InterpolatedAnimation.of(existingAnimations.getValue(),
                    virtualServoControl,
                    this.robotController);

            Group trajectory = new Group(interpolatedAnimation.draw());
            this.currentAnimation = interpolatedAnimation;
            scene3D.addTrajectory(trajectory);
        });

        Button playButton = new Button();
        playButton.textProperty().bind(Bindings.when(sequencer.playingProperty().and(
                existingAnimations.valueProperty().isNotNull()))
                .then("Pause").otherwise("Play"));

        playButton.setOnAction(e -> {
            if (sequencer.isPlaying()) {
                sequencer.stop();
            } else {
                sequencer.play(existingAnimations.getValue(), virtualServoControl);
            }
        });

        Button exportButton = new Button("Export");
        exportButton.setOnAction(e -> {
            this.currentAnimation.export();
        });


        animPlay.getChildren().addAll(existingAnimations, playButton, exportButton);

        root.getChildren().addAll(animLbl, animControls, animPlay);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        root.setBackground(new Background(new BackgroundFill(InverseKinApp.BACKGROUND_COLOR, null, null)));
    }

    public VBox getRoot() {
        return root;
    }
}
