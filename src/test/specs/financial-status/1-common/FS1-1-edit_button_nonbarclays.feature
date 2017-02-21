Feature: Edit search button to return UI populated with current values (for all routes on all output pages - pass & non passed)


########################################################################################################################

    Background:
        Given the api health check response has status 200
        Given the account has sufficient funds
        And caseworker is using the financial status service ui
        And the caseworker selects Tier four
        And the no option of the dependants only radio is selected
        And the submit button is clicked
        And the general student type is chosen
        And the caseworker selects the No radio button

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
            | Course type                     | Main       |
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
