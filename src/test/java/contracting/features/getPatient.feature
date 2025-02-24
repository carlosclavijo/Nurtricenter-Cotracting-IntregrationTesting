@Patient
Feature: Get a list of patients

  Scenario: Get the list of patients
    When request is submitted to get the list of patients
    Then verify that the Patient HTTP response is 200
    And a list of patients is returned

  Scenario: Perform get patient by id
    Given get the patient which id is "222f885c-e93b-4720-a5b7-b10eece7a35e"
    When request is submitted to get the patient with id is "222f885c-e93b-4720-a5b7-b10eece7a35e"
    Then verify that the Patient HTTP response is 200
    And a patient is returned

  Scenario: Perform an Id for an Patient that does not exist
    Given get the patient which id is "b72901b9-1397-4988-8b6d-0330c5d05a72"
    When request is submitted to get the patient with id is "b72901b9-1397-4988-8b6d-0330c5d05a72"
    Then verify that the Patient HTTP response is 404
    And returns an error message about not finding an Patient with that Id