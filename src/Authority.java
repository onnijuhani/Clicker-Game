import java.util.HashMap;

public class Authority {
    Property property;
    AuthorityCharacter character;
    String PropertyType;

    protected double foodTaxRate = 0.6;
    protected double alloyTaxRate = 0.6;
    protected double goldTaxRate = 0.6;

    protected Authority authOver;
    protected Authority authUnder;


    public AuthorityCharacter getCharacter() {
        return character;
    }

    public Authority(Property property, AuthorityCharacter character) {
        this.property = property;
        this.character = character;
        this.PropertyType = property.getName();
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

    public Authority getAuthOver() {
        return authOver;
    }

    public void setAuthOver(Authority authOver) {
        this.authOver = authOver;
    }

    public Authority getAuthUnder() {
        return authUnder;
    }

    public void setAuthUnder(Authority authUnder) {
        this.authUnder = authUnder;
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

    public QuarterAuthority(Property property, AuthorityCharacter character) {
        super(property, character);
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
