Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate In London (single current account and no dependants)

    Requirement to meet Tier 4 passed and not passed

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount- (Required Maintenance funds non doctorate in London
    (£1265) * course length) + total tuition fees

    Background:
        Given caseworker is using the financial status service ui
        And the non-doctorate student type is chosen

#Added to Jira PT-27 - Add 'Account holder name' to FSPS UI
    Scenario: Shelly is a Non Doctorate in London student and does not have sufficient funds
        Given the account does not have sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/11/2016 |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
            | DOB                             | 21/09/1981 |
        Then the service displays the following result
            | Outcome                         | Not passed                   |
            | Account holder name             | Shelly Smith                 |
            | Total funds required            | £16,090.00                   |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016     |
            | Course dates checked            | 30/05/2016 to 30/11/2016     |
            | Lowest Balance                  | £100.00 on 03/10/2016        |
            | Student type                    | Tier 4 (General) student     |
            | In London                       | Yes                          |
            | Course length                   | 7 (limited to 9)             |
            | Total tuition fees              | £8,500.00                    |
            | Tuition fees already paid       | £0.00                        |
            | Accommodation fees already paid | £0.00 (limited to £1,265.00) |
            | Number of dependants            | 0                            |
            | Sort code                       | 11-11-11                     |
            | Account number                  | 11111111                     |
            | DOB                             | 21/09/1981                   |

#Added to Jira PT-27 - Add 'Account holder name' to FSPS UI
    Scenario: Shelly is a Non Doctorate in London student and has sufficient funds
        Given the account has sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 28/02/2017 |
            | Total tuition fees              | 9755.50    |
            | Tuition fees already paid       | 500        |
            | Accommodation fees already paid | 250.50     |
            | Number of dependants            | 0          |
            | Sort code                       | 22-22-22   |
            | Account number                  | 22222222   |
            | DOB                             | 06/04/1989 |
        Then the service displays the following result
            | Outcome                         | Passed                         |
            | Account holder name             | Laura Taylor                   |
            | Total funds required            | £16,090.00                     |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016       |
            | Course dates checked            | 30/05/2016 to 28/02/2017       |
            | Student type                    | Tier 4 (General) student       |
            | In London                       | Yes                            |
            | Course length                   | 9 (limited to 9)               |
            | Total tuition fees              | £9,755.50                      |
            | Tuition fees already paid       | £500.00                        |
            | Accommodation fees already paid | £250.50 (limited to £1,265.00) |
            | Number of dependants            | 0                              |
            | Sort code                       | 22-22-22                       |
            | Account number                  | 22222222                       |
            | DOB                             | 06/04/1989                     |
