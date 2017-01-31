Feature: Route selection screen inputs - All tiers
    This screen allows caseworker to select Tier 2, 4 & 5 application type, which will direct to the right Financial status check form

    Scenario Outline: Caseworker selects the student type - Tier 4 (General)
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        When the Tier 4 student type is chosen
        Then the service displays the <page-title> page sub heading
        Examples:
            | student-type  | page-title                       |
            | non-doctorate | General student                  |
            | doctorate     | Doctorate extension scheme       |
            | pgdd          | Postgraduate doctor or dentist   |
            | sso           | Student union sabbatical officer |

    Scenario Outline: Caseworker selects student type - T4 (General) and page-title for Barclays radio button to appear

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the Tier 4 <student-type> is chosen
        And the <page-title> is chosen
        Examples:
            | student-type  | page-title                       |
            | non-doctorate | General student                  |
            | doctorate     | Doctorate extension scheme       |
            | pgdd          | Postgraduate doctor or dentist   |
            | sso           | Student union sabbatical officer |
        Then the caseworker can select the Yes, check Barclays radio button or the No radio button

    Scenario Outline: Caseworker selects Tier 2 (General)

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        When the Tier 2 <student-type> student type is chosen
        Then the service displays the <page-title> page sub heading
        Examples:
            | student-type | page-title                                 |
            | t2main       | Main applicant (with & without dependants) |
            | t2dependant  | Dependant only                             |

    Scenario Outline: Caseworker selects student type - T2 and page-title for Barclays radio button to appear

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the Tier 2 <student-type> student type is chosen
        And the <page-title> is chosen
        Examples:
            | student-type | page-title                                 |
            | t2main       | Main applicant (with & without dependants) |
            | t2dependant  | Dependant only                             |
        Then the caseworker can select the Yes, check Barclays radio button or the No radio button


    Scenario Outline: Caseworker selects Tier 5 (Temporary)

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        When the Tier 5 <student-type> student type is chosen
        Then the service displays the <page-title> page sub heading
        Examples:
            | student-type | page-title                                 |
            | t5main       | Main applicant (with & without dependants) |
            | t5dependant  | Dependant only                             |

    Scenario Outline: Caseworker selects student type - T5 and page-title for Barclays radio button to appear

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the Tier 5 <student-type> student type is chosen
        And the <page-title> is chosen
        Examples:
            | student-type | page-title                                 |
            | t5main       | Main applicant (with & without dependants) |
            | t5dependant  | Dependant only                             |
        Then the caseworker can select the Yes, check Barclays radio button or the No radio button
