import java.util.ArrayList;
import java.util.Scanner;

public class UserTest {

    public static void main(String[] args) {

        Scanner read = new Scanner(System.in);
//
//        while (true) {
//            System.out.println("Decide world size (SMALL, MEDIUM, LARGE): ");
//            String userInput = read.nextLine().toUpperCase(); // Convert to uppercase for case-insensitive matching
//
//            Size size;
//
//            try {
//                size = Size.valueOf(userInput);
//                System.out.println("Selected size: " + size);
//                break;
//            } catch (IllegalArgumentException e) {
//                System.out.println("Invalid size input. Please enter SMALL, MEDIUM, or LARGE.");
//            }
//        }

        World world = new World("Medium World", Size.MEDIUM);

        Quarter spawn = world.getContents().get(0).getContents().get(0).getContents().get(0).getContents().get(0).getContents().get(0);
        Nation spawnNation = world.getContents().get(0).getContents().get(0);

        System.out.println(spawnNation.getAllQuarters());
        System.out.println(spawnNation.getAllQuarters().get(0).getPopulationList().get("merchants"));

        ControlledArea currentView = spawn;
        CurrentPosition position = new CurrentPosition();
        position.updateCurrentQuarter(spawn);

        System.out.println(position.getCurrentQuarter().returnAllInformation());

        while (true) {
            String currentViewName = currentView.getName();
            String currentViewDetails = currentView.getDetails();

            Authority authorityHere = currentView.getAuthority();
            String authDetails = authorityHere.getDetails();


            ArrayList<Area> currentPosContents = currentView.getContents();
            Area currentPosUnder = currentView.getHigher();
            String currentPosUnderName = currentPosUnder.getName();


            System.out.println("You are viewing " + currentViewDetails);

            System.out.println("This area contains areas: " + currentPosContents);
            System.out.println(currentViewName + " is under " + currentPosUnderName);
            System.out.println("Authority here is: " + authDetails);
            System.out.println("wtf"+currentView.getHigher().getHigher());

            System.out.println("Selection: ");
            String input = read.nextLine();


            if (input.equals("move up")) {
                currentView = (ControlledArea) currentView.getHigher();
            }
            if (input.equals("info")) {
                System.out.println(position.getCurrentQuarter().returnAllInformation());
            }
        }


    }
}



