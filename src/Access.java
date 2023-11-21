import java.util.ArrayList;

public class Access {

    public static void printNames(Area area) {
        ArrayList<Area> areas = area.getContents();
        for (Area areaInstance : areas) {
            System.out.println(areaInstance.getName());
        }

    }

    public static void printAll(Area area, String indentation) {
        String className = area.getClass().toString();
        System.out.println(className + " :" + indentation + area.getName());

        if (area instanceof Area) {
            ArrayList<Area> contents = ((Area) area).getContents();
            for (Area subArea : contents) {
                printAll(subArea, indentation + "\t\t");
            }
        }
    }

}






//abstract class MyAbstractClass {
//    // Abstract method (no method body)
//    abstract void abstractMethod();
//
//    // Concrete method (with implementation)
//    void concreteMethod() {
//        System.out.println("This is a concrete method.");
//    }
//}