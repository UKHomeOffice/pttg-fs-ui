Feature: Show clear error details when inputs are invalid

    Acceptance criteria

    Fields mandatory to fill in:
    Main applicant - yes or no
    End date of 90-day period
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)
    Date of birth - should be dd/mm/yyyy (always 8 numbers, 0-9, no letters, cannot be all 0's)

    Background:
        Given the api health check response has status 200
        And caseworker is using the financial status service ui
        And the doctorate student type is chosen
        And the default details are
            | Main applicant       | Yes        |
            | Number of dependants | 0          |
            | Sort code            | 111111     |
            | Account number       | 11111111   |
            | DOB                  | 27/07/1981 |


######################### General validation message display #########################

    Scenario: Error summary details are shown when a validation error occurs
        When the financial status check is performed with
            | Main applicant       |  |
            | Number of dependants |  |
            | Sort code            |  |
            | Account number       |  |
            | DOB                  |  |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information                  |
            | validation-error-summary-text    | Make sure that all the fields have been completed |
        And the error summary list contains the text
            | The main applicant is invalid       |
            | The number of dependants is invalid |
            | The account number is invalid       |
            | The sort code is invalid            |
            | The date of birth is invalid        |

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

  ######################### Validation on the number of main applicant Field #########################

    Scenario: Case Worker does NOT enter number of dependants
        When the financial status check is performed with
            | Main applicant |  |
        Then the service displays the following error message
            | Main applicant-error | Choose a valid response - yes or no |

