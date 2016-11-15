Feature: Total Funds Required Calculation - Tier 4 (General) Student (sabbatical officer) In London (single current account with dependants)

    Acceptance criteria

    Requirement to meet Tier 4 Doctorate passed and not passed

    In London - The applicant must show evidence of funds to cover £1,265 for each month remaining of the course up to a maximum of 2 months

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount =  (Required Maintenance funds doctorate in London
    (£1265) * 2) -  Accommodation fees already paid

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the sso student type is chosen


    Scenario: Raj is a sabbatical officer in London student and does not have sufficient funds
        Given the account does not have sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 01/03/2016 |
            | Course end date                 | 20/04/2016 |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-12   |
            | Account number                  | 11111112   |
            | DOB                             | 01/12/1975 |
        Then the service displays the following result
            | Outcome                         | Not passed                                    |
            | Total funds required            | £16,090.00                                    |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016                      |
            | Lowest Balance           | £100.00 on 03/10/2016                         |
            | Student type                    | Tier 4 (General) student union (sabbatical officer) |
            | In London                       | Yes                                           |
            | Course length                   | 2 (limited to 9)                              |
            | Accommodation fees already paid | £0.00 (limited to £1,265.00)                  |
            | Number of dependants            | 0                                             |
            | Sort code                       | 11-11-12                                      |
            | Account number                  | 11111112                                      |
            | DOB                             | 01/12/1975                                    |

    Scenario: Shelly is a sabbatical officer in London student and has sufficient funds
        Given the account has sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 01/03/2016 |
            | Course end date                 | 30/03/2016 |
            | Accommodation fees already paid | 100        |
            | Number of dependants            | 0          |
            | Sort code                       | 22-22-23   |
            | Account number                  | 22222223   |
            | DOB                             | 05/12/1986 |
        Then the service displays the following result
            | Outcome                         | Passed                                        |
            | Total funds required            | £16,090.00                                    |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016                      |
            | Student type                    | Tier 4 (General) student union (sabbatical officer) |
            | In London                       | Yes                                           |
            | Course length                   | 1 (limited to 9)                              |
            | Accommodation fees already paid | £100.00 (limited to £1,265.00)                |
            | Number of dependants            | 0                                             |
            | Sort code                       | 22-22-23                                      |
            | Account number                  | 22222223                                      |
            | DOB                             | 05/12/1986                                    |
