public class FlightBookingFacade {
    private SeatInventorySystem seatSystem;
    private BaggagePricingEngine baggageEngine;
    private CurrencyConverter currencyConverter;
    private PaymentProcessor paymentProcessor;
    private TicketIssuer ticketIssuer;
    public FlightBookingFacade() {
        seatSystem = new SeatInventorySystem();
        baggageEngine = new BaggagePricingEngine();
        currencyConverter = new CurrencyConverter();
        paymentProcessor = new PaymentProcessor();
        ticketIssuer = new TicketIssuer();
    }

    public String bookFlight(String passengerName, String flightCode, String seatNumber,
                             double baseFareUSD, int bagCount, double bagWeightKg,
                             String currency, String cardNumber) {
        return bookFlight(passengerName, flightCode, seatNumber, baseFareUSD,
                bagCount, bagWeightKg, currency, cardNumber, "STANDARD");
    }

    public String bookFlight(String passengerName, String flightCode, String seatNumber,
                             double baseFareUSD, int bagCount, double bagWeightKg,
                             String currency, String cardNumber, String loyaltyTier) {
        String lockToken = null;
        try {
            seatSystem.connectToReservationServer();
            lockToken = seatSystem.lockSeat(flightCode, seatNumber, passengerName);

            double baggageFee = 0.0;
            String tier = loyaltyTier == null ? "STANDARD" : loyaltyTier.trim().toUpperCase();
            if (!tier.equals("SILVER") && !tier.equals("GOLD")) {
                baggageFee = baggageEngine.calculateBaggageFeeUSD(bagCount, bagWeightKg);
            }

            double totalUSD = baseFareUSD + baggageFee;
            double finalPrice = currencyConverter.convertFromUSD(totalUSD, currency);

            paymentProcessor.initializeGateway();
            if (tier.equals("SILVER")) {
                paymentProcessor.applyPromoCode("SILVER-FREE-BAG");
            }

            String receiptId = paymentProcessor.chargeCard(cardNumber, finalPrice, currency);
            return ticketIssuer.generateTicket(passengerName, flightCode, lockToken, receiptId);

        } catch (RuntimeException e) {
            if (lockToken != null) {
                seatSystem.releaseSeatLock(lockToken);
            }
            System.out.println("Booking error: " + e.getMessage());
            return "BOOKING_FAILED: " + e.getMessage();
        } finally {
            seatSystem.disconnect();
        }
    }
}