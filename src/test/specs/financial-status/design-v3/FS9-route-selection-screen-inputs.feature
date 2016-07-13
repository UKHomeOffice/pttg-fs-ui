Feature: Route selection screen inputs - Tier 4 (General) student non Doctorate and Doctorate In London (single current account and no dependants)

    Acceptance Criteria

    This screen allows caseworker to select Tier 4 Application route (student type), which will direct to the right Financial status check form

    Student Types:
    Tier 4 (General) student (non-doctorate)
    Tier 4 (General) student (doctorate)

    Scenario Outline: Caseworker selects the student type - Tier 4 (General) student (non-doctorate)
        Given caseworker is using the financial status service ui
        When the <student-type> student type is chosen
        Then the service displays the <page-heading> page heading
        Examples:
            | student-type  | page-heading                             |
            | doctorate     | Tier 4 (General) student (doctorate)     |
            | non-doctorate | Tier 4 (General) student (non-doctorate) |
