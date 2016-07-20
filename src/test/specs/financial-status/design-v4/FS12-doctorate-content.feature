Feature: Tier 4 (General) doctorate extension scheme content (single current account and no dependants)

    Background:
        Given caseworker is using the financial status service ui
        And the doctorate student type is chosen

 ###################################### Section - Check for text on Output meets minimum financial requirement - Pass page ######################################

    Scenario: Page checks for Passed text write up
    This is a scenario to check if applicant meets minimum financial requirement text write up
        Given the account has sufficient funds
        When the financial status check is performed
        Then the service displays the following page content
            | Page dynamic heading | Passed                                                |
            | Page heading         | Tier 4 (General) student (doctorate extension scheme) |
            | Page dynamic detail  | This applicant meets the financial requirements       |
        And the service displays the following results headers in order
            | Total funds required  |
            | 28-day period checked |
        And the service displays the following your search headers in order
            | Student type                    |
            | Inner London borough            |
            | Course length                   |
            | Accommodation fees already paid |
            | Number of dependants            |
            | Sort code                       |
            | Account number                  |


 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed text write up
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given the account does not have sufficient funds
        When the financial status check is performed
        Then the service displays the following page content
            | Page dynamic heading | Not passed                                              |
            | Page heading         | Tier 4 (General) student (doctorate extension scheme)   |
            | Page dynamic detail  | This applicant does not meet the financial requirements |
        And the service displays the following results headers in order
            | Total funds required              |
            | 28-day period checked             |
            | Date total funds required not met |
            | Amount on that date               |
        And the service displays the following your search headers in order
            | Student type                    |
            | Inner London borough            |
            | Course length                   |
            | Accommodation fees already paid |
            | Number of dependants            |
            | Sort code                       |
            | Account number                  |

 ###################################### Section - Check for text on Output  - Insufficient Information ######################################

    Scenario: Caseworker enters account number and sort code where no records exist within the period stated
        Given no record for the account
        When the financial status check is performed
        Then the service displays the following page content
            | Page dynamic heading | There is no record for the sort code and account number with Barclays                                                           |
            | Page Dynamic detail  | We couldn't perform the financial requirement check as no information exists for sort code 11-11-11 and account number 11111111 |
        And the service displays the following your search headers in order
            | Sort code      |
            | Account number |

 ###################################### Section - Check for text on input page ######################################

    Scenario: Input Page checks for if Applicant meets minimum financial requirement text write up
        Then the service displays the following page content
            | Page sub heading | Tier 4 (General) student (doctorate extension scheme) |



