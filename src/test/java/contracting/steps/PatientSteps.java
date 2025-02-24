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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static util.Util.jsonTemplate;

public class PatientSteps {
    private final World world;
    private final Properties envConfig;
    private RequestSpecification request;

    public PatientSteps(World world) {
        this.world = world;
        this.envConfig = World.envConfig;
        this.world.featureContext = World.threadLocal.get();
    }

    @Before
    public void setUp() {
        request = RequestSpecificationFactory.getInstance();
    }

    @Given("a patient with valid details")
    public void getPatientValidData(@Transpose DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String patientName = data.get(0).get("patientName");
        String patientPhone = data.get(0).get("patientPhone");
        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("patientName", patientName);
        valuesToTemplate.put("patientPhone", patientPhone);

        String jsonAsString = jsonTemplate(envConfig.getProperty("contracting-patient_request"), valuesToTemplate);
        world.scenarioContext.put("requestStr", jsonAsString);
    }

    @Given("an incomplete patient details")
    public void getPatientInvalidName(@Transpose DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String patientName = data.get(0).get("patientName");
        String patientPhone = data.get(0).get("patientPhone");
        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("patientName", patientName);
        valuesToTemplate.put("patientPhone", patientPhone);
        String jsonAsString = jsonTemplate(envConfig.getProperty("contracting-patient_request"), valuesToTemplate);
        world.scenarioContext.put("requestStr", jsonAsString);
    }

    @When("request is submitted for patient creation")
    public void submitPatientCreation() {
        String payload = world.scenarioContext.get("requestStr").toString();
        Response response = request
                .accept(ContentType.JSON)
                .body(payload)
                .contentType(ContentType.JSON)
                .when().post(envConfig.getProperty("contracting-service_url")
                        + envConfig.getProperty("contracting-patient_api"));
        world.scenarioContext.put("response", response);
    }

    @Then("verify that the Patient HTTP response is {int}")
    public void verifyHTTPResponseCode(Integer status) {
        Response response = (Response) world.scenarioContext.get("response");
        Integer actualStatusCode = response.then()
                .extract()
                .statusCode();
        Assert.assertEquals(status, actualStatusCode);
    }

    @Then("a patient is returned")
    public void checkPatient() {
        Response response = (Response) world.scenarioContext.get("response");
        String id = response.jsonPath().getString("patient.id");
        String message = response.jsonPath().getString("message");

        Assert.assertNotNull(id);
        Assert.assertNotNull(message);

        Assert.assertTrue(
                "Message should be 'Patient created successfully' or 'Patient details retrieved successfully'",
                message.equals("Patient created successfully") || message.equals("Patient details retrieved successfully")
        );
        Assert.assertTrue(id.matches("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$"));
    }

    @Then("an error message for patient is returned and is {string}")
    public void errorMessageIsReturned(String alert) {
        Response response = (Response) world.scenarioContext.get("response");
        Map<String, List<String>> errors = response.jsonPath().getMap("errors");
        String firstKey = errors.keySet().iterator().next();
        String firstErrorMessage = errors.get(firstKey).get(0);
        Assert.assertNotNull(firstErrorMessage);
        Assert.assertEquals(alert, firstErrorMessage);
    }

    @When("request is submitted to get the list of patients")
    public void getListOfPatients() {
        String url = envConfig.getProperty("contracting-service_url") + envConfig.getProperty("contracting-patient_api");
        Response response = request.accept(ContentType.JSON).when().get(url);
        world.scenarioContext.put("response", response);
    }

    @Then("a list of patients is returned")
    public void checkPatientsList() {
        Response response = (Response) world.scenarioContext.get("response");

        List<Map<String, String>> patients = response.jsonPath().getList("patients");
        Assert.assertNotNull(patients);
        Assert.assertFalse(patients.isEmpty());

        for (Map<String, String> patient : patients) {
            Assert.assertNotNull(patient.get("id"));
            Assert.assertNotNull(patient.get("patientName"));
            Assert.assertNotNull(patient.get("patientPhone"));
            Assert.assertTrue(patient.get("id").matches("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$"));
        }
    }

    @Given("get the patient which id is {string}")
    public void setPatientId(String id) {
        world.scenarioContext.put("patientId", id);
    }

    @When("request is submitted to get the patient with id is {string}")
    public void getPatientById(String id) {
        String url = envConfig.getProperty("contracting-service_url") + envConfig.getProperty("contracting-patient_api") + "/" + id;
        Response response = request.accept(ContentType.JSON).when().get(url);
        world.scenarioContext.put("response", response);
    }

    @Then("returns an error message about not finding an Patient with that Id")
    public void getNoPatientById() {
        Response response = (Response) world.scenarioContext.get("response");
        String message = response.jsonPath().getString("message");
        Assert.assertNotNull(response);
        Assert.assertEquals("Patient not found", message);
    }
}
