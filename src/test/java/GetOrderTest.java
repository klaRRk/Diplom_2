import methods.SpecRequest;
import org.junit.*;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.User;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import methods.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class GetOrderTest {
    private final String email;
    private final String password;
    private final String name;
    private UserRequest userRequest;
    private OrderRequest orderRequest;
    private String accessToken;

    public GetOrderTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        return new Object[][]{
                {Faker.instance().internet().emailAddress(), Faker.instance().internet().password(), Faker.instance().name().firstName()},
                {Faker.instance().internet().emailAddress(), Faker.instance().internet().password(), Faker.instance().name().firstName()},
                {Faker.instance().internet().emailAddress(), Faker.instance().internet().password(), Faker.instance().name().firstName()}
        };
    }

    @Before
    public void setUp() {
        RestAssured.requestSpecification = SpecRequest.requestSpecification();
        orderRequest = new OrderRequest();
        userRequest = new UserRequest();
        User user = new User(email, password, name);
        Response responseCreate = userRequest.createUser(user);
        accessToken = responseCreate.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Проверка получения заказов авторизованного пользователя")
    public void getOrderTest() {
        Response responseGetOrder = orderRequest.getUserOrder(accessToken);
        responseGetOrder.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders.total", notNullValue());
    }

    @Test
    @DisplayName("Проверка получения заказов неавторизованного пользователя")
    public void getOrderNoAuth() {
        Response responseGetOrderNoAuth = orderRequest.getNoUserOrder();
        responseGetOrderNoAuth.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void deleteUser() {
        Response responseDelete = userRequest.deleteUser(accessToken);
        responseDelete.then()
                .statusCode(202)
                .body("success", equalTo(true));
    }
}