public class Authority {
    Property property;
    AuthorityCharacter character;
    String authorityType;
    String PropertyType;

    public Authority(Property property, AuthorityCharacter character) {
        this.property = property;
        this.character = character;
        this.authorityType = character.getClass().getSimpleName();
        this.PropertyType = property.getName();
    }

    public String getAuthorityType() {
        return authorityType;
    }

    public String getPropertyType() {
        return PropertyType;
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
