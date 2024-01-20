package model.buildings;

public class PropertyConfig {
    public static final PropertyValues FORTRESS = new PropertyValues(400, 300, 200, 100);
    public static final PropertyValues CITADEL = new PropertyValues(300, 300, 100, 90);
    public static final PropertyValues CASTLE = new PropertyValues(200, 250, 50, 70);
    public static final PropertyValues MANOR = new PropertyValues(150, 100, 50, 60);
    public static final PropertyValues MANSION = new PropertyValues(100, 40, 40, 40);
    public static final PropertyValues VILLA = new PropertyValues(50, 0, 30, 30);
    public static final PropertyValues COTTAGE = new PropertyValues(30, 0, 20, 20);
    public static final PropertyValues SHACK = new PropertyValues(20, 0, 10, 0);
    public static class PropertyValues {
        double food;
        double alloy;
        double gold;
        double strength;

        public PropertyValues(double food, double alloy, double gold, double strength) {
            this.food = food;
            this.alloy = alloy;
            this.gold = gold;
            this.strength = strength;
        }
    }
}



