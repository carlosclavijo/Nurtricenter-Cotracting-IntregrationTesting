@Patient
Feature: Perform a patient creation

  Scenario: Perform patient creation with valid details
    Given a patient with valid details
      | patientName       | Alberto Fernandez   |
      | patientPhone      | 66666666 |
    When request is submitted for patient creation
    Then verify that the Patient HTTP response is 201
    And a patient is returned

  Scenario: Perform a failed patient creation
    Given an incomplete patient details
      | patientPhone | 77887878 |
    When request is submitted for patient creation
    Then verify that the Patient HTTP response is 400
    And an error message for patient is returned and is "The PatientName field is required."

  Scenario: Perform a failed patient creation
    Given an incomplete patient details
      | patientName | Alberto Fernandez |
    When request is submitted for patient creation
    Then verify that the Patient HTTP response is 400
    And an error message for patient is returned and is "The PatientPhone field is required."