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
        And caseworker is using the financial status calculator service ui
        And the Tier 4 student-type is chosen
        And the non-doctorate student type is chosen
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
            | Course type                     | Main       |

 ######### Overall course <12 months In London #############
    Scenario: Shelly is a Non Doctorate in London student and does not have sufficient funds
        Given the account does not have sufficient funds
        When the financial status check is performed
        Then the service displays the following result
            | Application Raised Date         | 31/05/2016                   |
            | Total funds required            | £16,090.00                   |
            | Course length                   | 7 (limited to 9)             |
            | Applicant type                  | Tier 4 (General) student     |
            | In London                       | Yes                          |
            | Course dates checked            | 30/05/2016 to 30/11/2016     |
            | Total tuition fees              | £8,500.00                    |
            | Tuition fees already paid       | £0.00                        |
            | Accommodation fees already paid | £0.00 (limited to £1,265.00) |
            | Dependants                      | 1                            |
        And the result table contains the following
            | Total funds required     | £16,090.00       |
            | Course length            | 7 (limited to 9) |
            | Estimated Leave End Date | 22/10/2017       |
          #  | Entire course length       | 14                       |

    Scenario: Shelly is a Non Doctorate in London student
        Given the account has sufficient funds
        When the financial status check is performed
        Then the service displays the following result
            | Application Raised Date         | 31/05/2016                   |
            | Total funds required            | £16,090.00                   |
            | Course length                   | 7 (limited to 9)             |
            | Applicant type                  | Tier 4 (General) student     |
            | In London                       | Yes                          |
            | Course dates checked            | 30/05/2016 to 30/11/2016     |
            | Total tuition fees              | £8,500.00                    |
            | Tuition fees already paid       | £0.00                        |
            | Accommodation fees already paid | £0.00 (limited to £1,265.00) |
            | Dependants                      | 1                            |
            | Continuation Course             | No                           |
        And the result table contains the following
            | Total funds required     | £16,090.00       |
            | Course length            | 7 (limited to 9) |
            | Estimated Leave End Date | 22/10/2017       |
            #| Entire course length       | 14                       |

    Scenario: Shelly is a Non Doctorate in London student
        Given the account has sufficient funds
        When the financial status check is performed with
            | Course end date                 | 30/01/2017 |
            | Total tuition fees              | 9755.50    |
            | Tuition fees already paid       | 500        |
            | Accommodation fees already paid | 250.50     |
        Then the service displays the following result
            | Application Raised Date         | 31/05/2016                     |
            | Total funds required            | £16,090.00                     |
            | Course length                   | 9 (limited to 9)               |
            | Applicant type                  | Tier 4 (General) student       |
            | In London                       | Yes                            |
            | Course dates checked            | 30/05/2016 to 30/01/2017       |
            | Total tuition fees              | £9,755.50                      |
            | Tuition fees already paid       | £500.00                        |
            | Accommodation fees already paid | £250.50 (limited to £1,265.00) |
            | Dependants                      | 1                              |
            | Continuation Course             | No                             |
            | Estimated Leave End Date        | 22/10/2017                     |
        And the result table contains the following
            | Total funds required     | £16,090.00       |
            | Course length            | 9 (limited to 9) |
            | Estimated Leave End Date | 22/10/2017       |
            #| Entire course length       | 14                       |

 ###### overall course length 12+ months In London #######

    Scenario: Shelly is a Non Doctorate in London student
        Given the account has sufficient funds
        When the financial status check is performed with
            | Course end date | 30/01/2017 |
        Then the service displays the following result
            | Application Raised Date         | 31/05/2016                   |
            | Total funds required            | £16,090.00                   |
            | Course length                   | 9 (limited to 9)             |
            | Applicant type                  | Tier 4 (General) student     |
            | In London                       | Yes                          |
            | Course dates checked            | 30/05/2016 to 30/01/2017     |
            | Total tuition fees              | £8,500.00                    |
            | Tuition fees already paid       | £0.00                        |
            | Accommodation fees already paid | £0.00 (limited to £1,265.00) |
            | Dependants                      | 1                            |
            | Continuation Course             | No                           |
        And the result table contains the following
            | Total funds required     | £16,090.00       |
            | Course length            | 9 (limited to 9) |
            | Estimated Leave End Date | 22/10/2017       |
            #| Entire course length       | 14                       |

    Scenario: Shelly is a Non Doctorate in London student
        Given the account has sufficient funds
        When the financial status check is performed with
            | Course end date                 | 30/05/2017 |
            | Total tuition fees              | 9755.50    |
            | Tuition fees already paid       | 500        |
            | Accommodation fees already paid | 250.50     |
        Then the service displays the following result
            | Application Raised Date         | 31/05/2016                     |
            | Total funds required            | £16,090.00                     |
            | Course dates checked            | 30/05/2016 to 30/05/2017       |
            | Course length                   | 13 (limited to 9)              |
            | Applicant type                  | Tier 4 (General) student       |
            | In London                       | Yes                            |
            | Total tuition fees              | £9,755.50                      |
            | Tuition fees already paid       | £500.00                        |
            | Accommodation fees already paid | £250.50 (limited to £1,265.00) |
            | Dependants                      | 1                              |
            | Continuation Course             | No                             |
            | Estimated Leave End Date        | 22/10/2017                     |
        And the result table contains the following
            | Total funds required     | £16,090.00        |
            | Course length            | 13 (limited to 9) |
            | Estimated Leave End Date | 22/10/2017        |
            #| Entire course length       | 14                       |
