Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate out of London (single current account and no dependants)

    Requirement to meet Tier 4 passed and not passed

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount- (Required Maintenance funds non doctorate not in London
    (£1015) * course length) + total tuition fees

    Background:
        Given caseworker is using the financial status service ui
        And the non-doctorate student type is chosen

        #Added to Jira PT-27 - Add 'Account holder name' to FSPS UI
    Scenario: Shelly is a Non Doctorate not in London student and does not have sufficient funds
        Given the account does not have sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | In London                       | No         |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 29/07/2016 |
            | Total tuition fees              | 3000.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 33-33-33   |
            | Account number                  | 33333333   |
            | DOB                             | 29/07/1978 |
        Then the service displays the following result
            | Outcome                         | Not passed                   |
            | Total funds required            | £16,090.00                   |
           # | Account holder name             | Shelly Smith                 |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016     |
            | Course dates checked            | 30/05/2016 to 29/07/2016     |
            | Lowest Balance                  | £100.00 on 03/10/2016        |
            | Student type                    | Tier 4 (General) student     |
            | In London                       | No                           |
            | Course length                   | 2 (limited to 9)             |
            | Total tuition fees              | £3,000.00                    |
            | Tuition fees already paid       | £0.00                        |
            | Accommodation fees already paid | £0.00 (limited to £1,265.00) |
            | Number of dependants            | 0                            |
            | Sort code                       | 33-33-33                     |
            | Account number                  | 33333333                     |
            | DOB                             | 29/07/1978                   |

#Added to Jira PT-27 - Add 'Account holder name' to FSPS UI
    Scenario: Shelly is a Non Doctorate not in London student and has sufficient funds
        Given the account has sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | In London                       | No         |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/07/2016 |
            | Total tuition fees              | 15500.00   |
            | Tuition fees already paid       | 100        |
            | Accommodation fees already paid | 1200       |
            | Number of dependants            | 0          |
            | Sort code                       | 44-44-44   |
            | Account number                  | 44444444   |
            | DOB                             | 19/01/1990 |
        Then the service displays the following result
            | Outcome                         | Passed                           |
           # | Account holder name             | Shelly Smith                     |
            | Total funds required            | £16,090.00                       |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016         |
            | Course dates checked            | 30/05/2016 to 30/07/2016         |
            | Student type                    | Tier 4 (General) student         |
            | In London                       | No                               |
            | Course length                   | 3 (limited to 9)                 |
            | Total tuition fees              | £15,500.00                       |
            | Tuition fees already paid       | £100.00                          |
            | Accommodation fees already paid | £1,200.00 (limited to £1,265.00) |
            | Number of dependants            | 0                                |
            | Sort code                       | 44-44-44                         |
            | Account number                  | 44444444                         |
            | DOB                             | 19/01/1990                       |
