import org.junit.*;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import methods.UserRequest;

import static constants.APIConstants.BURGERS_URL;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class LoginTest {
    private final String email;
    private final String password;
    private final int statusCode;
    private final boolean success;
    private User user;
    private UserRequest userRequest;
    private Login login;
    private String accessToken;
    private final int createStatusCode;
    private final String message;

    public LoginTest(String email, String password, int statusCode, boolean success, int createStatusCode, String message) {
        this.email = email;
        this.password = password;
        this.statusCode = statusCode;
        this.success = success;
        this.createStatusCode = createStatusCode;
        this.message = message;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        Faker faker = new Faker();
        return new Object[][]{
                {"cobain@yandex.com", "!nirvana969", 200, true, 200, null},
                {"cobain@yandex.com", faker.internet().password(), 401, false, 200, "email or password are incorrect"},
                {null, "!nirvana969", 401, false, 200, "email or password are incorrect"},
                {"cobain@yandex.com", null, 401, false, 200, "email or password are incorrect"},
                {faker.internet().emailAddress(), "987283467", 401, false, 200, "email or password are incorrect"}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BURGERS_URL;
        user = new User("cobain@yandex.com", "!nirvana969", "Kurt");
        userRequest = new UserRequest();
        login = new Login(email, password);
    }

    @Test
    @DisplayName("Проверка логина пользователя")
    public void loginUser() {
        Response responseCreate = userRequest.createUser(user);
        responseCreate.then().log().all().statusCode(createStatusCode);

        if (createStatusCode == 200) {
            accessToken = responseCreate.then().extract().path("accessToken");
        }

        Response responseLogin = userRequest.loginUser(login);
        responseLogin.then().log().all().statusCode(statusCode);

        if (statusCode == 200) {
            responseLogin.then().body("success", equalTo(success));
        } else {
            responseLogin.then().body("success", equalTo(success)).body("message", equalTo(message));
        }
    }

    @After
    public void deleteUser() {
        if (createStatusCode == 200 && accessToken != null) {
            Response responseDelete = userRequest.deleteUser(accessToken);
            responseDelete.then().log().all().statusCode(202).body("success", equalTo(true));
        }
    }
}