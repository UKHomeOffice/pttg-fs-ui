Feature: Route selection screen inputs - Tier 4 (General) student non Doctorate and Doctorate In London (single current account and no dependants)

    Acceptance Criteria

    This screen allows caseworker to select Tier 4 Application route (student type), which will direct to the right Financial status check form

    Student Types:
    Tier 4 (General) student (non-doctorate)
    Tier 4 (General) student (doctorate)

    Initially none of the options is selected
    One of the options is mandatory to select before going to the next page


    Scenario: Caseworker selects the student type - Tier 4 (General) student (non-doctorate)

        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Tier 4 (General) student | Yes |
        Then the service displays the following result
          # End Data??  | End Date                        |  |
            | Inner London borough            |  |
            | Course length                   |  |
            | Total tuition fees              |  |
            | Tuition fees already paid       |  |
            | Accommodation fees already paid |  |
            | Sort code                       |  |
            | Account number                  |  |


    Scenario: Caseworker selects the student type - Tier 4 (General) student (doctorate)

        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | Tier 4 (General) doctorate extension scheme | Yes |
        Then the service displays the following result
          # End Data??  | End Date                        |  |
            | Inner London borough            |  |
            | Remaining course length         |  |
            | Tuition fees already paid       |  |
            | Accommodation fees already paid |  |
            | Sort code                       |  |
            | Account number                  |  |
