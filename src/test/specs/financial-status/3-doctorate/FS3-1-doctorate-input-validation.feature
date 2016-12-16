Feature: Show clear error details when inputs are invalid

    Acceptance criteria

    The same validations for in and out of London

    Fields mandatory to fill in:
    Application Raised Date - numbers only
    End Date - Format should be dd/mm/yyyy and the End date has to be less than 32 days before the application raised date
    Date of birth - should be dd/mm/yyyy (always 8 numbers, 0-9, no letters, cannot be all 0's)
    Dependants - should always be 0 for courses of six months or less ####
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)
    In London - Yes or No options (mandatory)
    Accommodation fees already paid - numbers only. Highest amount £1,265. Format should not contain commas or currency symbols
    Continuation Course - Yes or No

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the doctorate student type is chosen
        And the default details are
            | Application raised date         | 30/05/2016 |
            | End Date                        | 30/05/2016 |
            | In London                       | Yes        |
            | Accommodation fees already paid | 0          |
            | Number of dependants            | 0          |
            | Sort code                       | 111111     |
            | Account number                  | 11111111   |
            | DOB                             | 27/07/1981 |

######################### General validation message display #########################

    Scenario: Error summary details are shown when a validation error occurs
        When the financial status check is performed with
            | Application Raised Date         |  |
            | End Date                        |  |
            | In London                       |  |
            | Accommodation fees already paid |  |
            | Number of dependants            |  |
            | Sort code                       |  |
            | Account number                  |  |
            | DOB                             |  |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information                  |
            | validation-error-summary-text    | Make sure that all the fields have been completed |
        And the error summary list contains the text
            | The application raised date is invalid         |
            | The end date is invalid                        |
            | The in London option is invalid                |
            | The accommodation fees already paid is invalid |
            | The number of dependants is invalid            |
            | The account number is invalid                  |
            | The sort code is invalid                       |
            | The date of birth is invalid                   |

######################### Validation on the Sort Code Field #########################

    Scenario: Case Worker does NOT enter Sort Code
        When the financial status check is performed with
            | Sort code |  |
        Then the service displays the following error message
            | sort Code-error | Enter a valid sort code |

    Scenario: Case Worker enters invalid Sort Code - mising digits
        When the financial status check is performed with
            | Sort code | 11-11-1 |
        Then the service displays the following error message
            | sort Code-error | Enter a valid sort code |

    Scenario: Case Worker enters invalid Sort Code - all 0's
        When the financial status check is performed with
            | Sort code | 00-00-00 |
        Then the service displays the following error message
            | sort Code-error | Enter a valid sort code |

    Scenario: Case Worker enters invalid Sort Code - not numbers 0-9
        When the financial status check is performed with
            | Sort code | 11-11-1q |
        Then the service displays the following error message
            | sort Code-error | Enter a valid sort code |


######################### Validation on the Account Number Field #########################

    Scenario: Case Worker does NOT enter Account Number
        When the financial status check is performed with
            | Account number |  |
        Then the service displays the following error message
            | account number-error | Enter a valid account number |

    Scenario: Case Worker enters invalid Account Number - too short
        When the financial status check is performed with
            | Account number | 1111111 |
        Then the service displays the following error message
            | account number-error | Enter a valid account number |


    Scenario: Case Worker enters invalid Account Number - all 0's
        When the financial status check is performed with
            | Account number | 00000000 |
        Then the service displays the following error message
            | account number-error | Enter a valid account number |

    Scenario: Case Worker enters invalid Account Number - not numbers 0-9
        When the financial status check is performed with
            | Account number | 111a1111 |
        Then the service displays the following error message
            | account number-error | Enter a valid account number |


######################### Validation on the In London Field #########################
    Scenario: Case Worker does NOT enter In London
        When the financial status check is performed with
            | In London |  |
        Then the service displays the following error message
            | In London-error | Select an option |


