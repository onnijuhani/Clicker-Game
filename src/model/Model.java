package model;

import model.characters.Character;
import model.characters.Peasant;
import model.characters.Person;
import model.characters.Role;
import model.characters.npc.Captain;
import model.characters.npc.Governor;
import model.characters.npc.King;
import model.characters.npc.Mayor;
import model.characters.player.clicker.Clicker;
import model.map.CurrentView;
import model.time.Time;
import model.worldCreation.*;

public class Model {

    private static final CreateWorld createWorld = new CreateWorld();
    private static final CurrentView currentView = new CurrentView();
    private static final Time time = new Time();

    private static Person playerPerson;
    private static Role playerRole;
    private static Character playerCharacter;
    private static double playerTerritory = 0;


    public Model(){
        currentView.setCurrentView(createWorld.getSpawnQuarter().getHigher());
        setUpPlayer();
        createWorld.world.calculateAllNonPlayerNations();
        Clicker.initializeClicker(playerPerson);
    }

    private void setUpPlayer() {
        playerCharacter = createWorld.getInitialPlayer();
        playerPerson = playerCharacter.getPerson();
        playerRole = playerCharacter.getRole();
    }

    public static void updatePlayer(){
        setPlayerCharacter(playerPerson.getCharacter());
        setPlayerRole(playerPerson.getRole());
        calculatePlayerTerritory();
    }

    public static Role getPlayerRole() {
        return playerRole;
    }
    public static Person getPlayerAsPerson() {
        return playerPerson;
    }
    public static Character getPlayerAsCharacter() {
        return getPlayerAsPerson().getCharacter();
    }
    public static void setPlayerRole(Role playerRole) {
        Model.playerRole = playerRole;
    }

    public Person getPlayerPerson(){
        return playerPerson;
    }
    public void setPlayerPerson(Person player) {
        Model.playerPerson = player;
    }
    public static void setPlayerCharacter(Character playerCharacter) {
        Model.playerCharacter = playerCharacter;
    }
    public Character getPlayerCharacter() {
        return playerCharacter;
    }
    public Time accessTime() {
        return time;
    }
    public CreateWorld accessWorld() {
        return createWorld;
    }
    public CurrentView accessCurrentView() {
        return currentView;
    }

    public static double getPlayerTerritory() {
        return playerTerritory;
    }

    public static void calculatePlayerTerritory(){
        if(playerCharacter instanceof Peasant){
            return;
        }
        double all = World.getAllQuarters().size();

        if(playerCharacter instanceof Captain){
            playerTerritory = 1 / all;
        }

        if(playerCharacter instanceof Mayor mayor){
            Area city = mayor.getAuthorityPosition().getAreaUnderAuthority();
            playerTerritory = city.getContents().size() / all;
        }

        if(playerCharacter instanceof Governor governor){
            Area province = governor.getAuthorityPosition().getAreaUnderAuthority();
            int amountQuartersUnderProvince = 0;
            for(Object area : province.getContents()){
                City city = (City) area;
                amountQuartersUnderProvince += city.getContents().size();
            }
            playerTerritory = amountQuartersUnderProvince / all;
        }

        if(playerCharacter instanceof King king){
            Nation nation = (Nation) king.getAuthorityPosition().getAreaUnderAuthority();
            int amountQuartersUnderAuthority = nation.getQuarterAmount();

            if(!nation.getVassals().isEmpty()){
                for(Nation n : nation.getVassals()){
                    amountQuartersUnderAuthority += n.getQuarterAmount();
                }
            }

            playerTerritory = amountQuartersUnderAuthority / all;
        }

    }






}

