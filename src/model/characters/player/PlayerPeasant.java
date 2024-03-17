package model.characters.player;


import model.buildings.properties.Shack;
import model.characters.Peasant;
import model.characters.Person;
import model.characters.Status;
import model.worldCreation.Quarter;

public class PlayerPeasant extends Peasant {



    public PlayerPeasant(Quarter spawn, Person person){

        spawn.setPopulationChanged(true);
        this.person = person;
        setProperty(new Shack("Your Own", person));

        getProperty().setOwner(person);
        setNation(spawn.getAuthorityHere().getCharacterInThisPosition().getNation());
        setAuthority(spawn.getAuthorityHere());
        getProperty().setLocation(spawn);

        makeConnections();

        setStatus(Status.Peasant);
        spawn.addCitizen(getStatus(),person);

        getWallet().setGold(1000000);
//        getWallet().setFood(1000);
//        getWallet().setAlloy(1000);


        spawn.createQuarterAlliances();

        person.setPlayer(true);
        spawn.updateCitizenCache();
    }


    @Override
    protected boolean shouldSubscribeToNpcEvent() {
        return false;
    }


}











