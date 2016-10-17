Feature: Non Doctorate Content - Tier 4 (General) student (single current account with dependants)

    Background:
        Given caseworker is using the financial status service ui
        And the non-doctorate student type is chosen


 ###################################### Section - Check for text on Output meets minimum financial requirement - Pass page ######################################

    Scenario: Page checks for Passed text write up
    This is a scenario to check if applicant meets minimum financial requirement text write up
        Given the account has sufficient funds
        When the financial status check is performed
        Then the service displays the following page content
            | Page dynamic heading | Passed                                          |
            | Page dynamic detail  | This applicant meets the financial requirements |
        And the service displays the following results headers in order
            | Account holder name |
            | Total funds required  |
            | 28-day period checked |
            | Course length         |
        And the service displays the following your search headers in order
            | Student type                    |
            | In London                       |
            | Course dates                    |
            | Total tuition fees              |
            | Tuition fees already paid       |
            | Accommodation fees already paid |
            | Number of dependants            |
            | Sort code                       |
            | Account number                  |
            | Date of birth                   |

 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    ## Changed in Jira number PT-24 - Added new 'Page dynamic detail' text
    Scenario: Page checks for Not Passed text write up 1
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given the account does not have sufficient funds
        When the financial status check is performed
        Then the service displays the following page content
            | Page dynamic heading | Not passed                                              |
            | Page dynamic detail  | One or more daily closing balances are below the total funds required |
        And the service displays the following results headers in order
            | Account holder name   |
            | Total funds required  |
            | 28-day period checked |
            | Course length         |
            | Lowest balance        |
        And the service displays the following your search headers in order
            | Student type                    |
            | In London                       |
            | Course dates                    |
            | Total tuition fees              |
            | Tuition fees already paid       |
            | Accommodation fees already paid |
            | Number of dependants            |
            | Sort code                       |
            | Account number                  |
            | Date of birth                   |

    ## Changed in Jira number PT-25 - Added new 'Page dynamic detail' text
    Scenario: Page checks for Not Passed text write up
    This is a scenario to check if Applicant has less than 28 days funds text write up
        Given the account does not have sufficient records
        When the financial status check is performed
        Then the service displays the following page content
            | Page dynamic heading | Not passed                                       |
            | Page dynamic detail  | The records for this account does not cover the whole 28 day period |
        And the service displays the following results headers in order
            | Account holder name   |
            | Total funds required  |
            | 28-day period checked |
        And the service displays the following your search headers in order
            | Student type                    |
            | In London                       |
            | Course dates                    |
            | Total tuition fees              |
            | Tuition fees already paid       |
            | Accommodation fees already paid |
            | Number of dependants            |
            | Sort code                       |
            | Account number                  |
            | Date of birth                   |


###################################### Section - Check for text on Output  - Insufficient Information ######################################

    Scenario: No records exist within the period stated
        Given no record for the account
        When the financial status check is performed
        Then the service displays the following page content
            | Page dynamic heading | Invalid or inaccessible account                                                  |
            | Page Dynamic detail  | One or more of the following conditions prevented us from accessing the account: |
        And the service displays the following your search headers in order
            | Sort code      |
            | Account number |

