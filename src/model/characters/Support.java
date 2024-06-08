package model.characters;

import model.buildings.PropertyFactory;
import model.characters.authority.Authority;
import model.resourceManagement.payments.Salary;

public class Support extends Character {
    protected Salary salary;

    public static int totalAmount;

    public Support(Authority authorityTo) {
        role.setAuthority(authorityTo);
        totalAmount += 1;
        createProperty();
    }

    public void createProperty() {
        person.setProperty(PropertyFactory.createSupportProperty(person));
        person.getProperty().setOwner(this.getPerson());
    }

    public Salary getSalary() {
        return salary;
    }


}
