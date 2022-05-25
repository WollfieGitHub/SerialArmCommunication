package fr.wollfie.serial_arm_com.movement_sequence;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;


public class MovementAnimation {

    public static ListProperty<MovementAnimation> ALL_ANIMATIONS
            = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

    public static Map<String, MovementAnimation> MAPPED_ANIM = new HashMap<>();

    static final String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator +
            "MakingIntelligentThings" + File.separator
            + "Animations";

    static {
        ALL_ANIMATIONS.addAll(MovementAnimation.loadAll());
    }

    private final String animationName;
    private final MovementFrame[] frames;
    private final float frameRate;

    public MovementAnimation(String animationName, MovementFrame[] frames, float frameRate) {
        this.animationName = animationName;
        this.frames = frames;
        this.frameRate = frameRate;
        MAPPED_ANIM.put(animationName, this);
    }

    public static MovementAnimation deserialize(JSONObject jsonObject) {
        JSONArray framesJson = jsonObject.getJSONArray("frames");
        float frameRate = jsonObject.getFloat("frameRate");
        String animationName = jsonObject.getString("animationName");
        MovementFrame[] frames = new MovementFrame[framesJson.length()];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = MovementFrame.deserialize(framesJson.getJSONObject(i));
        }
        return new MovementAnimation(animationName, frames, frameRate);
    }

    public JSONObject serialize() {
        JSONObject json = new JSONObject();
        json.put("animationName", animationName);
        json.put("frameRate", frameRate);
        JSONArray jsonArray = new JSONArray();

        for (MovementFrame frame : frames) {
            jsonArray.put(frame.serialize());
        }

        json.put("frames", jsonArray);
        return json;
    }

    public static Builder buildNew(String animationName, float frameRate) {
        return new Builder(animationName, frameRate);
    }

    public String getName() {
        return animationName;
    }

    public float getFrameRate() {
        return frameRate;
    }

    public int getFrameCount() {
        return frames.length;
    }

    public MovementFrame getFrame(int frameIndex) {
        return frames[frameIndex];
    }

    public static class Builder {
        private final String animationName;
        private final float frameRate;
        private final List<MovementFrame> frameList;

        private Builder(String animationName, float frameRate) {
            this.animationName = animationName;
            this.frameRate = frameRate;
            this.frameList = new ArrayList<>();
        }

        public Builder addKey(MovementFrame frame) {
            this.frameList.add(frame);
            return this;
        }

        public MovementAnimation build() {
            return new MovementAnimation(this.animationName, frameList.toArray(new MovementFrame[0]), frameRate);
        }
    }

    public void save() {
        String path = MovementAnimation.path;
        File animDir = new File(path);

        if (!(animDir.exists() || animDir.mkdirs())) {
            try {
                throw new IOException("Path could not be created and doesn't exist : " + String.format("%s", animDir.getPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ALL_ANIMATIONS.add(this);
        String fileName = animationName + ".json";
        animDir = new File(animDir.getPath() + File.separator + fileName);
        try(PrintWriter fileIn = new PrintWriter(new BufferedOutputStream(new FileOutputStream(animDir)))) {
            fileIn.write(this.serialize().toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<MovementAnimation> loadAll() {
        String path = MovementAnimation.path;
        File animDir = new File(path);

        if (!(animDir.exists() || animDir.mkdirs())) {
            try {
                throw new IOException("Path could not be created and doesn't exist : " + String.format("%s", animDir.getPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        List<MovementAnimation> movementAnimations = new ArrayList<>();
        for (File file : Objects.requireNonNull(animDir.listFiles())) {
            if (file.getName().contains(".json")) {
                try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder sb = new StringBuilder();
                    String s;
                    while ((s = reader.readLine()) != null) {
                        sb.append(s);
                    }
                    String json = sb.toString();
                    JSONObject jsonObj = new JSONObject(json);

                    movementAnimations.add(MovementAnimation.deserialize(jsonObj));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return movementAnimations;
    }
}
