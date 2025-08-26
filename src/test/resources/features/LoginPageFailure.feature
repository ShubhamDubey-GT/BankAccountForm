Feature: Savings Account form validations

Background:
Given the user opens the savings account form

Scenario: Missing required Full Name blocks submission
When the user fills the form with valid data except "Full Name"
And the user submits the form
Then the browser should show a required validation for "Full Name"

Scenario: Missing required Father Name blocks submission
When the user fills the form with valid data except "Father Name"
And the user submits the form
Then the browser should show a required validation for "Father Name"

Scenario: Invalid email format shows validation
When the user fills the form with an invalid email "not-an-email"
And the user submits the form
Then the browser should show a type mismatch validation for "Email Address"

Scenario: Phone is required
When the user fills the form with valid data except "Mobile Number"
And the user submits the form
Then the browser should show a required validation for "Mobile Number"

Scenario: Initial deposit cannot be negative
When the user fills the form with initial deposit "-1"
And the user submits the form
Then the browser should show a range validation for "Initial Deposit Amount ($)"

Scenario: Select each account type option
When the user selects account type "Regular Savings"
And the user selects account type "Student Account"
And the user selects account type "Senior Citizen"
Then the account type field should reflect the last selected value "Senior Citizen"

Scenario: Date of Birth required
When the user fills the form with valid data except "Date of Birth"
And the user submits the form
Then the browser should show a required validation for "Date of Birth"