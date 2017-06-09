Feature: Handle the responses from the Barclays Consent API and display the appropriate output page when applicant consent is not granted.

    The Barclays Consent API response will return a consent 'status' (Success, Failure, Invalid, Pending) when invoked.

    The service will display the corresponding output page dependant on the status.

            ## 'Pending' consent status followed by 'Failure' status (e.g. consent not granted) ##
    Scenario Outline: Consent status is in 'Pending' status when the financial status check is performed
        Given the api health check response has status 200
        And the api consent response will be PENDING
        And the api threshold response will be t<Tier>
        And caseworker is using the financial status service ui
        And caseworker is on page t<Tier>/application/status/<Applicant>
        And the financial status check is performed with
            | Application raised date | 30/07/2016 |
            | End Date                | 04/07/2016 |
            | Dependants              | 1          |
            | DOB                     | 25/03/1987 |
            | Sort code               | 22-22-23   |
            | Account number          | 22222229   |
        And after at least 5 seconds
        And the api consent response will be FAILURE
        And the check again button is clicked
        Then the service displays the following result
            | Outcome              | Consent not given                                        |
            | Outcome detail       | Applicant has refused permission to access their account |
            | Total funds required | <Funds>                                                  |
        Examples:
            | Tier | Applicant | Funds     |
            | 2    | main      | £945.00   |
            | 5    | dependant | £1,575.00 |


        ## 'Failure' consent status received immediately when requesting consent (before the financial status check is performed) ##
    Scenario Outline: 'Failure' consent status received immediately when requesting consent (before the financial status check is performed)
        Given the api health check response has status 200
        And the api consent response will be FAILURE
        And caseworker is using the financial status service ui
        And caseworker is on page t<Tier>/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222225   |
        Then the service displays the following page content
            | Outcome        | Consent not given                                        |
            | Outcome detail | Applicant has refused permission to access their account |
        Examples:
            | Tier      |
            | Tier two  |
            | Tier five |


    Scenario: T4 Main applicant - Consent granted, balances API invoked and data received - display result page
        Given the api health check response has status 200
        And the api consent response will be INVALID
        And the api threshold response will be t4
        And the default details are
            | Application raised date    | 31/05/2016 |
            | End date                   | 30/05/2016 |
            | In London                  | Yes        |
            | Course start date          | 30/05/2016 |
            | Course end date            | 30/11/2016 |
            | Tuition fees               | 8500.00    |
            | Tuition fees paid          | 0          |
            | Accommodation fees paid    | 0          |
            | Dependants                 | 1          |
            | Continuation Course        | No         |
            | Original Course Start Date | 30/10/2015 |
            | Course type                | main       |
            | Course institution         | true       |
            | DOB                        | 21/09/1981 |
            | Sort code                  | 11-11-11   |
            | Account number             | 11111111   |
        And caseworker is using the financial status service ui
        And caseworker is on page t4/application/status/main/general
        And the financial status check is performed
        Then the service displays the following result
            | Outcome        | Consent not given                                        |
            | Outcome detail | Applicant has refused permission to access their account |
