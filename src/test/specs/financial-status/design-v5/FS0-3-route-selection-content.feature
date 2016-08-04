Feature: Route selection screen content - Tier 4 (General) student non Doctorate and Doctorate In London (single current account and no dependants)

 ###################################### Section - Check for text on route selection page ######################################

    Scenario: Input Page checks for student type selection
        Given caseworker is using the financial status service ui
        Then the service displays the following page content
            | Page sub heading     | Financial status check                                                                                   |
            | Page sub text        | An online statement checker for a Barclays current account holder (must be in the applicant's own name). |
            | Student type-1 label | Tier 4 (General) student                                                                                 |
            | Student type-2 label | Tier 4 (General) student (doctorate extension scheme)                                                    |
            | Student type-3 label | Tier 4 (General) student (post-graduate doctor or dentist)                                                    |
            | Student type-4 label | Tier 4 (General) student (sabbatical officer)
