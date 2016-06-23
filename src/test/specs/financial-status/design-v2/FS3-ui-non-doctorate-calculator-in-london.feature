@WIP
@DataDir=v2
Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate In London (single current account and no dependants)

    Requirement to meet Tier 4 passed and not passed

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount- (Required Maintenance funds non doctorate inner London
    borough (£1265) * course length) + total tuition fees

    Scenario: Shelly is a Non Doctorate inner London student and does not have sufficient funds (On a daily basis the
    closing balance in her account is < than the Total funds required - at £16089)
    She has < than the threshold for the previous 28 days

        Given caseworker is using the financial status service ui
        Given the test data for account 11111111
        When the financial status check is performed with
            | End Date                              | 30/05/2016 |
            | Inner London Borough                  | Yes        |
            | Course Length                         | 6          |
            | Total tuition fees for the first year | 8500.00    |
            | Tuition fees already paid             | 0          |
            | Accommodation fees already paid       | 0          |
            | Sort code                             | 11-11-11   |
            | Account number                        | 11111111   |
        Then the service displays the following result
            | Outcome                    | Not Passed               |
            | Total Funds required       | 16090.00                 |
            | Maintenance Period Checked | 03/05/2016 to 30/05/2016 |
            | Sort code                  | 11-11-11                 |
            | Account number             | 11111111                 |


    Scenario: Shelly is a Non Doctorate inner London student and has sufficient funds (On a daily basis the closing
    balance in her account is >= than the Total funds required - at £20390)
    She has >= than the threshold for the previous 28 days

        Given caseworker is using the financial status service ui
        Given the test data for account 22222222
        When the financial status check is performed with
            | End Date                              | 30/05/2016 |
            | Inner London Borough                  | Yes        |
            | Course Length                         | 9          |
            | Total tuition fees for the first year | 9755.50    |
            | Tuition fees already paid             | 500        |
            | Accommodation fees already paid       | 250.50     |
            | Sort code                             | 22-22-22   |
            | Account number                        | 22222222   |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Total Funds required       | 20390                    |
            | Maintenance Period Checked | 03/05/2016 to 30/05/2016 |
            | Sort Code                  | 22-22-22                 |
            | Account Code               | 22222222                 |

