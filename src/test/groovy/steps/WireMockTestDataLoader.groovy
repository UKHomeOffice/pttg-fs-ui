package steps

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.Fault
import cucumber.api.Scenario
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.github.tomakehurst.wiremock.client.WireMock.*

class WireMockTestDataLoader {


    private static Logger LOGGER = LoggerFactory.getLogger(WireMockTestDataLoader.class);

    def dataDirTagName = "@DataDir="
    def dataDirPath = "src/test/resources/account-data"
    def dataDirName
    def dataDir

    def WireMockServer wireMockServer

    WireMockTestDataLoader(String host, int port) {

        //todo port from test context
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8080))
        wireMockServer.start()
    }

    def prepareFor(Scenario scenario) {

        def dataDirTag = scenario.getSourceTagNames().find {
            it.startsWith(dataDirTagName)
        }

        if (dataDirTag != null) {
            dataDirName = dataDirTag - dataDirTagName

        } else {
            println ''
            LOGGER.warn('WARNING: No data directory specified. Tag the feature to specify the directory containing test data files, eg @DataDir=name')
        }

        dataDir = new File("$dataDirPath/$dataDirName")

        if (!dataDir.isDirectory()) {
            println ''
            LOGGER.warn("WARNING: $dataDir.absolutePath is not a directory. No test data files will be loaded")
        }
    }

    def stubTestData(String fileName, String url) {

        def json = jsonFromFile(fileName)

        if (json == null) {
            assert false: "No test data file was loaded for $fileName from directory $dataDirName - " +
                "Please add it or check filename is correct"
        }

        addStub(fileName, json, url)

    }


    private def jsonFromFile(String fileName) {

        println ''
        LOGGER.debug("Loading test data for $fileName from: $dataDir")

        def file = dataDir.listFiles().find {
            it.name.contains(fileName)
        }

        if (file == null) {
            return null
        }

        return file.text
    }

    private def addStub(String fileName, String json, String url) {

        println ''
        LOGGER.debug("Stubbing Response data with $fileName")

        stubFor(get(urlPathMatching(url))
            .willReturn(aResponse()
            .withBody(json)
            .withHeader("Content-Type", "application/json")
            .withStatus(200)));

        println ''
        LOGGER.debug("Completed Stubbing Response data with $fileName")
    }

    def clearTestData() {
        wireMockServer.stop()
    }


    def withDelayedResponse(String url, int delay) {

        println ''
        LOGGER.debug("Stubbing delayed response for $url of $delay seconds")

        stubFor(get(urlPathMatching(url))
            .willReturn(aResponse()
            .withFixedDelay(delay * 1000)
            .withStatus(200)));

        println ''
        LOGGER.debug("Completed stubbing delayed response")
    }

    def withGarbageResponse(String url) {

        println ''
        LOGGER.debug("Stubbing garbage response for $url")

        stubFor(get(urlPathMatching(url))
            .willReturn(aResponse()
            .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        println ''
        LOGGER.debug("Completed stubbing garbage response")
    }

    def withEmptyResponse(String url) {

        println ''
        LOGGER.debug("Stubbing empty response for $url")

        stubFor(get(urlPathMatching(url))
            .willReturn(aResponse()
            .withFault(Fault.EMPTY_RESPONSE)));

        println ''
        LOGGER.debug("Completed stubbing empty response")
    }

    def withResponseStatus(String url, int status) {

        println ''
        LOGGER.debug("Stubbing response for $url with status $status")

        stubFor(get(urlPathMatching(url))
            .willReturn(aResponse()
            .withStatus(status)));

        println ''
        LOGGER.debug("Completed stubbing response with status")
    }

    def withServiceDown(){
        wireMockServer.stop()
    }
}
