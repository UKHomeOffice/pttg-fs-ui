Feature: Edit search button to return UI populated with current values (for all routes on all output pages - pass & non passed)

########################################################################################################################
    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the non-doctorate student type is chosen

    Scenario: Caseworker needs to edit the information input into the UI after the API has been called and results displayed.
        Given the financial status check is performed with
            | Application raised date         | 02/05/2016 |
            | End Date                        | 01/05/2016 |
            | DOB                             | 29/07/1978 |
            | Dependants                      | 0          |
            | Sort code                       | 33-33-33   |
            | Account number                  | 33333333   |
            | In London                       | No         |
            | Course Start Date               | 30/05/2016 |
            | Course End Date                 | 29/07/2016 |
            | Total tuition fees              | 3000       |
            | Tuition fees already paid       | 2000       |
            | Accommodation fees already paid | 100        |
            | Continuation course             | No         |
            | Course type                     | Main       |
        #And the submit button is pressed
        When the edit search button is clicked
        Then the inputs will be populated with
            | Application raised date         | 02/05/2016 |
            | End Date                        | 01/05/2016 |
            | DOB                             | 29/07/1978 |
            | Dependants                      | 0          |
            | Sort code                       | 33-33-33   |
            | Account number                  | 33333333   |
            | In London                       | No         |
            | Course Start Date               | 30/05/2016 |
            | Course End Date                 | 29/07/2016 |
            | Total tuition fees              | 3000       |
            | Tuition fees already paid       | 2000       |
            | Accommodation fees already paid | 100        |
