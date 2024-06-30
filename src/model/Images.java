package model;

import javafx.scene.image.Image;
import model.buildings.Properties;

public class Images {
    public static class PropertyImg {
        static Image castle = new Image("/Properties/Castle.png");
        static Image shack = new Image("/Properties/Shack.png");
        static Image citadel = new Image("/Properties/Citadel.png");
        static Image fortress = new Image("/Properties/Fortress.png");
        static Image villa = new Image("/Properties/Villa.png");
        static Image mansion = new Image("/Properties/Mansion.png");
        static Image manor = new Image("/Properties/Manor.png");
        static Image cottage = new Image("/Properties/Cottage.png");

        public PropertyImg() {
        }

        public static Image getImage(Properties type) {
            return switch (type) {
                case Shack -> shack;
                case Cottage -> cottage;
                case Villa -> villa;
                case Mansion -> mansion;
                case Manor -> manor;
                case Castle -> castle;
                case Citadel -> citadel;
                case Fortress -> fortress;
                default -> throw new IllegalArgumentException("Unsupported property type: " + type);
            };
        }
    }

}




