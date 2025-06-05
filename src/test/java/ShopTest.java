// Importiere JUnit- und Mockito-Funktionen
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;  // für assertTrue, assertFalse, assertThrows
import static org.mockito.Mockito.*;              // für mock() und when()

// Importiere die Klassen aus deinem Projekt
import cyclechronicles.Shop;
import cyclechronicles.Order;
import cyclechronicles.Type;
import java.util.Optional;
public class ShopTest {

    private Shop shop;

    // Wird vor jedem Test automatisch ausgeführt – setzt den Shop zurück (leerer Zustand)
    @BeforeEach
    void setUp() {
        shop = new Shop();
    }

    // Test: Ein Auftrag mit einem zulässigen Fahrradtyp (RACE) wird akzeptiert
    @Test
    void testAccept_validRaceBike_shouldReturnTrue() {
        Order mockOrder = mock(Order.class); // Erzeuge einen Mock-Auftrag
        when(mockOrder.getBicycleType()).thenReturn(Type.RACE); // Simuliere: es ist ein RACE-Bike
        when(mockOrder.getCustomer()).thenReturn("kunde1");      // Kunde heißt „kunde1“

        // Erwartung: Auftrag wird angenommen
        assertTrue(shop.accept(mockOrder));
    }

    // Test: Ein Gravel-Bike wird abgelehnt (laut Shop-Regel)
    @Test
    void testAccept_gravelBike_shouldReturnFalse() {
        Order mockOrder = mock(Order.class);
        when(mockOrder.getBicycleType()).thenReturn(Type.GRAVEL); // Verbotener Typ
        when(mockOrder.getCustomer()).thenReturn("kunde1");

        // Erwartung: Auftrag wird abgelehnt
        assertFalse(shop.accept(mockOrder));
    }

    // Test: Ein E-Bike wird ebenfalls abgelehnt
    @Test
    void testAccept_eBike_shouldReturnFalse() {
        Order mockOrder = mock(Order.class);
        when(mockOrder.getBicycleType()).thenReturn(Type.EBIKE); // Verbotener Typ
        when(mockOrder.getCustomer()).thenReturn("kunde1");

        assertFalse(shop.accept(mockOrder));
    }

    // Test: Derselbe Kunde darf nicht zwei offene Aufträge gleichzeitig haben
    @Test
    void testAccept_duplicateCustomer_shouldReturnFalse() {
        Order firstOrder = mock(Order.class);
        when(firstOrder.getBicycleType()).thenReturn(Type.RACE);
        when(firstOrder.getCustomer()).thenReturn("kunde1");

        Order secondOrder = mock(Order.class);
        when(secondOrder.getBicycleType()).thenReturn(Type.RACE);
        when(secondOrder.getCustomer()).thenReturn("kunde1"); // gleicher Kunde

        assertTrue(shop.accept(firstOrder));    // Erster Auftrag wird akzeptiert
        assertFalse(shop.accept(secondOrder));  // Zweiter Auftrag wird abgelehnt
    }

    // Test: Der Shop akzeptiert maximal 5 Aufträge von 5 unterschiedlichen Kunden
    @Test
    void testAccept_maximumFiveOrders_allowed() {
        for (int i = 1; i <= 5; i++) {
            Order mockOrder = mock(Order.class);
            when(mockOrder.getBicycleType()).thenReturn(Type.RACE);
            when(mockOrder.getCustomer()).thenReturn("kunde" + i); // Jeder Kunde ist unterschiedlich
            assertTrue(shop.accept(mockOrder)); // Alle 5 werden akzeptiert
        }
    }

    // Test: Ein sechster Auftrag wird abgelehnt, wenn bereits 5 akzeptiert wurden
    @Test
    void testAccept_sixthOrder_shouldReturnFalse() {
        // 5 gültige Aufträge
        for (int i = 1; i <= 5; i++) {
            Order mockOrder = mock(Order.class);
            when(mockOrder.getBicycleType()).thenReturn(Type.RACE);
            when(mockOrder.getCustomer()).thenReturn("kunde" + i);
            assertTrue(shop.accept(mockOrder));
        }

        // 6. Kunde → sollte abgelehnt werden
        Order sixthOrder = mock(Order.class);
        when(sixthOrder.getBicycleType()).thenReturn(Type.RACE);
        when(sixthOrder.getCustomer()).thenReturn("kunde6");
        assertFalse(shop.accept(sixthOrder));
    }

    /**
     * Die Methoden `repair()` und `deliver()` sind noch nicht implementiert
     * und werfen aktuell eine UnsupportedOperationException.
     * Durch gezielte Tests stellen wir sicher, dass dieser Zustand dokumentiert ist.
     * <p>
     * Mockito wäre hier dann sinnvoll, wenn z. B. ein Order-Objekt im Rückgabewert
     * gemockt werden müsste. In unserem Fall genügt der direkte Aufruf der Methode,
     * um die Ausnahme zu prüfen.
     */
// Test: Ein reparierter Auftrag wird korrekt von pending zu completed verschoben
    @Test
    void testRepair_shouldMoveOrderFromPendingToCompleted() {
        Order mockOrder = mock(Order.class);
        when(mockOrder.getBicycleType()).thenReturn(Type.RACE);
        when(mockOrder.getCustomer()).thenReturn("kunde1");

        shop.accept(mockOrder); // Auftrag wird angenommen
        Optional<Order> repaired = shop.repair(); // Auftrag wird repariert

        assertTrue(repaired.isPresent()); // Es wurde etwas repariert
        assertEquals(mockOrder, repaired.get()); // Das reparierte Objekt ist das erwartete
    }

    // Test: Wenn keine Aufträge vorliegen, gibt repair() Optional.empty() zurück
    @Test
    void testRepair_withNoPendingOrders_shouldReturnEmpty() {
        Optional<Order> result = shop.repair(); // Kein Auftrag vorhanden
        assertTrue(result.isEmpty()); // Muss leer sein
    }

    // Test: Nach der Reparatur kann ein Auftrag dem richtigen Kunden ausgeliefert werden
    @Test
    void testDeliver_shouldReturnCompletedOrder() {
        Order mockOrder = mock(Order.class);
        when(mockOrder.getBicycleType()).thenReturn(Type.RACE);
        when(mockOrder.getCustomer()).thenReturn("kunde1");

        shop.accept(mockOrder);
        shop.repair(); // Auftrag wird abgeschlossen

        Optional<Order> delivered = shop.deliver("kunde1");
        assertTrue(delivered.isPresent()); // Auftrag wurde geliefert
        assertEquals(mockOrder, delivered.get()); // Der gelieferte Auftrag stimmt überein
    }

    // Test: Wenn ein Kunde keinen reparierten Auftrag hat, bekommt er nichts
    @Test
    void testDeliver_withUnknownCustomer_shouldReturnEmpty() {
        Order mockOrder = mock(Order.class);
        when(mockOrder.getBicycleType()).thenReturn(Type.RACE);
        when(mockOrder.getCustomer()).thenReturn("kunde1");

        shop.accept(mockOrder);
        shop.repair(); // Auftrag wird abgeschlossen

        Optional<Order> result = shop.deliver("kunde2"); // Falscher Kunde
        assertTrue(result.isEmpty()); // Keine Lieferung möglich
    }
}

