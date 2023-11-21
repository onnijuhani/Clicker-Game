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
}
