Feature: Route selection screen inputs - Tier 4 (General) student non Doctorate and Doctorate In London (single current account and no dependants)
    This screen allows caseworker to select Tier 4 Application route (student type), which will direct to the right Financial status check form

    Scenario Outline: Caseworker selects the student type - Tier 4 (General) student (non-doctorate)
        Given caseworker is using the financial status service ui
        When the <student-type> student type is chosen
        Then the service displays the <page-sub-heading> page sub heading
        Examples:
            | student-type  | page-sub-heading                                           |
            | non-doctorate | Tier 4 (General) student                                   |
            | doctorate     | Tier 4 (General) student (doctorate extension scheme)      |
            | pgdd          | Tier 4 (General) student (post-graduate doctor or dentist) |