######################### Validation on the Accommodation fees already paid Field #########################
    Scenario: Case Worker does NOT enter Accommodation fees already paid
        When the financial status check is performed with
            | Accommodation fees already paid |  |
        Then the service displays the following error message
            | Accommodation Fees Already Paid-error | Enter a valid accommodation fees already paid |

    Scenario: Case Worker enters invalid Accommodation fees already paid - not numbers 0-9
        When the financial status check is performed with
            | Accommodation fees already paid | A |
        Then the service displays the following error message
            | Accommodation Fees Already Paid-error | Enter a valid accommodation fees already paid |

 ######################### Validation on the number of dependants Field #########################
    Scenario: Case Worker does NOT enter number of dependants
        When the financial status check is performed with
            | Number of dependants |  |
        Then the service displays the following error message
            | number Of Dependants-error | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid number of dependants - not numbers 0-9
        When the financial status check is performed with
            | Number of dependants | A |
        Then the service displays the following error message
            | number Of Dependants-error | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid number of dependants - negative
        When the financial status check is performed with
            | Number of dependants | -1 |
        Then the service displays the following error message
            | number Of Dependants-error | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid number of dependants - fractional
        When the financial status check is performed with
            | Number of dependants | 1.1 |
        Then the service displays the following error message
            | number Of Dependants-error | Enter a valid number of dependants |

        ######################### Validation on the Date of birth Field #########################

    Scenario: Case Worker does NOT enter Date of birth
        When the financial status check is performed with
            | DOB |  |
        Then the service displays the following error message
            | dob-error | Enter a valid date of birth |

    Scenario: Case Worker enters invalid Date of birth - in the future
        When the financial status check is performed with
            | DOB | 25/09/2099 |
        Then the service displays the following error message
            | dob-error | Enter a valid date of birth |

    Scenario: Case Worker enters invalid Date of birth - not numbers 0-9
        When the financial status check is performed with
            | DOB | 25/08/198x |
        Then the service displays the following error message
            | dob-error | Enter a valid date of birth |

           ######################### Validation on the Application Raised Date Field #########################

    Scenario: Case Worker does NOT enter Application Raised date
        When the financial status check is performed with
            | Application Raised Date |  |
        Then the service displays the following error message
            | Application Raised Date-error | Enter a valid application raised date |

    Scenario: Case Worker enters invalid Course start date - not numbers 0-9
        When the financial status check is performed with
            | Application Raised Date | 30/1d/2016 |
        Then the service displays the following error message
            | Application Raised Date-error | Enter a valid application raised date |

    Scenario: Case Worker enters Original Course Start Date - in the future
        When the financial status check is performed with
            | Application Raised Date | 30/05/2099 |
        Then the service displays the following error message
            | Application Raised Date-error | Enter a valid application raised date |

        ######################### Validation on the End Date Field #########################

    Scenario: Case Worker does NOT enter End Date
        When the financial status check is performed with
            | End Date |  |
        Then the service displays the following error message
            | End Date-error | Enter a valid end date |

    Scenario: Case Worker enters invalid End Date - in the future
        When the financial status check is performed with
            | End Date | 30/05/2099 |
        Then the service displays the following error message
            | End Date-error | Enter a valid end date |

    Scenario: Case Worker enters invalid End date - not numbers 0-9
        When the financial status check is performed with
            | End Date | 30/0d/2016 |
        Then the service displays the following error message
            | End Date-error | Enter a valid end date |

    Scenario: Caseworker enters end date GREATER than 31 days of the Application Raised Date
        When the financial status check is performed with
            | End Date                | 01/02/2016 |
            | Application raised date | 31/01/2016 |
        Then the service displays the following error message
            | End Date-error | Enter a valid end date |

    Scenario: Caseworker enters end date LESS THAN than 31 days of the Application Raised Date
        When the financial status check is performed with
            | End Date                | 31/12/2015 |
            | Application raised date | 31/01/2016 |
        Then the service displays the following error message
            | End Date-error | Enter a valid end date |

