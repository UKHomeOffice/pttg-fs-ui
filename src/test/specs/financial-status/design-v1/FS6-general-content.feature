Feature: General Content - Tier 4 (General) student (single current account and no dependants)


 ###################################### Section - Check for text on Output meets minimum financial requirement - Pass page ######################################

    Scenario: Page checks for Passed text write up
    This is a scenario to check if Applicant meets minimum financial requirement text write up
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                               | 01/06/2016  |
            | Total funds required                   | 2350        |
            | Sort code                              | 13-56-09    |
            | Account  number                        | 23568498    |
        Then the service displays the following result
            | Page dynamic heading                   | Passed      |
        And the service displays the following result headers in order
            | Total funds required   |
            | 28-day period checked  |
            | Sort code              |
            | Account number         |


    Scenario: Check for important text on the page
    This scenario is to check for required text on the page
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                     | 01/06/2016  |
            | Total funds required         | 2350        |
            | Sort code                    | 13-56-09    |
            | Account  number              | 23568498    |
        Then the service displays the following result
            | Page heading                 | Tier 4 (General) student |
            | Page sub heading             | Financial status check  |
            | Page dynamic detail          | This applicant meets all of the financial status requirements |


 ###################################### Section - Check for text on Output does not meet minimum financial requirement - Not Passed ######################################

    Scenario: Page checks for Not Passed text write up
    This is a scenario to check if Applicant does not meet minimum financial requirement text write up
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                               | 07/06/2016  |
            | Total funds required                   | 2350        |
            | Sort code                              | 13-56-09    |
            | Account  number                        | 23568498    |
        Then the service displays the following result
            | Page dynamic heading                   | Not passed  |
        And the service displays the following result headers in order
            | Total funds required   |
            | 28-day period checked  |
            | Sort code              |
            | Account number         |

    Scenario: Check for important text on the page
    This scenario is to check for required text on the page
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                               | 01/06/2016  |
            | Total funds required                   | 2350    |
            | Sort code                              | 13-56-09    |
            | Account  number                        | 23568498    |
        Then the service displays the following result
            | Page heading                           | Tier 4 (General) student |
            | Page sub heading                       | Financial status check   |
            | Page dynamic detail                    | This applicant meets all of the financial status requirements |


 ###################################### Section - Check for text on input page ######################################


    Scenario: Input Page checks for if Applicant meets minimum financial requirement text write up

        Given caseworker is using the financial status service ui
        When the caseworker views the query page
        Then the service displays the following page content
            | Page heading     | Tier 4 (General) student |
            | Page sub heading | Financial status check   |
            | Page sub text    | An online statement checker for Barclays current account holder (must be in the applicant's own name).|

