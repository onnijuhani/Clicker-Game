import javafx.scene.image.Image;

public class Images {
    static class PropertyImg {
        static Image castle = new Image("file:C:/Users/onnil/IdeaProjects/Outside/Pictures/Properties/Castle.png");
        static Image shack = new Image("file:C:/Users/onnil/IdeaProjects/Outside/Pictures/Properties/Shack.png");
        static Image citadel = new Image("file:C:/Users/onnil/IdeaProjects/Outside/Pictures/Properties/Citadel.png");
        static Image fortress = new Image("file:C:/Users/onnil/IdeaProjects/Outside/Pictures/Properties/Fortress.png");
        static Image villa = new Image("file:C:/Users/onnil/IdeaProjects/Outside/Pictures/Properties/Villa.png");
        static Image mansion = new Image("file:C:/Users/onnil/IdeaProjects/Outside/Pictures/Properties/Mansion.png");
        static Image manor = new Image("file:C:/Users/onnil/IdeaProjects/Outside/Pictures/Properties/Manor.png");
        static Image cottage = new Image("file:C:/Users/onnil/IdeaProjects/Outside/Pictures/Properties/Cottage.png");

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




