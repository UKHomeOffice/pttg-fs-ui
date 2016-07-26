Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate In London (single current account and no dependants)

    Requirement to meet Tier 4 passed and not passed

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount- (Required Maintenance funds non doctorate in London
    (£1265) * course length) + total tuition fees

    Background:
        Given caseworker is using the financial status service ui
        And the non-doctorate student type is chosen


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
        Then the service displays the following result
            | Outcome                         | Not passed               |
            | Total funds required            | £16,090.00               |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016 |
            | Course dates checked            | 30/05/2016 to 30/11/2016 |
            | Minimum Balance Date            | 03/10/2016               |
            | Minimum Balance Value           | £100.00                  |
            | Student type                    | Tier 4 (General) student |
            | In London                       | Yes                      |
            | Course length                   | 6                        |
            | Total tuition fees              | £8,500.00                |
            | Tuition fees already paid       | £0.00                    |
            | Accommodation fees already paid | £0.00                    |
            | Number of dependants            | 0                        |
            | Sort code                       | 11-11-11                 |
            | Account number                  | 11111111                 |


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
        Then the service displays the following result
            | Outcome                         | Passed                   |
            | Total funds required            | £16,090.00               |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016 |
            | Course dates checked            | 30/05/2016 to 28/02/2017 |
            | Student type                    | Tier 4 (General) student |
            | In London                       | Yes                      |
            | Course length                   | 9                        |
            | Total tuition fees              | £9,755.50                |
            | Tuition fees already paid       | £500.00                  |
            | Accommodation fees already paid | £250.50                  |
            | Number of dependants            | 0                        |
            | Sort code                       | 22-22-22                 |
            | Account number                  | 22222222                 |

