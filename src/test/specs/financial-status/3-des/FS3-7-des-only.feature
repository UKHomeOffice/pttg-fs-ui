Feature: Dependant only stuff

    Background:
        Given the api health check response has status 200
        And the api daily balance response will Pass
        And the api consent response will be SUCCESS
        And the api threshold response will be t4
        And the api condition codes response will be -3-1
        And caseworker is using the financial status service ui
        And caseworker is on page t4/status/dependant/des
        And consent is sought for the following:
        And the default details are
            | Application raised date         | 29/06/2016 |
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 100        |
            | Dependants                      | 1          |
            | DOB                             | 25/03/1987 |
            | Sort code                       | 33-33-33   |
            | Account number                  | 33333333   |

    Scenario: Rhianna is dependant only doctorate application and has sufficient funds
        When the financial status check is performed
        Then the service displays the following result
            | Outcome                    | Passed                                   |
            | Account holder name        | Laura Taylor                             |
            | Total funds required       | £16,090.00                               |
            | Maintenance period checked | 03/05/2016 to 30/05/2016                 |
            | Condition code             | 3 - Adult dependant\n1 - Child dependant |
            | Applicant type             | Doctorate extension scheme               |
            | Tier                       | Tier 4 (General)                         |
            | In London                  | Yes                                      |
            | Dependants                 | 1                                        |
            | Sort code                  | 33-33-33                                 |
            | Account number             | 33333333                                 |
            | DOB                        | 25/03/1987                               |
            | Application raised date    | 29/06/2016                               |

    Scenario: Latoya and Janet are a dependant only (x2) application for a Doctorate not in London and has sufficient funds
        When the financial status check is performed with
            | Application raised date | 20/06/2016 |
            | End date                | 30/05/2016 |
            | In London               | No         |
            | Dependants              | 2          |
        Then the service displays the following result
            | Outcome                    | Passed                                   |
            | Account holder name        | Laura Taylor                             |
            | Total funds required       | £16,090.00                               |
            | Maintenance period checked | 03/05/2016 to 30/05/2016                 |
            | Condition code             | 3 - Adult dependant\n1 - Child dependant |
            | Tier                       | Tier 4 (General)                         |
            | Applicant type             | Doctorate extension scheme               |
            | In London                  | No                                       |
            | Dependants                 | 2                                        |
            | Sort code                  | 33-33-33                                 |
            | Account number             | 33333333                                 |
            | DOB                        | 25/03/1987                               |
            | Application raised date    | 20/06/2016                               |

        ###################################### Dependant Only - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed text write up

    This is a scenario to check if Applicant does not meet minimum financial requirement text write up for dependants only
        Given the api daily balance response will Fail-low-balance

        When the financial status check is performed with
            | Application raised date | 29/06/2016 |
            | End date                | 30/05/2016 |
            | In London               | Yes        |
            | Dependants              | 1          |
        Then the service displays the following page content
            | Outcome        | Not passed                                                            |
            | Outcome detail | One or more daily closing balances are below the total funds required |
        And the service displays the following result headers in order
            | Account holder name      |
            | Total funds required     |
            | 28-day period checked    |
            | Lowest balance           |
            | Condition code           |
            | Estimated leave end date |
            | Result timestamp         |
        And the service displays the following criteria headers in order
            | Tier                     |
            | Applicant type           |
            | Dependant/Main applicant |
            | Application raised date  |
            | In London                |
            | Number of dependants     |
            | Sort code                |
            | Account number           |
            | Date of birth            |
