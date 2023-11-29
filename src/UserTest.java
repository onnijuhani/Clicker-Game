import java.util.ArrayList;
import java.util.Scanner;

public class UserTest {

    public static void main(String[] args) {

        Scanner read = new Scanner(System.in);

        while (true) {
            System.out.println("Decide world size (SMALL, MEDIUM, LARGE): ");
            String userInput = read.nextLine().toUpperCase(); // Convert to uppercase for case-insensitive matching

            Size size;

            try {
                size = Size.valueOf(userInput);
                System.out.println("Selected size: " + size);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid size input. Please enter SMALL, MEDIUM, or LARGE.");
            }
        }

        World world = new World("Medium World", Size.LARGE);



        Quarter spawn = world.getContents().get(0).getContents().get(0).getContents().get(0).getContents().get(0).getContents().get(0);

        ControlledArea currentPos = spawn;

        while (true) {
            String currentPosName = currentPos.getName();
            String currentPosDetails = currentPos.getDetails();

            Authority authorityHere = currentPos.getAuthority();
            String authDetails = authorityHere.getDetails();


            ArrayList<Area> currentPosContents = currentPos.getContents();
            Area currentPosUnder = currentPos.getHigher();
            String currentPosUnderName = currentPosUnder.getName();


            System.out.println("You are in " + currentPosDetails);
            System.out.println("This area contains: " + currentPosContents);
            System.out.println(currentPosName + " is under " + currentPosUnderName);
            System.out.println("Authority here is: " + authDetails);

            System.out.println("Selection: ");
            String input = read.nextLine();

            if (input.equals("move up")) {
                currentPos = (ControlledArea) currentPos.getHigher();
            }
        }


    }
}
