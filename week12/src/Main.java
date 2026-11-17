public class Main {
    public static void main(String[] args) {
        FlightBookingFacade facade = new FlightBookingFacade();

        System.out.println("--- Test 1: Normal booking ---");
        String t1 = facade.bookFlight("Amina Suleimenova", "KC101", "14A", 320.00, 2, 23.0, "EUR", "4532015112830366");
        System.out.println("Result 1: " + t1);
        System.out.println();

        System.out.println("--- Test 2: Row 13 blocked ---");
        String t2 = facade.bookFlight("Daniyar Kassymov", "KC101", "13A", 320.00, 1, 15.0, "USD", "4532015112830366");
        System.out.println("Result 2: " + t2);
        System.out.println();

        System.out.println("--- Test 3: Bad card rollback ---");
        String t3 = facade.bookFlight("Timur Bekov", "KC101", "07D", 320.00, 1, 18.0, "KZT", "9999");
        System.out.println("Result 3: " + t3);
        System.out.println();

        System.out.println("--- Test 4: Silver tier ---");
        String t4 = facade.bookFlight("Aliya Nurlanova", "KC101", "10B", 320.00, 3, 30.0, "USD", "4532015112830366", "SILVER");
        System.out.println("Result 4: " + t4);
    }
}