Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate out of London (single current account and no dependants)

    Requirement to meet Tier 4 passed and not passed

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount- (Required Maintenance funds non doctorate not in London
    (£1015) * course length) + total tuition fees

    Background:
        Given caseworker is using the financial status service ui
        And the non-doctorate student type is chosen

    Scenario: Shelly is a Non Doctorate not in London student and does not have sufficient funds
        Given the account does not have sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | In London                       | No         |
            | Course length                   | 2          |
            | Total tuition fees              | 3000.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 33-33-33   |
            | Account number                  | 33333333   |
        Then the service displays the following result
            | Outcome                         | Not passed               |
            | Total funds required            | £16,090.00               |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016 |
            | Minimum Balance Date            | 03/10/2016               |
            | Minimum Balance Value           | £100.00                  |
            | Student type                    | Tier 4 (General) student |
            | In London                       | No                       |
            | Course length                   | 2                        |
            | Total tuition fees              | £3,000.00                |
            | Tuition fees already paid       | £0.00                    |
            | Accommodation fees already paid | £0.00                    |
            | Number of dependants            | 0                        |
            | Sort code                       | 33-33-33                 |
            | Account number                  | 33333333                 |


    Scenario: Shelly is a Non Doctorate not in London student and has sufficient funds
        Given the account has sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | In London                       | No         |
            | Course length                   | 2          |
            | Total tuition fees              | 15500.00   |
            | Tuition fees already paid       | 100        |
            | Accommodation fees already paid | 1200       |
            | Number of dependants            | 0          |
            | Sort code                       | 44-44-44   |
            | Account number                  | 44444444   |
        Then the service displays the following result
            | Outcome                         | Passed                   |
            | Total funds required            | £16,090.00               |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016 |
            | Student type                    | Tier 4 (General) student |
            | In London                       | No                       |
            | Course length                   | 2                        |
            | Total tuition fees              | £15,500.00               |
            | Tuition fees already paid       | £100.00                  |
            | Accommodation fees already paid | £1,200.00                |
            | Number of dependants            | 0                        |
            | Sort code                       | 44-44-44                 |
            | Account number                  | 44444444                 |

