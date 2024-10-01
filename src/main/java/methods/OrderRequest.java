package methods;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

import static constants.APIConstants.INGREDIENTS;
import static constants.APIConstants.ORDERS;
import static io.restassured.RestAssured.given;

public class OrderRequest {

    @Step("Получить данные об ингредиентах")
    public Response getIngredient(){
        return given()
                .get(INGREDIENTS);
    }

    @Step("Создать заказ")
    public Response createOrder(Order order){
        return given() // Возврат результата метода given
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post(ORDERS);
    }

    @Step("Получить заказы конкретного авторизованного пользователя")
    public Response getUserOrder(String token){
        return given()
                .header("Authorization", token)
                .get(ORDERS);
    }

    @Step("Получить заказы не авторизованного пользователя")
    public Response getNoUserOrder(){
        return given()
                .get(ORDERS);
    }
}