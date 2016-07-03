@DataDir=v2
Feature: Non Doctorate Content - Tier 4 (General) student (single current account and no dependants)

 ###################################### Section - Check for text on Output meets minimum financial requirement - Pass page ######################################

    Scenario: Page checks for Passed text write up
    This is a scenario to check if applicant meets minimum financial requirement text write up
        Given caseworker is using the financial status service ui
        Given the test data for account 22222222
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 9          |
            | Total tuition fees              | 9755.50    |
            | Tuition fees already paid       | 500        |
            | Accommodation fees already paid | 250.50     |
            | Sort code                       | 22-22-22   |
            | Account number                  | 22222222   |
        Then the service displays the following result
            | Page dynamic heading | Passed                                                          |
            | Page heading         | Tier 4 (General) student (non-doctorate)                        |
            | Page sub heading     | Financial Status check                                          |
            | Page dynamic detail  | This application meets all of the financial status requirements |
        And the service displays the following result headers in order
            | Total funds required  |
            | 28-day period checked |



 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed text write up
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given caseworker is using the financial status service ui
        Given the test data for account 33333333
        When the financial status check is performed with
            | End date                        | 07/06/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 33-33-33   |
            | Account number                  | 33333333   |
        Then the service displays the following result
            | Page dynamic heading | Not passed |
        And the service displays the following your search headers in order
            | Sort code                       |
            | Account number                  |
            | Inner London borough            |
            | Course length                   |
            | Total tuition fees              |
            | Tuition fees already paid       |
            | Accommodation fees already paid |

    Scenario: Check for important text on the page
    This scenario is to check for required text on the page
        Given caseworker is using the financial status service ui
        Given the test data for account 33333333
        When the financial status check is performed with
            | End date                        | 07/06/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 33-33-33   |
            | Account number                  | 33333333   |
        Then The FSPS Tier Four general Case Worker tool input page provides the following result
            | Page heading        | Tier 4 (General) student                                |
            | Page sub heading    | Financial status check                                  |
            | Page dynamic detail | This applicant does not meet the financial requirements |


###################################### Section - Check for text on Output  - Insufficient Information ######################################



    Scenario: Caseworker enters account number and sort code where no records exist within the period stated (no test data for all 9's)
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                        | 07/06/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 99-99-99   |
            | Account number                  | 99999999   |
        Then the service displays the following result
            | Page dynamic heading  | There is no record for the sort code and account number with Barclays                                                           |
            | Page dynamic sub text | We couldn't perform the financial requirement check as no information exists for sort code 99-99-99 and account number 99999999. |
        And the service displays the following your search data
            | Sort code                       |99-99-99|
            | Account number                  |99999999|


 ###################################### Section - Check for text on input page ######################################


    Scenario: Input Page checks for if Applicant meets minimum financial requirement text write up

        Given caseworker is using the financial status service ui
        When Case worker is on the input page

        Then The FSPS Tier Four general Case Worker tool input page provides the following result
            | Page heading     | Tier 4 (General) student (non-doctorate)                                                               |
            | Page sub heading | Financial status check                                                                                 |
            | Page sub text    | An online statement checker for Barclays current account holder (must be in the applicant's own name). |


