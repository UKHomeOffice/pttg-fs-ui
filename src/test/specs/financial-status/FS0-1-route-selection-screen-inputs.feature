Feature: Route selection screen inputs - All tiers
    This screen allows caseworker to select Tier 2, 4 & 5 application type, which will direct to the right Financial status check form

    Background:
        Given the api health check response has status 200

    Scenario: Caseworker selects the student type - Tier 4 (General)
        Given caseworker is on page t4
        Then applicant type should have the following options
            | nondoctorate | General student                  |
            | doctorate    | Doctorate extension scheme       |
            | pgdd         | Postgraduate doctor or dentist   |
            | sso          | Student union sabbatical officer |

    Scenario: Caseworker selects Tier 2 (General)
        Given caseworker is on page t2
        Then applicant type should have the following options
            | main      | Main applicant (with & without dependants) |
            | dependant | Dependant only                             |

    Scenario: Caseworker selects Tier 5
        Given caseworker is on page t5
        Then applicant type should have the following options
            | main      | Main applicant (with & without dependants) |
            | dependant | Dependant only                             |

    Scenario: Caseworker selects Tier 5
        Given caseworker is on page t5/main
        Then do check should have the following options
            | yes | Yes, check Barclays |
            | no  | No                  |
