#@design-v1
#@invalid-inputs
Feature: Show clear error details when inputs are invalid


############### Sort code should be valid format ###############

#    Sort Codes should be 3 pairs of digits
#    Sort codes cannot be all 0

    Scenario: User enters an invalid sort code - missing digits
        Given using the financial status service ui
        When the financial status check is performed with
            | Application Raised Date | 01/01/1980 |
            | Total funds required    | 1          |
            | Account Number          | 12345678   |
            | Sort Code               | 20-2-03    |
        Then the service displays the following message
            | Error Message | Please provide a valid sort code |
            | Error Field   | sort-code-error                  |

    Scenario: User enters an invalid sort code - all 0
        Given using the financial status service ui
        When the financial status check is performed with
            | Application Raised Date | 01/01/1980 |
            | Total funds required    | 1          |
            | Account Number          | 12345678   |
            | Sort Code               | 00-00-00   |
        Then the service displays the following message
            | Error Message | Please provide a valid sort code |
            | Error Field   | sort-code-error                  |

############### Sort code should be a Barclays sort code ###############

#    Barclays sort codes have a first pair of 13, 14, or in the range 20-29

    Scenario: User enters a sort code outside the barclays range
        Given using the financial status service ui
        When the financial status check is performed with
            | Application Raised Date | 01/01/1980 |
            | Total funds required    | 1          |
            | Account Number          | 12345678   |
            | Sort Code               | 19-02-03   |
        Then the service displays the following message
            | Error Message | Please provide a valid sort code |
            | Error Field   | sort-code-error                  |


############### Account number should be valid ###############

#    Account numbers are 8 digits
#    Account numbers can't be all 0

    Scenario: User does not enter a bank account number
        Given using the financial status service ui
        When the financial status check is performed with
            | Application Raised Date | 01/01/1980 |
            | Total funds required    | 1          |
            | Account Number          |            |
            | Sort Code               | 20-02-03   |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |

    Scenario: User enters an invalid bank account number - too short
        Given using the financial status service ui
        When the financial status check is performed with
            | Application Raised Date | 01/01/1980 |
            | Total funds required    | 1          |
            | Account Number          | 1234       |
            | Sort Code               | 20-02-03   |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |

    Scenario: User enters an invalid bank account number - too long
        Given using the financial status service ui
        When the financial status check is performed with
            | Application Raised Date | 01/01/1980 |
            | Total funds required    | 1          |
            | Account Number          | 123456789  |
            | Sort Code               | 20-02-03   |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |

    Scenario: User enters an invalid bank account number - all zeroes
        Given using the financial status service ui
        When the financial status check is performed with
            | Application Raised Date | 01/01/1980 |
            | Total funds required    | 1          |
            | Account Number          | 00000000   |
            | Sort Code               | 20-02-03   |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |

    Scenario: User enters an invalid bank account number - not digits
        Given using the financial status service ui
        When the financial status check is performed with
            | Application Raised Date | 01/01/1980 |
            | Total funds required    | 1          |
            | Account Number          | 1234567A   |
            | Sort Code               | 20-02-03   |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |


############### Application raised date is mandatory and must be a valid date not in the future ###############

    Scenario: User does not enter an application raised date
        Given using the financial status service ui
        When the financial status check is performed with
            | Application Raised Date |          |
            | Total funds required    | 1        |
            | Account Number          | 12345678 |
            | Sort Code               | 20-02-03 |
        Then the service displays the following message
            | Error Message | Please provide a valid application raised date |
            | Error Field   | application-raised-date-error                  |

    Scenario: User enters an invalid application raised date
        Given using the financial status service ui
        When the financial status check is performed with
            | Application Raised Date | 50/01/1980 |
            | Total funds required    | 1          |
            | Account Number          | 12345678   |
            | Sort Code               | 20-02-03   |
        Then the service displays the following message
            | Error Message | Please provide a valid application raised date |
            | Error Field   | application-raised-date-error                  |

    Scenario: User enters a future application raised date
        Given using the financial status service ui
        When the financial status check is performed with
            | Application Raised Date | 01/01/9999 |
            | Total funds required    | 1          |
            | Account Number          | 12345678   |
            | Sort Code               | 20-02-03   |
        Then the service displays the following message
            | Error Message | Please provide a valid application raised date |
            | Error Field   | application-raised-date-error                  |
