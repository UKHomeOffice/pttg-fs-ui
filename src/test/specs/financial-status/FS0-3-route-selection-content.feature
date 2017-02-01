Feature: Route selection screen content - Tiers 2, 4 or 5

 ###################################### Section - Check for text on route selection page ######################################

    Scenario: Input Page checks for application type selection
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        Then the service displays the following page content
            | Page title | Check financial status - A service to prove an applicant meets the financial status requirements using an api linked to their bank account. |

    Scenario: Input Page checks for application type selection
        Given the api health check response has status 200
        And the Tier 2 <student-type> student type is chosen
        Then the service displays the following page content|
            | applicant-type-t2main-label      | Main applicant (with & without dependants) |
            | applicant-type-t2dependant-label | Dependant only

    Scenario: Input Page checks for application type selection
        Given the api health check response has status 200
        And the Tier 4 <student-type> student type is chosen
        Then the service displays the following page content|
            | applicant-type-nondoctorate-label | General student                  |
            | applicant-type-doctorate-label    | Doctorate extension scheme       |
            | applicant-type-pgdd-label         | Postgraduate doctor or dentist   |
            | applicant-type-sso-label          | Student union sabbatical officer

    Scenario: Input Page checks for application type selection
        Given the api health check response has status 200
        And the Tier 5 <student-type> student type is chosen
        Then the service displays the following page content|
            | Applicant-type-t5main-label      | Main applicant (with & without dependants) |
            | Applicant-type-t5dependant-label | Dependant only
