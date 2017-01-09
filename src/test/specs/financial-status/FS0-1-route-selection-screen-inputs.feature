Feature: Route selection screen inputs - All tiers
    This screen allows caseworker to select Tier 2, 4 & 5 application type, which will direct to the right Financial status check form

    Scenario Outline: Caseworker selects the student type - Tier 4 (General) student (non-doctorate)
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        When the <student-type> student type is chosen
        Then the service displays the <page-title> page sub heading
        Examples:
            | student-type  | page-title                       |
            | non-doctorate | General student                  |
            | doctorate     | Doctorate extension scheme       |
            | pgdd          | Postgraduate doctor or dentist   |
            | sso           | Student union sabbatical officer |

    Scenario Outline: Caseworker selects Tier 2 (General)
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        When the <student-type> student type is chosen
        Then the service displays the <page-title> page sub heading
        Examples:
            | student-type                  | page-title                                 |
            | tier-2-general-mainapplicant) | Main applicant (with & without dependants) |
            | tier-2-general-dependant)     | Dependant only                             |

    Scenario Outline: Caseworker selects Tier 5 (Temporary)
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        When the <student-type> student type is chosen
        Then the service displays the <page-title> page sub heading
        Examples:
            | student-type                          | page-title                                 |
            | tier-5-temporaryworker-mainapplicant) | Main applicant (with & without dependants) |
            | tier-5-temporaryworker-dependant)     | Dependant only                             |
