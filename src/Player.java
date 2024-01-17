public class Player {
    private Property property;
    private Wallet wallet;
    private String name;
    private int totalClicks = 0;

    public Player(Quarter spawn){
        this.property = new Shack("Your Own");
        this.property.setLocation(spawn);
        this.wallet = new Wallet();
    }
    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }
    public Wallet getWallet() {
        return wallet;
    }
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getTotalClicks() {
        return totalClicks;
    }
    public void addClick() {
        this.totalClicks++;
    }

}
