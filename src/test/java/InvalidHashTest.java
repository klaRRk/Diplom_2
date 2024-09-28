import org.junit.*;
import org.junit.runner.RunWith;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Order;
import org.junit.runners.Parameterized;
import methods.*;

import java.util.List;

@RunWith(Parameterized.class)
public class InvalidHashTest {
    private final List<String> ingredients;
    private OrderRequest orderRequest;
    private Order order;

    public InvalidHashTest(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Parameterized.Parameters
    public static Object[][] getData(){
        Faker faker = new Faker();
        return new Object[][]{
                {List.of(faker.number().digits(7))},
                {List.of(faker.number().digits(23))},
                {List.of(faker.number().digits(25))}
        };
    }

    @Before
    public void setUp() {
        RestAssured.requestSpecification = SpecRequest.requestSpecification();
        orderRequest = new OrderRequest();
        order = new Order(ingredients);
    }

    @Test
    @DisplayName("Проверка отправки невалидного хэша при создании заказа")
    public void createOrderInvalidHash(){
        Response responseCreate = orderRequest.createOrder(order);
        responseCreate.then().log().all().statusCode(500);
    }
}