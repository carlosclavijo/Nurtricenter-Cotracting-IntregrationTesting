@Administrator
Feature: Perform a administrator creation

  Scenario: Perform administrator creation with valid details
    Given an adminitrator with valid details
      | administratorName       | Carlos   |
      | administratorPhone      | 77777777 |
    When request is submitted for administrator creation
    Then verify that the Administrator HTTP response is 200
    And an administrator id is returned

  Scenario: Perform a failed administrator creation
    Given an incomplete administrator details
      | administratorPhone | 77887878 |
    When request is submitted for administrator creation
    Then verify that the Administrator HTTP response is 400