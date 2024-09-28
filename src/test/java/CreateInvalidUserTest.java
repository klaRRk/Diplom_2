import methods.SpecRequest;
import org.junit.*;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.User;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import methods.UserRequest;

import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateInvalidUserTest {
    private final String email;
    private final String password;
    private final String name;
    private final int statusCode;
    private final boolean success;
    private final String message;

    private User user;
    private UserRequest userRequest;
    private String accessToken;

    public CreateInvalidUserTest(String email, String password, String name, int statusCode, boolean success, String message) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.statusCode = statusCode;
        this.success = success;
        this.message = message;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        Faker faker = new Faker();
        return new Object[][]{
                {null, faker.internet().password(), faker.name().firstName(), 403, false, "Email, password and name are required fields"},
                {faker.internet().emailAddress(), null, faker.name().firstName(), 403, false, "Email, password and name are required fields"},
                {faker.internet().emailAddress(), faker.internet().password(), null, 403, false, "Email, password and name are required fields"},
                {faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName(), 200, true, null}
        };
    }

    @Before
    public void setUp() {
        RestAssured.requestSpecification = SpecRequest.requestSpecification();
        user = new User(email, password, name);
        userRequest = new UserRequest();
    }

    @Test
    @DisplayName("Проверка создания пользователя с НЕвалидными данными и проверка возможности дублирования существующего пользователя")
    public void createInvalidUser() {
        Response responseCreate = userRequest.createUser(user);
        responseCreate.then().log().all().statusCode(statusCode);

        if (statusCode != 200) {
            responseCreate.then().body("success", equalTo(success)).body("message", equalTo(message));
        } else {
            responseCreate.then().body("success", equalTo(success));
            accessToken = responseCreate.then().extract().path("accessToken");
            Response responseCreateDouble = userRequest.createUser(user);
            responseCreateDouble.then().log().all().statusCode(403)
                    .body("success", equalTo(false))
                    .body("message", equalTo("User already exists"));
        }
    }

    @After
    public void deleteUser() {
        if (statusCode == 200 && accessToken != null) {
            userRequest.deleteUser(accessToken);
        }
    }
}