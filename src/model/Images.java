package model;

import javafx.scene.image.Image;
import model.buildings.Properties;

public class Images {
    public static class PropertyImg {
        static Image castle = new Image("file:C:/Users/onnil/IdeaProjects/Outside/resources/Properties/Castle.png");
        static Image shack = new Image("file:C:/Users/onnil/IdeaProjects/Outside/resources/Properties/Shack.png");
        static Image citadel = new Image("file:C:/Users/onnil/IdeaProjects/Outside/resources/Properties/Citadel.png");
        static Image fortress = new Image("file:C:/Users/onnil/IdeaProjects/Outside/resources/Properties/Fortress.png");
        static Image villa = new Image("file:C:/Users/onnil/IdeaProjects/Outside/resources/Properties/Villa.png");
        static Image mansion = new Image("file:C:/Users/onnil/IdeaProjects/Outside/resources/Properties/Mansion.png");
        static Image manor = new Image("file:C:/Users/onnil/IdeaProjects/Outside/resources/Properties/Manor.png");
        static Image cottage = new Image("file:C:/Users/onnil/IdeaProjects/Outside/resources/Properties/Cottage.png");

        public PropertyImg() {
        }

        public static Image getImage(Properties type) {
            switch (type) {
                case Shack:
                    return shack;
                case Cottage:
                    return cottage;
                case Villa:
                    return villa;
                case Mansion:
                    return mansion;
                case Manor:
                    return manor;
                case Castle:
                    return castle;
                case Citadel:
                    return citadel;
                case Fortress:
                    return fortress;
                default:
                    throw new IllegalArgumentException("Unsupported property type: " + type);
            }
        }
    }

}




