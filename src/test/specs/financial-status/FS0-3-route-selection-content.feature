Feature: Route selection screen content - Tiers 2, 4 or 5

 ###################################### Section - Check for text on route selection page ######################################

    Scenario: Input Page checks for application type selection
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        Then the service displays the following page content
            | Page title               | Online statement checker for a Barclays current account holder (must be in the applicant’s name only). |
            | Tier-label               | Tier 2                                                                                                 |
            | Application-type-1-label | Main applicant (with & without dependants)                                                             |
            | Application-type-2-label | Dependants only                                                                                        |
            | Tier-label               | Tier 4                                                                                                 |
            | Student-type-0-label     | General student                                                                                        |
            | Student-type-1-label     | Doctorate extension scheme                                                                             |
            | Student-type-2-label     | Postgraduate doctor or dentist                                                                         |
            | Student-type-3-label     | Student union sabbatical officer                                                                       |
            | Tier-label               | Tier 5                                                                                                 |
            | Application-type-3-label | Main applicant (with & without dependants)                                                             |
            | Application-type-4-label | Dependants only                                                                                        |
