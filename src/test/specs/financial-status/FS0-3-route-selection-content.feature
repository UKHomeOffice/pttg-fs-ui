Feature: Route selection screen content - Tier 4 (General) student non Doctorate and Doctorate In London (single current account and no dependants)

 ###################################### Section - Check for text on route selection page ######################################

    Scenario: Input Page checks for student type selection
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        Then the service displays the following page content
            | Page title           | Online statement checker for a Barclays current account holder (must be in the applicant’s name only). |
            | Student-type-0-label | General student                                                                                        |
            | Student-type-1-label | Doctorate extension scheme                                                                             |
            | Student-type-2-label | Postgraduate doctor or dentist                                                                         |
            | Student-type-3-label | Student union sabbatical officer                                                                       |
