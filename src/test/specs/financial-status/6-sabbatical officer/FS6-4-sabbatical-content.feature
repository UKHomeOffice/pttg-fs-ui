Feature: Tier 4 (General) student union (sabbatical officer) content (single current account with dependants)

    Background:
        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api daily balance response will Pass
        And the api threshold response will be t4
        And caseworker is using the financial status service ui
        And the default details are
            | Application raised date         | 10/06/2016 |
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 01/03/2016 |
            | Course end date                 | 30/03/2016 |
            | Dependants                      | 2          |
            | Continuation Course             | No         |
            | Accommodation fees already paid | 0          |

 ###################################### Section - Check for text on Output meets minimum financial requirement - Pass page ######################################

    Scenario: Page checks for Passed text write up
    This is a scenario to check if applicant meets minimum financial requirement text write up
        Given the account has sufficient funds
        And caseworker is on page t4/sso/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        When the financial status check is performed
        Then the service displays the following page content
            | Outcome        | Passed                                          |
            | Outcome detail | This applicant meets the financial requirements |
        And the service displays the following result headers in order
            | Account holder name      |
            | Total funds required     |
            | 28-day period checked    |
            | Estimated leave end date |
            | Result timestamp         |
            | Course length            |
        And the service displays the following criteria headers in order
            | Tier                            |
            | Applicant type                  |
            | Dependant/Main applicant        |
            | Application raised date         |
            | Course dates checked            |
            | Continuation course             |
            | In London                       |
            | Accommodation fees already paid |
            | Number of dependants            |
            | Sort code                       |
            | Account number                  |
            | Date of birth                   |



 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed due to low balance text write up
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given the api daily balance response will Fail-low-balance
        And caseworker is on page t4/sso/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        When the financial status check is performed
        Then the service displays the following page content
            | Outcome        | Not passed                                                            |
            | Outcome detail | One or more daily closing balances are below the total funds required |
        And the service displays the following result headers in order
            | Account holder name      |
            | Total funds required     |
            | 28-day period checked    |
            | Lowest balance           |
            | Estimated leave end date |
            | Result timestamp         |
            | Course length            |
        And the service displays the following criteria headers in order
            | Tier                            |
            | Applicant type                  |
            | Dependant/Main applicant        |
            | Application raised date         |
            | Course dates checked            |
            | Continuation course             |
            | In London                       |
            | Accommodation fees already paid |
            | Number of dependants            |
            | Sort code                       |
            | Account number                  |
            | Date of birth                   |

    Scenario: Page checks for Not Passed due to record count text write up
    This is a scenario to check if Applicant has less than 28 days funds text write up
        Given the api daily balance response will Fail-record-count
        And caseworker is on page t4/sso/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        When the financial status check is performed with
            | Application raised date         | 10/06/2016 |
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 01/03/2016 |
            | Course end date                 | 30/03/2016 |
            | Accommodation fees already paid | 100        |
            | Dependants                      | 0          |
            | Continuation Course             | Yes        |
            | Original Course Start Date      | 30/10/2015 |
        Then the service displays the following page content
            | Outcome        | Not passed                                                          |
            | Outcome detail | The records for this account does not cover the whole 28 day period |
        And the service displays the following result headers in order
            | Account holder name   |
            | Total funds required  |
            | 28-day period checked |
        And the service displays the following criteria headers in order
            | Tier                            |
            | Applicant type                  |
            | Dependant/Main applicant        |
            | Application raised date         |
            | Course dates checked            |
            | Continuation course             |
            | Original course start date      |
            | In London                       |
            | Accommodation fees already paid |
            | Number of dependants            |
            | Sort code                       |
            | Account number                  |
            | Date of birth                   |

###################################### Dependant Only - Check for text on Output meets minimum financial requirement - Pass page ######################################

    Scenario: Page checks for Passed text write up - dependants only
    This is a scenario to check if applicant meets minimum financial requirement text write up
        Given the account has sufficient funds
        And caseworker is on page t4/sso-dependants/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        When the financial status check is performed
        Then the service displays the following page content
            | Outcome        | Passed                                          |
            | Outcome detail | This applicant meets the financial requirements |
        And the service displays the following result headers in order
            | Account holder name      |
            | Total funds required     |
            | 28-day period checked    |
            | Estimated leave end date |
            | Result timestamp         |
            | Course length            |
        And the service displays the following criteria headers in order
            | Tier                     |
            | Applicant type           |
            | Dependant/Main applicant |
            | Application raised date  |
            | Course dates checked     |
            | Continuation course      |
            | In London                |
            | Number of dependants     |
            | Sort code                |
            | Account number           |
            | Date of birth            |

###################################### Dependant Only - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed due to low balance text write up - dependants only
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given the api daily balance response will Fail-low-balance
        And caseworker is on page t4/sso-dependants/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        When the financial status check is performed
        Then the service displays the following page content
            | Outcome        | Not passed                                                            |
            | Outcome detail | One or more daily closing balances are below the total funds required |
        And the service displays the following result headers in order
            | Account holder name      |
            | Total funds required     |
            | 28-day period checked    |
            | Lowest balance           |
            | Estimated leave end date |
            | Result timestamp         |
            | Course length            |
        And the service displays the following criteria headers in order
            | Tier                     |
            | Applicant type           |
            | Dependant/Main applicant |
            | Application raised date  |
            | Course dates checked     |
            | Continuation course      |
            | In London                |
            | Number of dependants     |
            | Sort code                |
            | Account number           |
            | Date of birth            |
