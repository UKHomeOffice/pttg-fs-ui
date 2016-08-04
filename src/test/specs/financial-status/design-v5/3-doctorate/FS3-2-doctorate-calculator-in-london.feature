Feature: Total Funds Required Calculation - Tier 4 (General) Student Doctorate In London (single current account including dependants)

    Acceptance criteria

    Requirement to meet Tier 4 Doctorate passed and not passed

    In London - The applicant must show evidence of funds to cover £1,265 per month for 2 months (£2,530)

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate in London
    (£1265) * 2) -  Accommodation fees already paid

    Background:
        Given caseworker is using the financial status service ui
        And the doctorate student type is chosen


    Scenario: Shelly is a Doctorate in London student and has sufficient funds
        Given the account has sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 100        |
            | Number of dependants            | 0          |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
        Then the service displays the following result
            | Outcome                         | Passed                                                |
            | Total funds required            | £16,090.00                                            |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016                              |
            | Student type                    | Tier 4 (General) student (doctorate extension scheme) |
            | In London                       | Yes                                                   |
            | Accommodation fees already paid | £100.00 (limited to £1265.00)                                               |
            | Number of dependants            | 0                                                     |
            | Sort code                       | 22-22-23                                              |
            | Account number                  | 22222223                                              |
