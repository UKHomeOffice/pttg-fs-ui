@DataDir=v2 @wiremock
Feature: Total Funds Required Calculation - Tier 4 (General) Student Non Doctorate In London (single current account and no dependants)

    Requirement to meet Tier 4 passed and not passed

    Required Maintenance threshold calculation to pass this feature file
    Maintenance threshold amount- (Required Maintenance funds non doctorate inner London
    borough (£1265) * course length) + total tuition fees

    Scenario: Shelly is a Non Doctorate inner London student and does not have sufficient funds (On a daily basis the
    closing balance in her account is < than the Total funds required - at £16089)
    She has < than the threshold for the previous 28 days
        Given caseworker is using the financial status service ui
        Given the account does not have sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | Inner London borough            | Yes        |
            | Course length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following result
            | Outcome                         | Not Passed               |
            | Total funds required            | £16,090.00               |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016 |
            | Inner London borough            | Yes                      |
            | Course length                   | 6                        |
            | Total tuition fees              | £8,500.00                |
            | Tuition fees already paid       | £0.00                    |
            | Accommodation fees already paid | £0.00                    |
            | Sort code                       | 11-11-11                 |
            | Account number                  | 11111111                 |

    Scenario: Shelly is a Non Doctorate inner London student and has sufficient funds (On a daily basis the closing
    balance in her account is >= than the Total funds required - at £21140.50)
    She has >= than the threshold for the previous 28 days
        Given caseworker is using the financial status service ui
        Given the account has sufficient funds
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | Inner London borough            | Yes        |
            | Course length                   | 9          |
            | Total tuition fees              | 9755.50    |
            | Tuition fees already paid       | 500        |
            | Accommodation fees already paid | 250.50     |
            | Sort code                       | 22-22-22   |
            | Account number                  | 22222222   |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Total funds required       | £20,390.00               |
            | Maintenance period checked | 03/05/2016 to 30/05/2016 |
            | Sort code                  | 22-22-22                 |
            | Account number             | 22222222                 |

