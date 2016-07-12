Feature:

    Scenario: Shelly is a Non Doctorate inner London student and does not have sufficient funds (On a daily basis the
    closing balance in her account is < than the Total funds required - at £16089)
    She has < than the threshold for the previous 28 days
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | Inner London borough            | Yes        |
            | Course length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 77777777   |
        Then the service displays the following result
            | Outcome                         | Not passed               |
            | Total funds required            | £16,090.00               |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016 |
            | Inner London borough            | Yes                      |
            | Course length                   | 6                        |
            | Total tuition fees              | £8,500.00                |
            | Tuition fees already paid       | £0.00                    |
            | Accommodation fees already paid | £0.00                    |
            | Sort code                       | 11-11-11                 |
            | Account number                  | 77777777                 |


    Scenario: Shelly is a Non Doctorate inner London student and has sufficient funds (On a daily basis the closing
    balance in her account is >= than the Total funds required - at £21140.50)
    She has >= than the threshold for the previous 28 days
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | Inner London borough            | Yes        |
            | Course length                   | 9          |
            | Total tuition fees              | 9755.50    |
            | Tuition fees already paid       | 500        |
            | Accommodation fees already paid | 250.50     |
            | Sort code                       | 22-22-22   |
            | Account number                  | 88888888   |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Total funds required       | £20,390.00               |
            | Maintenance period checked | 03/05/2016 to 30/05/2016 |
            | Sort code                  | 22-22-22                 |
            | Account number             | 88888888                 |


    Scenario: Shelly is a Non Doctorate not inner London student and does not have sufficient funds
    (On a daily basis the closing balance in her account is < than the Total funds required - at £5029)
    She has < than the threshold for the previous 28 days
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | Inner London borough            | No         |
            | Course length                   | 2          |
            | Total tuition fees              | 3000.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 33-33-33   |
            | Account number                  | 12121212   |
        Then the service displays the following result
            | Outcome                         | Not passed               |
            | Total funds required            | £5,030.00                |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016 |
            | Inner London borough            | No                       |
            | Course length                   | 2                        |
            | Total tuition fees              | £3,000.00                |
            | Tuition fees already paid       | £0.00                    |
            | Accommodation fees already paid | £0.00                    |
            | Sort code                       | 33-33-33                 |
            | Account number                  | 12121212                 |


    Scenario: Shelly is a Non Doctorate not inner London student and has sufficient funds
    (On a daily basis the closing balance in her account is >= than the Total funds required - at £23335)
    She has >= than the threshold for the previous 28 days
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End date                        | 30/05/2016 |
            | Inner London borough            | No         |
            | Course length                   | 9          |
            | Total tuition fees              | 15500.00   |
            | Tuition fees already paid       | 100        |
            | Accommodation fees already paid | 1200       |
            | Sort code                       | 44-44-44   |
            | Account number                  | 13131313   |
        Then the service displays the following result
            | Outcome                    | Passed                   |
            | Total funds required       | £23,335.00               |
            | Maintenance period checked | 03/05/2016 to 30/05/2016 |
            | Sort code                  | 44-44-44                 |
            | Account number             | 13131313                 |


