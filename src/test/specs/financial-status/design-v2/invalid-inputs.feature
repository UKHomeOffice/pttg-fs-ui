#@design-v2
#@invalid-inputs
Feature: Show clear error details when inputs are invalid

############### Account number should be 8 digits ###############

    Scenario: User does not enter a bank account number
        Given using the financial status service ui
        When the financial status check is performed with
            | Applicant Date Of Birth Date | 01/01/1980 |
            | Application Raised Date      | 01/01/1980 |
            | Account Number               |            |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |

    Scenario: User enters an invalid bank account number - too short
        Given using the financial status service ui
        When the financial status check is performed with
            | Applicant Date Of Birth Date | 01/01/1980 |
            | Application Raised Date      | 01/01/1980 |
            | Account Number               | 1234       |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |

    Scenario: User enters an invalid bank account number - too long
        Given using the financial status service ui
        When the financial status check is performed with
            | Applicant Date Of Birth Date | 01/01/1980 |
            | Application Raised Date      | 01/01/1980 |
            | Account Number               | 123456789  |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |

    Scenario: User enters an invalid bank account number - not digits
        Given using the financial status service ui
        When the financial status check is performed with
            | Applicant Date Of Birth Date | 01/01/1980 |
            | Application Raised Date      | 01/01/1980 |
            | Account Number               | 1234567A   |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |


############### Application raised date is mandatory and must be a valid date not in the future ###############

    Scenario: User does not enter an application raised date
        Given using the financial status service ui
        When the financial status check is performed with
            | Applicant Date Of Birth Date | 01/01/1980 |
            | Application Raised Date      |            |
            | Account Number               | 12345678   |
        Then the service displays the following message
            | Error Message | Please provide a valid application raised date |
            | Error Field   | application-raised-date-error                  |

    Scenario: User enters an invalid application raised date
        Given using the financial status service ui
        When the financial status check is performed with
            | Applicant Date Of Birth Date | 01/01/1980 |
            | Application Raised Date      | 50/01/1980 |
            | Account Number               | 12345678   |
        Then the service displays the following message
            | Error Message | Please provide a valid application raised date |
            | Error Field   | application-raised-date-error                  |

    Scenario: User enters a future application raised date
        Given using the financial status service ui
        When the financial status check is performed with
            | Applicant Date Of Birth Date | 01/01/1980 |
            | Application Raised Date      | 01/01/9999 |
            | Account Number               | 12345678   |
        Then the service displays the following message
            | Error Message | Please provide a valid application raised date |
            | Error Field   | application-raised-date-error                  |
