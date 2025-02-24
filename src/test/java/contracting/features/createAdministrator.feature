@Administrator
Feature: Perform a administrator creation

  Scenario: Perform administrator creation with valid details
    Given an administrator with valid details
      | administratorName       | Carlos Clavijo  |
      | administratorPhone      | 77777777 |
    When request is submitted for administrator creation
    Then verify that the Administrator HTTP response is 201
    And an administrator is returned

  Scenario: Perform a failed administrator creation
    Given an incomplete administrator details
      | administratorPhone | 77887878 |
    When request is submitted for administrator creation
    Then verify that the Administrator HTTP response is 400
    And an error message is returned and is "The AdministratorName field is required."

  Scenario: Perform a failed administrator creation
    Given an incomplete administrator details
      | administratorName | Carlos Clavijo |
    When request is submitted for administrator creation
    Then verify that the Administrator HTTP response is 400
    And an error message is returned and is "The AdministratorPhone field is required."