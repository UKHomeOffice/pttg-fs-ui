Feature: Show clear error details when inputs are invalid

    Fields mandatory to fill in:
    End Date - Format should be dd/mm/yyyy
    Total Funds Required - Format should be
    Sort code - Format should be three pairs of digits 13-56-09 (always numbers 0-9, no letters and cannot be all 0's)
    Account Number - Format should be 12345678 (always 8 numbers, 0-9, no letters, cannot be all 0's)

######################### Validation on the End Date Field #########################

    Scenario: Case Worker does NOT enter End Date
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        |          |
            | Inner London Borough            | Yes      |
            | Course Length                   | 6        |
            | Total tuition fees              | 8500.00  |
            | Tuition fees already paid       | 0        |
            | Accommodation fees already paid | 0        |
            | Sort code                       | 11-11-11 |
            | Account number                  | 11111111 |
        Then the service displays the following message
            | Error Message | Please provide a valid end date |
            | Error Field   | end-date-error                  |

    Scenario: Case Worker enters invalid End Date - in the future
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2099 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid end date |
            | Error Field   | end-date-error                  |

    Scenario: Case Worker enters invalid End date - not numbers 0-9
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/0d/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid end date |
            | Error Field   | end-date-error                  |



######################### Validation on the Sort Code Field #########################

    Scenario: Case Worker does NOT enter Sort Code
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       |            |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid sort code |
            | Error Field   | sort-code-error                  |

    Scenario: Case Worker enters invalid Sort Code - mising digits
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-1    |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid sort code |
            | Error Field   | sort-code-error                  |

    Scenario: Case Worker enters invalid Sort Code - all 0's
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 00-00-00   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid sort code |
            | Error Field   | sort-code-error                  |

    Scenario: Case Worker enters invalid Sort Code - not numbers 0-9
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-1q   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid sort code |
            | Error Field   | sort-code-error                  |


######################### Validation on the Account Number Field #########################

    Scenario: Case Worker does NOT enter Account Number
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  |            |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |

    Scenario: Case Worker enters invalid Account Number - too short
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 1111111    |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |

    Scenario: Case Worker enters invalid Account Number - too long
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 111111111  |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |

    Scenario: Case Worker enters invalid Account Number - all 0's
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 00000000   |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |

    Scenario: Case Worker enters invalid Account Number - not numbers 0-9
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 111a1111   |
        Then the service displays the following message
            | Error Message | Please provide a valid account number |
            | Error Field   | account-number-error                  |


    ######################### Validation on the Inner London Borough Field #########################
    @WIP
    Scenario: Case Worker does NOT enter Inner London Borough
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            |            |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please specify whether the course is inside or outside London |
            | Error Field   | inner-london-borough-error                                    |


######################### Validation on the Course Length Field #########################
    Scenario: Case Worker does NOT enter Course Length
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   |            |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid course length |
            | Error Field   | course-length-error                  |

    Scenario: Case Worker enters invalid Course Length - not numbers 0-9
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | A          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid course length |
            | Error Field   | course-length-error                  |

######################### Validation on the Total tuition fees Field #########################
    Scenario: Case Worker does NOT enter Total tuition fees
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              |            |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid total tuition fees |
            | Error Field   | total-tuition-fees-error                  |

    Scenario: Case Worker enters invalid Total tuition fees - not numbers 0-9
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | A          |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid total tuition fees |
            | Error Field   | total-tuition-fees-error                  |

######################### Validation on the Tuition fees already paid Field #########################
    Scenario: Case Worker does NOT enter Tuition fees already paid
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       |            |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid tuition fees already paid |
            | Error Field   | tuition-fees-already-paid-error                  |

    Scenario: Case Worker enters invalid Tuition fees already paid - not numbers 0-9
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | A          |
            | Accommodation fees already paid | 0          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid tuition fees already paid |
            | Error Field   | tuition-fees-already-paid-error                  |

######################### Validation on the Accommodation fees already paid Field #########################
    Scenario: Case Worker does NOT enter Accommodation fees already paid
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid |            |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid accommodation fees already paid |
            | Error Field   | accommodation-fees-already-paid-error                  |

    Scenario: Case Worker enters invalid Accommodation fees already paid - not numbers 0-9
        Given caseworker is using the financial status service ui
        When the financial status check is performed with
            | End Date                        | 30/05/2016 |
            | Inner London Borough            | Yes        |
            | Course Length                   | 6          |
            | Total tuition fees              | 8500.00    |
            | Tuition fees already paid       | 0          |
            | Accommodation fees already paid | A          |
            | Sort code                       | 11-11-11   |
            | Account number                  | 11111111   |
        Then the service displays the following message
            | Error Message | Please provide a valid accommodation fees already paid |
            | Error Field   | accommodation-fees-already-paid-error                  |




