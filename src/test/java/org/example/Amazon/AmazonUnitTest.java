package org.example.Amazon;

import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Amazon unit tests")
class AmazonUnitTest {

    @Nested
    @DisplayName("specification-based")
    class SpecificationBased {

        @Test
        @DisplayName("RegularCost sums quantity multiplied by unit price for all items")
        void regularCostSumsAllRows() {
            RegularCost regularCost = new RegularCost();
            List<Item> cart = List.of(
                    new Item(ItemType.OTHER, "Notebook", 2, 10.0),
                    new Item(ItemType.ELECTRONIC, "Webcam", 1, 40.0)
            );

            double result = regularCost.priceToAggregate(cart);

            assertThat(result).isEqualTo(60.0);
        }

        @Test
        @DisplayName("ExtraCostForElectronics adds surcharge only when electronics exist")
        void extraCostForElectronicsFollowsBusinessRule() {
            ExtraCostForElectronics extraRule = new ExtraCostForElectronics();
            List<Item> withElectronics = List.of(new Item(ItemType.ELECTRONIC, "Keyboard", 1, 30.0));
            List<Item> withoutElectronics = List.of(new Item(ItemType.OTHER, "Pen", 3, 2.0));

            assertThat(extraRule.priceToAggregate(withElectronics)).isEqualTo(7.5);
            assertThat(extraRule.priceToAggregate(withoutElectronics)).isZero();
        }
    }

    @Nested
    @DisplayName("structural-based")
    class StructuralBased {

        @Test
        @DisplayName("DeliveryPrice follows each branch of item-count thresholds")
        void deliveryPriceThresholdBranches() {
            DeliveryPrice deliveryPrice = new DeliveryPrice();

            assertThat(deliveryPrice.priceToAggregate(List.of())).isEqualTo(0.0);
            assertThat(deliveryPrice.priceToAggregate(List.of(
                    new Item(ItemType.OTHER, "A", 1, 1.0),
                    new Item(ItemType.OTHER, "B", 1, 1.0),
                    new Item(ItemType.OTHER, "C", 1, 1.0)
            ))).isEqualTo(5.0);
            assertThat(deliveryPrice.priceToAggregate(List.of(
                    new Item(ItemType.OTHER, "A", 1, 1.0),
                    new Item(ItemType.OTHER, "B", 1, 1.0),
                    new Item(ItemType.OTHER, "C", 1, 1.0),
                    new Item(ItemType.OTHER, "D", 1, 1.0),
                    new Item(ItemType.OTHER, "E", 1, 1.0),
                    new Item(ItemType.OTHER, "F", 1, 1.0),
                    new Item(ItemType.OTHER, "G", 1, 1.0),
                    new Item(ItemType.OTHER, "H", 1, 1.0),
                    new Item(ItemType.OTHER, "I", 1, 1.0),
                    new Item(ItemType.OTHER, "J", 1, 1.0)
            ))).isEqualTo(12.5);
            assertThat(deliveryPrice.priceToAggregate(List.of(
                    new Item(ItemType.OTHER, "A", 1, 1.0),
                    new Item(ItemType.OTHER, "B", 1, 1.0),
                    new Item(ItemType.OTHER, "C", 1, 1.0),
                    new Item(ItemType.OTHER, "D", 1, 1.0),
                    new Item(ItemType.OTHER, "E", 1, 1.0),
                    new Item(ItemType.OTHER, "F", 1, 1.0),
                    new Item(ItemType.OTHER, "G", 1, 1.0),
                    new Item(ItemType.OTHER, "H", 1, 1.0),
                    new Item(ItemType.OTHER, "I", 1, 1.0),
                    new Item(ItemType.OTHER, "J", 1, 1.0),
                    new Item(ItemType.OTHER, "K", 1, 1.0)
            ))).isEqualTo(20.0);
        }

        @Test
        @DisplayName("Amazon aggregates all pricing rules and delegates addToCart")
        void amazonUsesRulesAndCartCollaboration() {
            ShoppingCart cart = mock(ShoppingCart.class);
            PriceRule rule1 = mock(PriceRule.class);
            PriceRule rule2 = mock(PriceRule.class);

            List<Item> items = List.of(new Item(ItemType.OTHER, "Bottle", 1, 5.0));
            when(cart.getItems()).thenReturn(items);
            when(rule1.priceToAggregate(items)).thenReturn(20.0);
            when(rule2.priceToAggregate(items)).thenReturn(2.5);

            Amazon amazon = new Amazon(cart, List.of(rule1, rule2));
            Item item = new Item(ItemType.ELECTRONIC, "Laptop", 1, 1000.0);

            amazon.addToCart(item);
            double result = amazon.calculate();

            verify(cart, times(1)).add(item);
            verify(cart, times(2)).getItems();
            verify(rule1, times(1)).priceToAggregate(items);
            verify(rule2, times(1)).priceToAggregate(items);
            assertThat(result).isEqualTo(22.5);
        }
    }
}
