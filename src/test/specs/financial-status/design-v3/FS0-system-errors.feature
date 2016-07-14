Feature: System errors - specify messages shown in response to (simulated) connection failures etc

    Background:
        Given caseworker is using the financial status service ui
        And the non-doctorate student type is chosen


    Scenario: Connection timeout
        Given the api response is delayed for 10 seconds
        When the financial status check is performed
        Then the service displays the following page content within 6 seconds
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |

    Scenario: Garbage response
        Given the api response is garbage
        When the financial status check is performed
        Then the service displays the following page content
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |

    Scenario: Empty response
        Given the api response is empty
        When the financial status check is performed
        Then the service displays the following page content
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |

    Scenario: Unexpected HTTP status
        Given the api response has status 503
        When the financial status check is performed
        Then the service displays the following page content
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |

    Scenario: API down
        Given the api is unreachable
        When the financial status check is performed
        Then the service displays the following page content
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |
