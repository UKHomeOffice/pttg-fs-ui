Feature: Total Funds Required Calculation - Tier 4 New (General) Student Non Doctorate In London (single current account)

    Applicants Required Maintenance period - Course length (capped to 9 months)
    Dependants Required Maintenance period - Continuation course length +2 (when entire course <12 months) or +4 months (when entire course 12+) (capped to 9 months) ##Dependants Required Maintenance period  - Course length + 2 months (capped to 9 months)
    Course length - this can be 7+ months ##this reflects the October 2016 policy change
    Total tuition fees - total amount of the tuition fees for the course
    Tuition fees already paid - total amount of tuition fees already paid
    Accommodation fees already paid - The maximum amount paid can be £1265

    Maintenance threshold amount = (Required Maintenance threshold non doctorate In London * Course length) + ((Dependants Required Maintenance threshold In London * Dependants Required Maintenance period)  * Dependants) + (total tuition fees - tuition fees paid - accommodation fees paid)

#   Requirement to meet Tier 4 pass (Continuation applications only)
#
#   Applicants Required Maintenance threshold non doctorate:  In London - £1265, Out London - £1015
#   Dependants Required Maintenance threshold: In London - £845, Out London - £680
#
#   Entire course <12 months ((£1265 x 4) + (845 x (4+2) x 1) + (£10,000 - 0 - 0)) ##
#   Entire course 12+ months ((£1265 x 4) + (845 x (4+4) x 1) + (£10,000 - 0 - 0)) ##
#
#   Entire course <12 months
#   Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 3) + (£845 x (3+2) x 1) + (£10,000 - £0 - £0) = £18,020
#   Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 8) + (£845 x (8+2) x 2) + (£7,000 - £300 - £500.50) = £31,529.50 (dependant require maintenance period capped at 9 months)
#
#    Entire course 12+ months
#    Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 3) + (£845 x (3+4) x 1) + (£10,000 - £0 - £0) = £19,710.00
#    Tier 4 (General) Student - non doctorate - In London, with dependents In Country - (£1265 x 8) + (£845 x (8+4) x 2) + (£7,000 - £300 - £500.50) = £31,529.50 (dependant require maintenance period capped at 9 months)


    Background:
        Given the api health check response has status 200
        And the api consent response will be SUCCESS
        And the api threshold response will be t4
        And the api daily balance response will Pass
        And caseworker is using the financial status service ui
        And caseworker is on page t4/general/consent
        And consent is sought for the following:
            | DOB            | 21/09/1981 |
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
        And the default details are
            | Application raised date         | 31/05/2016 |
            | End date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Course start date               | 30/05/2016 |
            | Course end date                 | 30/11/2016 |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Dependants                      | 1          |
            | Continuation Course             | No         |
            | Original Course Start Date      | 30/10/2015 |
            | Course type                     | main       |
            | Course institution              | true       |

 ######### Overall course <12 months In London #############

    Scenario: Shelly is a Non Doctorate in London student and does not have sufficient funds
        Given the api daily balance response will Fail-low-balance
        And the api condition codes response will be 2-3-1
        When the financial status check is performed
        Then the service displays the following result
            | Outcome                         | Not passed                   |
            | Application Raised Date         | 31/05/2016                   |
            | Tier                            | Tier 4 (General)             |
            | Applicant type                  | General student              |
            | In London                       | Yes                          |
            | Course dates checked            | 30/05/2016 to 30/11/2016     |
            | Total tuition fees              | £8,500.00                    |
            | Tuition fees already paid       | £0.00                        |
            | Accommodation fees already paid | £0.00 (limited to £1,265.00) |
            | Dependants                      | 1                            |
            | Continuation Course             | No                           |
        And the result table contains the following
            | Account holder name        | Shelly Smith                                            |
            | Total funds required       | £16,090.00                                              |
            | Maintenance period checked | 03/05/2016 to 30/05/2016                                |
            | Condition Code             | 2 - Applicant\n3 - Adult dependant\n1 - Child dependant |
            | Lowest balance             | £100.00 on 03/10/2016                                   |
            | Estimated Leave End Date   | 22/10/2017                                              |
            | Course length              | 7 (limited to 9)                                        |



   # //*[@id="resultsTable"]/tbody/tr[1]/td
    Scenario: Shelly is a Non Doctorate in London student and has sufficient funds
        Given the account has sufficient funds
        And the api condition codes response will be 2a-3-1
        When the financial status check is performed with
            | Application Raised Date         | 31/05/2016   |
            | In London                       | Yes          |
            | Dependants                      | 1            |
            | End date                        | 01/05/2016   |
            | Course end date                 | 30/01/2017   |
            | Total tuition fees              | 9755.50      |
            | Tuition fees already paid       | 500          |
            | Course start date               | 01/05/2016   |
            | Accommodation fees already paid | 250.50       |
            | Continuation Course             | Yes          |
            | Original Course Start Date      | 30/10/2015   |
            | Course type                     | below-degree |
        Then the service displays the following result
            | Outcome                         | Passed                         |
            | Application Raised Date         | 31/05/2016                     |
            | Tier                            | Tier 4 (General)               |
            | Applicant type                  | General student                |
            | In London                       | Yes                            |
            | Course dates checked            | 01/05/2016 to 30/01/2017       |
            | Total tuition fees              | £9,755.50                      |
            | Tuition fees already paid       | £500.00                        |
            | Accommodation fees already paid | £250.50 (limited to £1,265.00) |
            | Dependants                      | 1                              |
            | Continuation Course             | Yes                            |
        And the result table contains the following
            | Account holder name        | Laura Taylor                                             |
            | Total funds required       | £16,090.00                                               |
            | Maintenance period checked | 04/04/2016 to 01/05/2016                                 |
            | Condition Code             | 2A - Applicant\n3 - Adult dependant\n1 - Child dependant |
            | Estimated Leave End Date   | 22/10/2017                                               |
            | Course length              | 9 (limited to 9)                                         |
            | Entire course length       | 16                                                       |


 ###### overall course length 12+ months In London #######

    Scenario: Shelly is a Non Doctorate in London student and does not have sufficient funds2
        Given the account does not have sufficient funds
        And the api condition codes response will be 2-4B-1
        When the financial status check is performed with
            | Application Raised Date         | 31/05/2016 |
            | In London                       | Yes        |
            | Dependants                      | 1          |
            | End date                        | 30/05/2016 |
            | Course end date                 | 30/01/2017 |
            | Total tuition fees              | 9755.50    |
            | Tuition fees already paid       | 0          |
            | Continuation Course             | No         |
            | Course start date               | 01/05/2016 |
            | Accommodation fees already paid | 0          |
            | Continuation Course             | Yes        |
            | Original Course Start Date      | 30/10/2015 |
        Then the service displays the following result
            | Outcome                         | Not passed                   |
            | Application Raised Date         | 31/05/2016                   |
            | Account holder name             | Shelly Smith                 |
            | Total funds required            | £16,090.00                   |
            | Maintenance period checked      | 03/05/2016 to 30/05/2016     |
            | Course length                   | 9 (limited to 9)             |
            | Lowest Balance                  | £100.00 on 03/10/2016        |
            | Tier                            | Tier 4 (General)             |
            | Applicant type                  | General student              |
            | In London                       | Yes                          |
            | Course dates checked            | 01/05/2016 to 30/01/2017     |
            | Total tuition fees              | £9,755.50                    |
            | Tuition fees already paid       | £0.00                        |
            | Accommodation fees already paid | £0.00 (limited to £1,265.00) |
            | Dependants                      | 1                            |
            | Sort code                       | 11-11-11                     |
            | Account number                  | 11111111                     |
            | DOB                             | 21/09/1981                   |
            | Continuation Course             | Yes                          |
        And the result table contains the following
            | Account holder name        | Shelly Smith                                             |
            | Total funds required       | £16,090.00                                               |
            | Maintenance period checked | 03/05/2016 to 30/05/2016                                 |
            | Condition code             | 2 - Applicant\n4B - Adult dependant\n1 - Child dependant |
            | Lowest Balance             | £100.00 on 03/10/2016                                    |
            | Estimated Leave End Date   | 22/10/2017                                               |
            | Course length              | 9 (limited to 9)                                         |
            | Entire course length       | 16                                                       |


    Scenario: Shelly is a Non Doctorate in London student and has sufficient funds other institution
        Given the account has sufficient funds
        And the api condition codes response will be 3-3-1
        When the financial status check is performed with
            | Application Raised Date         | 31/05/2016 |
            | In London                       | Yes        |
            | Dependants                      | 1          |
            | Course start date               | 01/05/2016 |
            | End date                        | 01/05/2016 |
            | Course end date                 | 30/05/2017 |
            | Total tuition fees              | 9755.50    |
            | Tuition fees already paid       | 500        |
            | Course institution              | false      |
            | Continuation Course             | No         |
            | Accommodation fees already paid | 250.50     |
        Then the service displays the following result
            | Outcome                         | Passed                         |
            | Application Raised Date         | 31/05/2016                     |
            | Course dates checked            | 01/05/2016 to 30/05/2017       |
            | Tier                            | Tier 4 (General)               |
            | Applicant type                  | General student                |
            | In London                       | Yes                            |
            | Total tuition fees              | £9,755.50                      |
            | Tuition fees already paid       | £500.00                        |
            | Accommodation fees already paid | £250.50 (limited to £1,265.00) |
            | Dependants                      | 1                              |
            | Sort code                       | 11-11-11                       |
            | Account number                  | 11111111                       |
            | DOB                             | 21/09/1981                     |
            | Continuation Course             | No                             |
            | Estimated Leave End Date        | 22/10/2017                     |
        And the result table contains the following
            | Account holder name        | Laura Taylor                                            |
            | Total funds required       | £16,090.00                                              |
            | Maintenance period checked | 04/04/2016 to 01/05/2016                                |
            | Condition Code             | 3 - Applicant\n3 - Adult dependant\n1 - Child dependant |
            | Course length              | 13 (limited to 9)                                       |
            | Estimated Leave End Date   | 22/10/2017                                              |

######### Overall course <12 months In London - dependant only - not pass #############

    Scenario: Luiz is a dependant only application (Non Doctorate in London student and does not have sufficient funds)

        Given the api daily balance response will Fail-low-balance
        And caseworker is on page t4/general-dependants/bank/details
        And the api condition codes response will be -3-1
        When the financial status check is performed
        Then the service displays the following result
            | Outcome                 | Not passed               |
            | Application Raised Date | 31/05/2016               |
            | Tier                    | Tier 4 (General)         |
            | Applicant type          | General student          |
            | In London               | Yes                      |
            | Course dates checked    | 30/05/2016 to 30/11/2016 |
            | Dependants              | 1                        |
            | Continuation Course     | No                       |
        And the result table contains the following
            | Account holder name        | Shelly Smith                             |
            | Total funds required       | £16,090.00                               |
            | Maintenance period checked | 03/05/2016 to 30/05/2016                 |
            | Condition Code             | 3 - Adult dependant\n1 - Child dependant |
            | Lowest balance             | £100.00 on 03/10/2016                    |
            | Estimated Leave End Date   | 22/10/2017                               |
            | Course length              | 7 (limited to 9)                         |

######### Overall course <12 months In London - dependant only - pass #############

    Scenario: Deigo and Edin is a dependant only application (x2) Non Doctorate in London student and has sufficient funds

        Given the account has sufficient funds
        And caseworker is on page t4/general-dependants/bank/details
        And the api condition codes response will be -3-1
        When the financial status check is performed with
            | Application Raised Date    | 31/05/2016 |
            | In London                  | Yes        |
            | Dependants                 | 2          |
            | End date                   | 01/05/2016 |
            | Course end date            | 30/01/2017 |
            | Course start date          | 01/05/2016 |
            | Continuation Course        | Yes        |
            | Original Course Start Date | 30/10/2015 |
        Then the service displays the following result
            | Outcome                 | Passed                   |
            | Application Raised Date | 31/05/2016               |
            | Tier                    | Tier 4 (General)         |
            | Applicant type          | General student          |
            | In London               | Yes                      |
            | Course dates checked    | 01/05/2016 to 30/01/2017 |
            | Dependants              | 2                        |
            | Continuation Course     | Yes                      |
        And the result table contains the following
            | Account holder name        | Laura Taylor                             |
            | Total funds required       | £16,090.00                               |
            | Maintenance period checked | 04/04/2016 to 01/05/2016                 |
            | Condition Code             | 3 - Adult dependant\n1 - Child dependant |
            | Estimated Leave End Date   | 22/10/2017                               |
            | Course length              | 9 (limited to 9)                         |
            | Entire course length       | 16                                       |


 ###### overall course length 12+ months In London - dependant only - not pass #######

    Scenario: Neymar is a dependant only application (Non Doctorate in London student and does not have sufficient funds

        Given the account does not have sufficient funds
        And caseworker is on page t4/general-dependants/bank/details
        And the api condition codes response will be -3-1
        When the financial status check is performed with
            | Application Raised Date    | 31/05/2016 |
            | In London                  | Yes        |
            | Dependants                 | 1          |
            | End date                   | 30/05/2016 |
            | Course end date            | 30/01/2017 |
            | Continuation Course        | No         |
            | Course start date          | 01/05/2016 |
            | Continuation Course        | Yes        |
            | Original Course Start Date | 30/10/2015 |
        Then the service displays the following result
            | Outcome                    | Not passed               |
            | Application Raised Date    | 31/05/2016               |
            | Account holder name        | Shelly Smith             |
            | Total funds required       | £16,090.00               |
            | Maintenance period checked | 03/05/2016 to 30/05/2016 |
            | Course length              | 9 (limited to 9)         |
            | Lowest Balance             | £100.00 on 03/10/2016    |
            | Tier                       | Tier 4 (General)         |
            | Applicant type             | General student          |
            | In London                  | Yes                      |
            | Course dates checked       | 01/05/2016 to 30/01/2017 |
            | Dependants                 | 1                        |
            | Sort code                  | 11-11-11                 |
            | Account number             | 11111111                 |
            | DOB                        | 21/09/1981               |
            | Continuation Course        | Yes                      |
        And the result table contains the following
            | Account holder name        | Shelly Smith                             |
            | Total funds required       | £16,090.00                               |
            | Maintenance period checked | 03/05/2016 to 30/05/2016                 |
            | Condition code             | 3 - Adult dependant\n1 - Child dependant |
            | Lowest Balance             | £100.00 on 03/10/2016                    |
            | Estimated Leave End Date   | 22/10/2017                               |
            | Course length              | 9 (limited to 9)                         |
            | Entire course length       | 16                                       |


    ###### overall course length 12+ months In London - dependant only - pass #######

    Scenario: Alexis and Arsene is a dependant only (x2) application (Non Doctorate in London student and has sufficient funds)

        Given the account has sufficient funds
        And caseworker is on page t4/general-dependants/bank/details
        And the api condition codes response will be -3-1
        When the financial status check is performed with
            | Application Raised Date | 31/05/2016 |
            | In London               | Yes        |
            | Dependants              | 2          |
            | Course start date       | 01/05/2016 |
            | End date                | 01/05/2016 |
            | Course end date         | 30/05/2017 |
            | Continuation Course     | No         |
        Then the service displays the following result
            | Outcome                  | Passed                   |
            | Application Raised Date  | 31/05/2016               |
            | Course dates checked     | 01/05/2016 to 30/05/2017 |
            | Tier                     | Tier 4 (General)         |
            | Applicant type           | General student          |
            | In London                | Yes                      |
            | Dependants               | 2                        |
            | Sort code                | 11-11-11                 |
            | Account number           | 11111111                 |
            | DOB                      | 21/09/1981               |
            | Continuation Course      | No                       |
            | Estimated Leave End Date | 22/10/2017               |
        And the result table contains the following
            | Account holder name        | Laura Taylor                             |
            | Total funds required       | £16,090.00                               |
            | Maintenance period checked | 04/04/2016 to 01/05/2016                 |
            | Condition Code             | 3 - Adult dependant\n1 - Child dependant |
            | Course length              | 13 (limited to 9)                        |
            | Estimated Leave End Date   | 22/10/2017                               |
