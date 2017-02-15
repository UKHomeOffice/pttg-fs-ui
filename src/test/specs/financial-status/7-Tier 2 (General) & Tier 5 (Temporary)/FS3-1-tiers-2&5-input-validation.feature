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
        And the api consent response will be PENDING
        And the api threshold response will be t2
        And caseworker is using the financial status service ui
        And the caseworker selects Tier two
        And Main applicant type is selected
        And the caseworker selects the Yes, check Barclays radio button
        And consent is sought for the following:
            | Sort code      | 11-11-11   |
            | Account number | 11111111   |
            | DOB            | 27/07/1981 |
        And the default details are
            | Dependants | 0          |
            | End Date   | 30/05/2016 |


######################### General validation message display #########################

    Scenario: Error summary details are shown when a validation error occurs
        When the financial status check is performed with
            | Dependants |  |
        Then the service displays the following message
            | validation-error-summary-heading | There's some invalid information                  |
            | validation-error-summary-text    | Make sure that all the fields have been completed |
        And the error summary list contains the text
            | The number of dependants is invalid |

 ######################### Validation on the Dependants Field #########################

    Scenario: Case Worker does NOT enter Dependants
        When the financial status check is performed with
            | Dependants |  |
        Then the service displays the following error message
            | Dependants-error | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid Dependants - not numbers 0-9
        When the financial status check is performed with
            | Dependants | A |
        Then the service displays the following error message
            | Dependants-error | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid Dependants - negative
        When the financial status check is performed with
            | Dependants | -1 |
        Then the service displays the following error message
            | Dependants-error | Enter a valid number of dependants |

    Scenario: Case Worker enters invalid Dependants - fractional
        When the financial status check is performed with
            | Dependants | 1.1 |
        Then the service displays the following error message
            | Dependants-error | Enter a valid number of dependants |

