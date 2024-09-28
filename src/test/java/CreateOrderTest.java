import methods.SpecRequest;
import org.junit.*;
import org.junit.runner.RunWith;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Order;
import org.junit.runners.Parameterized;
import methods.OrderRequest;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private final int fromIndex;
    private final int toIndex;
    private final int expectedStatusCode;
    private OrderRequest orderRequest;

    public CreateOrderTest(int fromIndex, int toIndex, int expectedStatusCode) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.expectedStatusCode = expectedStatusCode;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        return new Object[][]{
                {0, 1, 200},
                {0, 7, 200},
                {10, 14, 200},
                {0, 0, 400},
        };
    }

    @Before
    public void setUp() {
        RestAssured.requestSpecification = SpecRequest.requestSpecification();
        orderRequest = new OrderRequest();
    }

    @Test
    @DisplayName("Проверка создания заказа")
    public void createOrder() {
        Response responseGetIngredient = orderRequest.getIngredient();
        List<String> ingredients = responseGetIngredient.then()
                .statusCode(200)
                .extract().path("data._id");

        Order order = new Order(ingredients.subList(fromIndex, Math.min(toIndex, ingredients.size())));
        Response responseCreate = orderRequest.createOrder(order);

        if (expectedStatusCode == 200) {
            responseCreate.then()
                    .statusCode(expectedStatusCode)
                    .body("order.number", notNullValue())
                    .body("success", equalTo(true));
        } else if (expectedStatusCode == 400) {
            responseCreate.then()
                    .statusCode(expectedStatusCode)
                    .body("success", equalTo(false))
                    .body("message", equalTo("Ingredient ids must be provided"));
        }
    }
}