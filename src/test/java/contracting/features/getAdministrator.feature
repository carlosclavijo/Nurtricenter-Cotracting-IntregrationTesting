@Administrator
Feature: Get a list of administrators

  Scenario: Get the list of administrators
    When request is submitted to get the list of administrators
    Then verify that the Administrator HTTP response is 200
    And a list of administrators is returned

  Scenario: Perform get administrator by id
    Given get the administrator which id is "b72901b9-1397-4988-8b6d-0330c5d05a74"
    When request is submitted to get the administrator with id is "b72901b9-1397-4988-8b6d-0330c5d05a74"
    Then verify that the Administrator HTTP response is 200
    And an administrator is returned

  Scenario: Perform an Id for an Administrator that does not exist
    Given get the administrator which id is "b72901b9-1397-4988-8b6d-0330c5d05a72"
    When request is submitted to get the administrator with id is "b72901b9-1397-4988-8b6d-0330c5d05a72"
    Then verify that the Administrator HTTP response is 404
    And returns an error message about not finding an Administrator with that Id