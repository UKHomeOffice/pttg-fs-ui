Feature: Edit search button to return UI populated with current values (for all routes on all output pages - pass & non passed)

########################################################################################################################
    Background:
        Given the api health check response has status 200
        And the api daily balance response will Pass
        And the api consent response will be SUCCESS
        And the api threshold response will be t4
        And caseworker is on page t4/calc/main/general

    Scenario: Caseworker needs to edit the information input into the UI after the API has been called and results displayed.
        Given the financial status check is performed with
            | Application raised date         | 02/05/2016 |
            | End Date                        | 01/05/2016 |
            | Dependants                      | 0          |
            | In London                       | No         |
            | Course Start Date               | 30/05/2016 |
            | Course End Date                 | 29/07/2016 |
            | Total tuition fees              | 3000       |
            | Tuition fees already paid       | 2000       |
            | Accommodation fees already paid | 100        |
            | Continuation course             | No         |
            | Course type                     | main       |
            | Course institution              | true       |
        When the edit search button is clicked
        Then the inputs will be populated with
            | Application raised date         | 02/05/2016 |
            | End Date                        | 01/05/2016 |
            | Dependants                      | 0          |
            | In London                       | No         |
            | Course Start Date               | 30/05/2016 |
            | Course End Date                 | 29/07/2016 |
            | Total tuition fees              | 3000       |
            | Tuition fees already paid       | 2000       |
            | Accommodation fees already paid | 100        |
