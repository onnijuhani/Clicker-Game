import java.util.ArrayList;
import java.util.HashMap;

public class Authority implements TimeObserver {

    @Override
    public void timeUpdate(int day, int week, int month, int year) {
        if (day == 1 && week == 1) {
            authOver.forEach(this::collectTax);
            supporters.forEach(this::paySupporters);
        }
    }

    public Property getProperty() {
        return property;
    }

    Property property;
    AuthorityCharacter character;
    String PropertyType;

    protected double foodTaxRate = 0.6;
    protected double alloyTaxRate = 0.6;
    protected double goldTaxRate = 0.6;

    protected ArrayList<Authority> authOver;
    protected ArrayList<Authority> authUnder;
    protected ArrayList<Support> supporters;


    public AuthorityCharacter getCharacter() {
        return character;
    }
    public void subscribeToTimeEvents() {
        TimeEventManager.subscribe(this);
    }

    public Authority(Property property, AuthorityCharacter character) {
        this.property = property;
        this.character = character;
        this.PropertyType = property.getName();
        this.authOver = new ArrayList<>();
        this.authUnder = new ArrayList<>();
        this.supporters = new ArrayList<>();
        subscribeToTimeEvents();
    }

    public String getPropertyType() {
        return PropertyType;
    }
    public double getFoodTaxRate() {
        return foodTaxRate;
    }
    public void setFoodTaxRate(double foodTaxRate) {
        this.foodTaxRate = foodTaxRate;
    }
    public double getAlloyTaxRate() {
        return alloyTaxRate;
    }
    public void setAlloyTaxRate(double alloyTaxRate) {
        this.alloyTaxRate = alloyTaxRate;
    }
    public double getGoldTaxRate() {
        return goldTaxRate;
    }
    public void setGoldTaxRate(double goldTaxRate) {
        this.goldTaxRate = goldTaxRate;
    }

    public ArrayList<Authority> getAuthOver() {
        return authOver;
    }

    public void setAuthOver(Authority authOver) {
        this.authOver.add(authOver);
    }

    public ArrayList<Authority> getAuthUnder() {
        return authUnder;
    }

    public void setAuthUnder(Authority authUnder) {
        this.authUnder.add(authUnder);
    }

    public void addSupporter(Support support) {
        this.supporters.add(support);
    }

    public ArrayList<Support> getSupporters(){
        return supporters;
    }

    public void collectTax(Authority authority){

        Vault captainsVault = authority.property.getVault();

        double food = captainsVault.getFood();
        double alloy = captainsVault.getAlloy();
        double gold = captainsVault.getGold();

        double foodAmount = food * getFoodTaxRate();
        double alloyAmount = alloy * getAlloyTaxRate();
        double goldAmount = gold * getGoldTaxRate();

        captainsVault.subtractFood(foodAmount);
        captainsVault.subtractAlloy(alloyAmount);
        captainsVault.subtractGold(goldAmount);

        this.property.vault.addResources(foodAmount, alloyAmount, goldAmount);
    }

    public void paySupporters(Support support){
        double food = property.getVault().getFood();
        double alloy = property.getVault().getAlloy();
        double gold = property.getVault().getGold();

        double amountFood = support.salary;
        double amountAlloy = support.salary;
        double amountGold = support.salary;

        property.vault.subtractResources(amountFood, amountAlloy, amountGold);

        support.getWallet().addResources(amountFood, amountAlloy, amountGold);

    }

}

class NationAuthority extends Authority {

    public NationAuthority(Property property, AuthorityCharacter character) {
        super(property, character);
    }



}

class ProvinceAuthority extends Authority {
    String authorityType = "Governor";

    public ProvinceAuthority(Property property, AuthorityCharacter character) {
        super(property, character);
    }
}

class CityAuthority extends Authority {

    String authorityType = "Mayor";
    public CityAuthority(Property property, AuthorityCharacter character) {
        super(property, character);
    }



}

class QuarterAuthority extends Authority {



    String authorityType = "Captain";

    public ArrayList<Peasant> getPeasants() {
        return peasants;
    }

    private ArrayList<Peasant> peasants;

    public QuarterAuthority(Property property, AuthorityCharacter character) {
        super(property, character);
        this.peasants = new ArrayList<>();
    }

    @Override
    public void timeUpdate(int day, int week, int month, int year) {
        if (day == 1 && week == 1) {
            peasants.forEach(peasant -> this.collectTax(peasant.releaseTax(this.enforceTax())));
        }
    }

    public void addPeasant(Peasant peasant){
        peasants.add(peasant);
    }

    public HashMap<Resource, Double> enforceTax(){
        HashMap<Resource, Double> taxRates = new HashMap<>();
        taxRates.put(Resource.Food, foodTaxRate);
        taxRates.put(Resource.Alloy, alloyTaxRate);
        taxRates.put(Resource.Gold, goldTaxRate);
        return taxRates;
    }

    public void collectTax(HashMap<Resource, Resources> collected) {
        if (collected.containsKey(Resource.Food)) {
            double food = collected.get(Resource.Food).getAmount();
            property.vault.addFood(food);
        }

        if (collected.containsKey(Resource.Alloy)) {
            double alloy = collected.get(Resource.Alloy).getAmount();
            property.vault.addAlloy(alloy);
        }

        if (collected.containsKey(Resource.Gold)) {
            double gold = collected.get(Resource.Gold).getAmount();
            property.vault.addGold(gold);
        }
    }

}
