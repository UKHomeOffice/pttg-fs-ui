Feature: Insufficient Information
    Tool identifies account number, sort code and date of birth combination does not exist with Barclay's

    # error message needs to be reviewed


    Scenario Outline: Consent request output page content
        Given the api health check response has status 200
        And the api consent response will be <Code>
        And caseworker is on page t4/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |
        Then the service displays the following page content
            | Outcome        | <Message> |
            | Outcome detail | <Detail>  |
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |
        Examples:
            | Code      | Message                                                                            | Detail                                                                           |
            | PENDING   | Consent requested                                                                  | Awaiting response from applicant                                                 |
            | INITIATED | Consent requested                                                                  | Awaiting response from applicant                                                 |
            | SUCCESS   | Consent given                                                                      | Applicant has given permission to access their account                           |
            | FAIlURE   | Consent not given                                                                  | Applicant has refused permission to access their account                         |
            | 404       | Invalid or inaccessible account                                                    | One or more of the following conditions prevented us from accessing the account: |
            | 500       | You can’t use this service just now. The problem will be fixed as soon as possible | Please try again later.                                                          |

    Scenario: No records exist within the period stated
        Given the api health check response has status 200
        And the api consent response will be 404
        And caseworker is on page t4/consent
        And consent is sought for the following:
            | DOB            | 25/03/1987 |
            | Sort code      | 22-22-23   |
            | Account number | 22222223   |
        Then the service displays the following page content
            | doNext | Check you have entered the correct information. |
