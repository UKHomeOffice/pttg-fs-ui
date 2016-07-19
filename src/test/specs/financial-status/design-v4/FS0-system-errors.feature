Feature: System errors - specify messages shown in response to (simulated) connection failures etc

    Background:
        Given caseworker is using the financial status service ui
        And the doctorate student type is chosen


    Scenario: Sensible connection timeout
        Given the api response is delayed for 10 seconds
        When the financial status check is performed
        Then the service displays the following page content within 6 seconds
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |

    Scenario: Coping with a garbage response
        Given the api response is garbage
        When the financial status check is performed
        Then the service displays the following page content
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |

    Scenario: Coping with an empty response
        Given the api response is empty
        When the financial status check is performed
        Then the service displays the following page content
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |

    Scenario: Coping with an unexpected HTTP response status
        Given the api response has status 503
        When the financial status check is performed
        Then the service displays the following page content
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |

    Scenario: Coping when the API is down
        Given the api is unreachable
        When the financial status check is performed
        Then the service displays the following page content
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |

    Scenario: Handling API server validation errors - missing parameter
        Given the api response is a validation error - missing parameter
        When the financial status check is performed
        Then the service displays the following page content
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |

    Scenario: Handling API server validation errors - invalid parameter
        Given the api response is a validation error - invalid parameter
        When the financial status check is performed
        Then the service displays the following page content
            | Server Error        | You can’t use this service just now. The problem will be fixed as soon as possible |
            | Server Error Detail | Please try again later.                                                            |
