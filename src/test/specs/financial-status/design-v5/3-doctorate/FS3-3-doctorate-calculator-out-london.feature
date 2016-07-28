Feature: Total Funds Required Calculation - Tier 4 (General) Student Doctorate out of London (single current account and no dependants)

    Acceptance criteria

    Requirement to meet Tier 4 Doctorate passed and not passed

    Not In London - The applicant must show evidence of funds to cover £1,015 for each month remaining of the course up to a maximum of 2 months

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate not in London
    (£1015) * remaining course length) -  Accommodation fees already paid

    Background:
        Given caseworker is using the financial status service ui
        And the doctorate student type is chosen


    Scenario: Ann is a Doctorate not in London student and does not have sufficient funds
        Given the account does not have sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | In London                       | No         |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-14   |
            | Account number                  | 11111114   |
        Then the service displays the following result
            | Outcome                         | Not passed                                            |
            | Total funds required            | £16,090.00                                            |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016                              |
            | Course dates checked            | 30/05/2016 to 30/07/2016                              |
            | Minimum Balance Date            | 03/10/2016                                            |
            | Minimum Balance Value           | £100.00                                               |
            | Student type                    | Tier 4 (General) student (doctorate extension scheme) |
            | In London                       | No                                                    |
            | Course length                   | 3                                                     |
            | Accommodation fees already paid | £0.00                                                 |
            | Number of dependants            | 0                                                     |
            | Sort code                       | 11-11-14                                              |
            | Account number                  | 11111114                                              |


    Scenario: Laura is a Doctorate not in London student and has sufficient funds
        Given the account has sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | In London                       | No         |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/06/2016 |
            | Accommodation fees already paid | 265        |
            | Number of dependants            | 0          |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
        Then the service displays the following result
            | Outcome                         | Passed                                                |
            | Total funds required            | £16,090.00                                            |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016                              |
            | Course dates checked            | 30/05/2016 to 30/06/2016                              |
            | Student type                    | Tier 4 (General) student (doctorate extension scheme) |
            | In London                       | No                                                    |
            | Course length                   | 2                                                    |
            | Accommodation fees already paid | £265.00                                               |
            | Number of dependants            | 0                                                     |
            | Sort code                       | 22-22-23                                              |
            | Account number                  | 22222223                                              |
