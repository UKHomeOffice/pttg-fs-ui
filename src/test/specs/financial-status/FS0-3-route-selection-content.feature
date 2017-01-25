Feature: Route selection screen content - Tiers 2, 4 or 5

 ###################################### Section - Check for text on route selection page ######################################

    Scenario: Input Page checks for application type selection
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        Then the service displays the following page content
            | Page title                        | Online statement checker for a Barclays current account holder (must be in the applicant’s name only). |
#           | Tier-label               | Tier 2                                                                                                 |
            | applicant-type-t2main-label       | Main applicant (with & without dependants)                                                             |
            | applicant-type-t2dependant-label  | Dependant only                                                                                         |
#            | Tier-label               | Tier 4                                                                                                 |
            | applicant-type-nondoctorate-label | General student                                                                                        |
            | applicant-type-doctorate-label    | General doctorate extension scheme                                                                             |
            | applicant-type-pgdd-label         | General postgraduate doctor or dentist                                                                         |
            | applicant-type-sso-label          | General student union sabbatical officer                                                                       |
#            |# Tier-label               | Tier 5                                                                                                 |
            | Applicant-type-t2main-label       | Main applicant (with & without dependants)                                                             |
            | Applicant-type-t2dependant-label  | Dependant only                                                                                         |
