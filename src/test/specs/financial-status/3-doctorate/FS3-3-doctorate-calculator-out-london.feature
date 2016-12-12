Feature: Total Funds Required Calculation - Tier 4 (General) Student Doctorate out of London (single current account including dependants)

    Acceptance criteria

    Requirement to meet Tier 4 Doctorate passed and not passed

    Not In London - The applicant must show evidence of funds to cover £1,015 for each month for 2 months (£2,030)

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate not in London
    (£1015) * 2) -  Accommodation fees already paid

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the doctorate student type is chosen

#Added to Jira PT-27 - Add 'Account holder name' to FSPS UI
    Scenario: Laura is a Doctorate not in London student and has sufficient funds
        Given the account has sufficient funds
        When the financial status check is performed with
            | Application raised date         | 20/06/2016 |
            | End date                        | 30/05/2016 |
            | In London                       | No         |
            | Accommodation fees already paid | 265        |
            | Number of dependants            | 0          |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
            | DOB                             | 02/12/1985 |
        Then the service displays the following result
            | Outcome                         | Passed                                                |
            | Account holder name             | Laura Taylor                                          |
            | Total funds required            | £16,090.00                                            |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016                              |
            | Student type                    | Tier 4 (General) student (doctorate extension scheme) |
            | In London                       | No                                                    |
            | Accommodation fees already paid | £265.00 (limited to £1,265.00)                        |
            | Number of dependants            | 0                                                     |
            | Sort code                       | 22-22-23                                              |
            | Account number                  | 22222223                                              |
            | DOB                             | 02/12/1985                                            |
