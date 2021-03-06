Feature: Non Doctorate Content - Tier 4 student (with dependants)

    Background:
        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api daily balance response will Pass
        And the api threshold response will be t4
        And caseworker is using the financial status service ui
        And caseworker is on page t4/application/status/main/general
        And the api condition codes response will be -3-1
        And the default details are
            | Application raised date | 05/06/2016 |
            | End date                | 30/05/2016 |
            | In London               | Yes        |
            | Course start date       | 30/05/2016 |
            | Course end date         | 30/11/2016 |
            | Tuition fees            | 8500.00    |
            | Tuition fees paid       | 0          |
            | Accommodation fees paid | 0          |
            | Dependants              | 1          |
            | Continuation Course     | No         |
            | Course type             | main       |
            | Course institution      | true       |
            | DOB                     | 25/03/1987 |
            | Sort code               | 11-11-11   |
            | Account number          | 11111111   |

 ###################################### Section - Check for text on Output meets minimum financial requirement - Pass page ######################################

    Scenario: Page checks for Passed text write up
    This is a scenario to check if applicant meets minimum financial requirement text write up
#        Given the account has sufficient funds
        When the financial status check is performed
        And after at least 1 seconds
        Then the service displays the following page content
            | Outcome        | Passed                                          |
            | Outcome detail | This applicant meets the financial requirements |
        And the service displays the following result headers in order
            | Account holder name      |
            | Total funds required     |
            | 28-day period checked    |
            | Condition code           |
            | Estimated leave end date |
            | Result timestamp         |
            | Course length            |
        And the service displays the following criteria headers in order
            | Tier                                  |
            | Applicant type                        |
            | Student type                          |
            | Application raised date               |
            | In London                             |
            | Accommodation fees already paid       |
            | Number of dependants                  |
            | Course dates checked                  |
            | Continuation course                   |
            | Course type                           |
            | Course institution                    |
            | Total tuition fees for the first year |
            | Tuition fees already paid             |
            | Sort code                             |
            | Account number                        |
            | Date of birth                         |

 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    ## Changed in Jira number PT-24 - Added new 'Page dynamic detail' text
    Scenario: Page checks for Not Passed text write up 1
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given the account does not have sufficient funds
        When the financial status check is performed
        And after at least 1 seconds
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
            | Course length            |
        And the service displays the following criteria headers in order
            | Tier                                  |
            | Applicant type                        |
            | Student type                          |
            | Application raised date               |
            | In London                             |
            | Accommodation fees already paid       |
            | Number of dependants                  |
            | Course dates checked                  |
            | Continuation course                   |
            | Course type                           |
            | Course institution                    |
            | Total tuition fees for the first year |
            | Tuition fees already paid             |
            | Sort code                             |
            | Account number                        |
            | Date of birth                         |

    ## Changed in Jira number PT-25 - Added new 'Page dynamic detail' text
    Scenario: Page checks for Not Passed text write up
    This is a scenario to check if Applicant has less than 28 days funds text write up
        Given the api consent response will be SUCCESS
        And the api daily balance response will Fail-record-count
        When the financial status check is performed
        And after at least 1 seconds
        Then the service displays the following page content
            | Outcome        | Not passed                                                          |
            | Outcome detail | The records for this account do not cover the whole 28 day period |
        And the service displays the following result headers in order
            | Account holder name      |
            | Total funds required     |
            | 28-day period checked    |
            | Condition code           |
            | Estimated leave end date |
            | Result timestamp         |
            | Course length            |
        And the service displays the following criteria headers in order
            | Tier                                  |
            | Applicant type                        |
            | Student type                          |
            | Application raised date               |
            | In London                             |
            | Accommodation fees already paid       |
            | Number of dependants                  |
            | Course dates checked                  |
            | Continuation course                   |
            | Course type                           |
            | Course institution                    |
            | Total tuition fees for the first year |
            | Tuition fees already paid             |
            | Sort code                             |
            | Account number                        |
            | Date of birth                         |
