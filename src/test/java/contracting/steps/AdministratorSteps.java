package contracting.steps;

import context.World;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import util.RequestSpecificationFactory;

import java.io.IOException;
import java.util.*;

import static util.Util.jsonTemplate;


public class AdministratorSteps {
    private final World world;
    private final Properties envConfig;
    private RequestSpecification request;

    public AdministratorSteps(World world) {
        this.world = world;
        this.envConfig = World.envConfig;
        this.world.featureContext = World.threadLocal.get();
    }

    @Before
    public void setUp() {
        request = RequestSpecificationFactory.getInstance();
    }

    @Given("an adminitrator with valid details")
    public void getAdministratorValidData(@Transpose DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String administratorName = data.get(0).get("administratorName");
        String administratorPhone = data.get(0).get("administratorPhone");
        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("administratorName", administratorName);
        valuesToTemplate.put("administratorPhone", administratorPhone);

        String jsonAsString = jsonTemplate(envConfig.getProperty("contracting-administrator_request"), valuesToTemplate);
        world.scenarioContext.put("requestStr", jsonAsString);
    }

    @Given("an incomplete administrator details")
    public void getAdministratorInvalidName(@Transpose DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String administratorName = data.get(0).get("administratorName");
        String administratorPhone = data.get(0).get("administratorPhone");
        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("administratorName", administratorName);
        valuesToTemplate.put("administratorPhone", administratorPhone);
        String jsonAsString = jsonTemplate(envConfig.getProperty("contracting-administrator_request"), valuesToTemplate);
        world.scenarioContext.put("requestStr", jsonAsString);
    }

    @When("request is submitted for administrator creation")
    public void submitAdminisrtatorCreation() {
        String payload = world.scenarioContext.get("requestStr").toString();
        Response response = request
                .accept(ContentType.JSON)
                .body(payload)
                .contentType(ContentType.JSON)
                .when().post(envConfig.getProperty("contracting-service_url")
                        + envConfig.getProperty("contracting-administrator_api"));
        world.scenarioContext.put("response", response);
    }

    @Then("verify that the Administrator HTTP response is {int}")
    public void verifyHTTPResponseCode(Integer status) {
        Response response = (Response) world.scenarioContext.get("response");
        Integer actualStatusCode = response.then()
                .extract()
                .statusCode();
        Assert.assertEquals(status, actualStatusCode);
    }

    @Then("an administrator id is returned")
    public void checkAdministratorId() {
        Response response = (Response) world.scenarioContext.get("response");
        String responseString = response.then().extract().asString();
        Assert.assertNotNull(responseString);
        Assert.assertNotEquals("", responseString);
        Assert.assertTrue(responseString.matches("\"[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}\""));
    }
}
