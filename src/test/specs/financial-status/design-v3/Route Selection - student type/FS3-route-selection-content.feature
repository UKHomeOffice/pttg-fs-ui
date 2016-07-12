Feature: Route selection screen content - Tier 4 (General) student non Doctorate and Doctorate In London (single current account and no dependants)

    Acceptance criteria



 ###################################### Section - Check for text on route selection page ######################################

    Scenario: Input Page checks for if Applicant meets minimum financial requirement text write up
        Given caseworker is using the financial status service ui
        When the caseworker views the query page
        Then the service displays the following page content
            | Page heading  | Financial status check                                                                                |
            | Page sub text | Online statement checker for a Barclays current account holder (must be in the applicant’s own name). |


