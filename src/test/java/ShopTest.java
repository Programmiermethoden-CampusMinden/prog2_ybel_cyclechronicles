import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cyclechronicles.Shop;
import cyclechronicles.Order;
import cyclechronicles.Type;

public class ShopTest {

    private Shop shop;

    @BeforeEach
    void setUp() {
        shop = new Shop();
    }

    @Test
    void testAccept_validRaceBike_shouldReturnTrue() {
        Order mockOrder = mock(Order.class);
        when(mockOrder.getBicycleType()).thenReturn(Type.RACE);
        when(mockOrder.getCustomer()).thenReturn("kunde1");

        assertTrue(shop.accept(mockOrder));
    }

    @Test
    void testAccept_gravelBike_shouldReturnFalse() {
        Order mockOrder = mock(Order.class);
        when(mockOrder.getBicycleType()).thenReturn(Type.GRAVEL);
        when(mockOrder.getCustomer()).thenReturn("kunde1");

        assertFalse(shop.accept(mockOrder));
    }

    @Test
    void testAccept_eBike_shouldReturnFalse() {
        Order mockOrder = mock(Order.class);
        when(mockOrder.getBicycleType()).thenReturn(Type.EBIKE);
        when(mockOrder.getCustomer()).thenReturn("kunde1");

        assertFalse(shop.accept(mockOrder));
    }

    @Test
    void testAccept_duplicateCustomer_shouldReturnFalse() {
        Order firstOrder = mock(Order.class);
        when(firstOrder.getBicycleType()).thenReturn(Type.RACE);
        when(firstOrder.getCustomer()).thenReturn("kunde1");

        Order secondOrder = mock(Order.class);
        when(secondOrder.getBicycleType()).thenReturn(Type.RACE);
        when(secondOrder.getCustomer()).thenReturn("kunde1");

        assertTrue(shop.accept(firstOrder));   // Erster Auftrag ok
        assertFalse(shop.accept(secondOrder)); // Zweiter wird abgelehnt
    }

    @Test
    void testAccept_maximumFiveOrders_allowed() {
        for (int i = 1; i <= 5; i++) {
            Order mockOrder = mock(Order.class);
            when(mockOrder.getBicycleType()).thenReturn(Type.RACE);
            when(mockOrder.getCustomer()).thenReturn("kunde" + i);

            assertTrue(shop.accept(mockOrder));  // alle 5 erlaubt
        }
    }

    @Test
    void testAccept_sixthOrder_shouldReturnFalse() {
        for (int i = 1; i <= 5; i++) {
            Order mockOrder = mock(Order.class);
            when(mockOrder.getBicycleType()).thenReturn(Type.RACE);
            when(mockOrder.getCustomer()).thenReturn("kunde" + i);

            assertTrue(shop.accept(mockOrder));  // bis 5 ok
        }

        Order sixthOrder = mock(Order.class);
        when(sixthOrder.getBicycleType()).thenReturn(Type.RACE);
        when(sixthOrder.getCustomer()).thenReturn("kunde6");

        assertFalse(shop.accept(sixthOrder));  // 6. wird abgelehnt
    }
}
