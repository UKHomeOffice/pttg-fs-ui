Feature: Handle the errors and bad request responses from the Barclays Consent API

    Background:
        Given the default details are
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

    Scenario Outline: Consent response indicates a bad request
        Given the api health check response has status 200
        And the api consent response will be <Status>
        And the api threshold response will be t4
        And caseworker is on page t4/application/status/main/general
        And the financial status check is performed
        Then the service displays the following result
            | Outcome        | Invalid or inaccessible account                                                  |
            | Outcome detail | One or more of the following conditions prevented us from accessing the account: |
        Examples:
            | Status |
            | 400    |
            | 401    |
            | 403    |
            | 404    |
            | 499    |

    Scenario Outline: Consent response indicates a server error upstream
        Given the api health check response has status 200
        And the api consent response will be <Status>
        And the api threshold response will be t4
        And caseworker is on page t4/application/status/main/general
        And the financial status check is performed
        Then the service displays the following result
            | Outcome        | Error                                         |
            | Outcome detail | Something went wrong, please try again later. |
        Examples:
            | Status |
            | 500    |
            | 501    |
            | 599    |

    Scenario: Consent response indicates an error not covered by specific cases
        Given the api health check response has status 200
        And the api consent response will be ERROR
        And the api threshold response will be t4
        And caseworker is on page t4/application/status/main/general
        And the financial status check is performed
        Then the service displays the following result
            | Outcome        | Error                                         |
            | Outcome detail | Mobile number has been updated within 14 days |
