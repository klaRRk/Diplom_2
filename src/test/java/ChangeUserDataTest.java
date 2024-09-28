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
public class ChangeUserDataTest {
    private Login login;
    private final String oldEmail;
    private final String newEmail;
    private final String oldPassword;
    private final String newPassword;
    private final String oldName;
    private final String newName;
    private UserRequest userRequest;
    private String accessToken;

    public ChangeUserDataTest(String oldEmail, String newEmail, String oldPassword, String newPassword, String oldName, String newName) {
        this.oldEmail = oldEmail;
        this.newEmail = newEmail;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        Faker faker = new Faker();
        return new Object[][]{
                {"klarrk@gmail.com", faker.internet().emailAddress(), "123445", faker.internet().password(), "Clark", faker.name().firstName()},
                {"klarrk@gmail.com", faker.internet().emailAddress(), "123445", "123445", "Clark", "Clark"},
                {"klarrk@gmail.com", "klarrk@gmail.com", "123445", faker.internet().password(), "Clark", "Clark"},
                {"klarrk@gmail.com", "klarrk1@gmail.com", "123445", "123445", "Clark", faker.name().firstName()}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BURGERS_URL;
        User user = new User(oldEmail, oldPassword, oldName);
        userRequest = new UserRequest();
        login = new Login(newEmail, newPassword);
        Response responseCreate = userRequest.createUser(user);
        accessToken = responseCreate.then().log().all().extract().path("accessToken");
    }

    @Test
    @DisplayName("Проверка изменения данных авторизованного пользователя")
    public void updateUserDataTest() {
        Response responseGetUser = userRequest.getUser(accessToken);
        responseGetUser.then().log().all().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(oldEmail))
                .body("user.name", equalTo(oldName));

        Response responseUpdate = userRequest.updateUser(new User(newEmail, newPassword, newName), accessToken);
        responseUpdate.then().log().all().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(newName));

        Response responseGetUserAfterUpdate = userRequest.getUser(accessToken);
        responseGetUserAfterUpdate.then().log().all().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(newName));

        Response responseLogin = userRequest.loginUser(login);
        responseLogin.then().log().all().statusCode(200);
    }

    @Test
    @DisplayName("Проверка изменения данных неавторизованного пользователя")
    public void updateUserDataNoAuthTest() {
        Response responseUpdateNoAuth = userRequest.updateUserNoAuth(new User(newEmail, newPassword, newName));
        responseUpdateNoAuth.then().log().all().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void deleteUser() {
        Response responseDelete = userRequest.deleteUser(accessToken);
        responseDelete.then().log().all().statusCode(202)
                .body("success", equalTo(true));
    }
}