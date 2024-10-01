import methods.*;
import org.junit.*;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.User;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserTest {
    private final String email;
    private final String password;
    private final String name;
    private User user;
    private UserRequest userRequest;
    private String accessToken;

    public CreateUserTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        Faker faker = new Faker();
        return new Object[][]{
                {faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName()},
                {faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName()},
                {faker.internet().emailAddress(), faker.internet().password(), faker.name().firstName()}
        };
    }

    @Before
    public void setUp() {
        RestAssured.requestSpecification = SpecRequest.requestSpecification();
        user = new User(email, password, name);
        userRequest = new UserRequest();
    }

    @Test
    @DisplayName("Проверка создания пользователя")
    public void createUser() {
        Response responseCreate = userRequest.createUser(user);
        responseCreate.then().statusCode(200).assertThat().body("success", equalTo(true));
        accessToken = responseCreate.then().extract().path("accessToken");
        System.out.println(accessToken);
    }

    @After
    public void deleteUser() {
        Response responseDelete = userRequest.deleteUser(accessToken);
        responseDelete.then().statusCode(202).body("success", equalTo(true));
    }
}