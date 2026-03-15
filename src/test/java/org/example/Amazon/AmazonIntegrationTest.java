package org.example.Amazon;

import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Amazon integration tests")
class AmazonIntegrationTest {

    private Database database;
    private ShoppingCartAdaptor cart;

    @BeforeEach
    void setUp() {
        database = new Database();
        database.resetDatabase();
        cart = new ShoppingCartAdaptor(database);
    }

    @AfterEach
    void tearDown() {
        database.close();
    }

    @Nested
    @DisplayName("specification-based")
    class SpecificationBased {

        @Test
        @DisplayName("calculates final price with regular cost, delivery fee and electronics surcharge")
        void calculatesFinalPriceForMixedCart() {
            Amazon amazon = new Amazon(cart, List.of(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

            amazon.addToCart(new Item(ItemType.ELECTRONIC, "Headphones", 1, 50.0));
            amazon.addToCart(new Item(ItemType.OTHER, "Book", 2, 15.0));

            double finalPrice = amazon.calculate();

            assertThat(finalPrice).isEqualTo(92.5);
        }

        @Test
        @DisplayName("returns zero when the cart is empty")
        void returnsZeroForEmptyCart() {
            Amazon amazon = new Amazon(cart, List.of(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

            double finalPrice = amazon.calculate();

            assertThat(finalPrice).isZero();
        }
    }

    @Nested
    @DisplayName("structural-based")
    class StructuralBased {

        @Test
        @DisplayName("persists items in DB and exposes them through the adapter")
        void persistsAndReadsItemsThroughAdapter() {
            Item first = new Item(ItemType.OTHER, "Pencil", 3, 1.5);
            Item second = new Item(ItemType.ELECTRONIC, "Mouse", 1, 20.0);

            cart.add(first);
            cart.add(second);

            List<Item> items = cart.getItems();

            assertThat(items)
                    .extracting(Item::getName)
                    .containsExactlyInAnyOrder("Pencil", "Mouse");
            assertThat(items).hasSize(2);
        }

        @Test
        @DisplayName("uses delivery boundary for 4 distinct rows in the DB-backed cart")
        void usesDeliveryBoundaryForFourRows() {
            Amazon amazon = new Amazon(cart, List.of(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));

            amazon.addToCart(new Item(ItemType.OTHER, "Item-1", 1, 10.0));
            amazon.addToCart(new Item(ItemType.OTHER, "Item-2", 1, 10.0));
            amazon.addToCart(new Item(ItemType.OTHER, "Item-3", 1, 10.0));
            amazon.addToCart(new Item(ItemType.OTHER, "Item-4", 1, 10.0));

            double finalPrice = amazon.calculate();

            assertThat(finalPrice).isEqualTo(52.5);
        }
    }
}
