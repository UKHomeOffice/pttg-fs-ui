Feature: Route selection screen inputs - Tier 4 (General) student non Doctorate and Doctorate In London (single current account and no dependants)
    This screen allows caseworker to select Tier 4 Application route (student type), which will direct to the right Financial status check form

    Scenario Outline: Caseworker selects the student type - Tier 4 (General) student (non-doctorate)
        Given caseworker is using the financial status service ui
        When the <student-type> student type is chosen
        Then the service displays the <page-title> page sub heading
        Examples:
            | student-type  | page-title                       |
            | non-doctorate | General student                  |
            | doctorate     | Doctorate extension scheme       |
            | pgdd          | Postgraduate doctor or dentist   |
            | sso           | Student union sabbatical officer |
