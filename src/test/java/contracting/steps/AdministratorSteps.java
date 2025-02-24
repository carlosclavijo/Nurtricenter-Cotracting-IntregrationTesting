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

    @Given("an administrator with valid details")
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
    public void submitAdministratorCreation() {
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

    @Then("an administrator is returned")
    public void checkAdministrator() {
        Response response = (Response) world.scenarioContext.get("response");
        String id = response.jsonPath().getString("administrator.id");
        String message = response.jsonPath().getString("message");

        Assert.assertNotNull(id);
        Assert.assertNotNull(message);

        Assert.assertTrue(
                "Message should be 'Administrator created successfully' or 'Administrator details retrieved successfully'",
                message.equals("Administrator created successfully") || message.equals("Administrator details retrieved successfully")
        );
        Assert.assertTrue(id.matches("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$"));
    }

    @Then("an error message is returned and is {string}")
    public void errorMessageIsReturned(String alert) {
        Response response = (Response) world.scenarioContext.get("response");
        Map<String, List<String>> errors = response.jsonPath().getMap("errors");
        String firstKey = errors.keySet().iterator().next();
        String firstErrorMessage = errors.get(firstKey).get(0);
        Assert.assertNotNull(firstErrorMessage);
        Assert.assertEquals(alert, firstErrorMessage);
    }

    @When("request is submitted to get the list of administrators")
    public void getListOfAdministrators() {
        String url = envConfig.getProperty("contracting-service_url") + envConfig.getProperty("contracting-administrator_api");
        Response response = request.accept(ContentType.JSON).when().get(url);
        world.scenarioContext.put("response", response);
    }

    @Then("a list of administrators is returned")
    public void checkAdministratorsList() {
        Response response = (Response) world.scenarioContext.get("response");

        List<Map<String, String>> administrators = response.jsonPath().getList("administrators");
        Assert.assertNotNull(administrators);
        Assert.assertFalse(administrators.isEmpty());

        for (Map<String, String> administrator : administrators) {
            Assert.assertNotNull(administrator.get("id"));
            Assert.assertNotNull(administrator.get("administratorName"));
            Assert.assertNotNull(administrator.get("administratorPhone"));
            Assert.assertTrue(administrator.get("id").matches("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$"));
        }
    }

    @Given("get the administrator which id is {string}")
    public void setAdministratorId(String id) {
        world.scenarioContext.put("administratorId", id);
    }

    @When("request is submitted to get the administrator with id is {string}")
    public void getAdministratorById(String id) {
        String url = envConfig.getProperty("contracting-service_url") + envConfig.getProperty("contracting-administrator_api") + "/" + id;
        Response response = request.accept(ContentType.JSON).when().get(url);
        world.scenarioContext.put("response", response);
    }

    @Then("returns an error message about not finding an Administrator with that Id")
    public void getNoAdministratorById() {
        Response response = (Response) world.scenarioContext.get("response");
        String message = response.jsonPath().getString("message");
        Assert.assertNotNull(response);
        Assert.assertEquals("Administrator not found", message);
    }
}
