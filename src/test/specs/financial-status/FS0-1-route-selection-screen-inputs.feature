Feature: Route selection screen inputs - All tiers
    This screen allows caseworker to select Tier 2, 4 & 5 application type, which will direct to the right Financial status check form

      Scenario: Caseworker selects the student type - Tier 4 (General)

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        When the Tier 4 student-type is chosen
        Then the service displays the page sub heading
            | non-doctorate | General student                  |
            | doctorate     | Doctorate extension scheme       |
            | pgdd          | Postgraduate doctor or dentist   |
            | sso           | Student union sabbatical officer |

    Scenario: Caseworker selects student type - T4 (General) and Barclays radio buttons appear

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the Tier 4 student-type is chosen
        When the <page-title> is chosen
        Examples:
            | page-title                       |
            | General student                  |
            | Doctorate extension scheme       |
            | Postgraduate doctor or dentist   |
            | Student union sabbatical officer |
        Then The service displays the following radio buttons
            | Yes, check Barclays |
            | No                  |

    Scenario: Caseworker selects Tier 2 (General)

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        When the Tier 2 student-type is chosen
        Then the service displays the page sub heading
            | student-type | page-title                                 |
            | t2main       | Main applicant (with & without dependants) |
            | t2dependant  | Dependant only                             |

    Scenario: Caseworker selects student type - T2 and and Barclays radio buttons appear

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the Tier 2 student-type is chosen
        And the <page-title> is chosen
        Examples:
            | page-title                                 |
            | Main applicant (with & without dependants) |
            | Dependant only                             |
        Then The service displays the following radio buttons
            | Yes, check Barclays |
            | No                  |

    Scenario: Caseworker selects Tier 5 (Temporary)

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        When the Tier 5 student-type is chosen
        Then the service displays the page sub heading
            | student-type | page-title                                 |
            | t5main       | Main applicant (with & without dependants) |
            | t5dependant  | Dependant only                             |

    Scenario: Caseworker selects student type - T5 and and Barclays radio buttons appear

        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the Tier 5 student-type is chosen
        And the <page-title> is chosen
        Examples:
            | page-title                                 |
            | Main applicant (with & without dependants) |
            | Dependant only                             |
        Then The service displays the following radio buttons
            | Yes, check Barclays |
            | No                  |
