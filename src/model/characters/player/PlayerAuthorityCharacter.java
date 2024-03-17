package model.characters.player;

import model.characters.AuthorityCharacter;
import model.characters.Role;
import model.characters.Status;
import model.characters.npc.Captain;
import model.worldCreation.Quarter;

public class PlayerAuthorityCharacter extends AuthorityCharacter {

    public PlayerAuthorityCharacter(PlayerPeasant playerPeasant, Captain captain){
        setPerson(playerPeasant.getPerson());

        setRole(new Role());
        getRole().setNation(playerPeasant.getNation());
        getRole().setStatus(Status.Captain);
        getRole().setAuthority(captain.getAuthority());
        getRole().setAuthorityPosition(captain.getAuthorityPosition());
        getRole().setCharacter(this);



        this.role.setPerson(person);
        this.role.setCharacter(this);

        this.person.setRole(role);
        this.person.setCharacter(this);

        Quarter location = person.getProperty().getLocation();

        location.changeCitizenPosition(person, Status.Peasant);

        person.setPlayer(true);

        location.setAuthorityHere(role.getAuthorityPosition(), this);

        location.updateCitizenCache();
    }




    @Override
    protected boolean shouldSubscribeToNpcEvent() {
        return false;
    }


}
