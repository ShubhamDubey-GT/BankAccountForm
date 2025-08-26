Feature: Open Savings Account form submission

  Scenario: Submit valid savings account application
    Given the user opens the savings account form
    When the user fills the form with valid data
    And the user submits the form
    Then the form should be submitted successfully