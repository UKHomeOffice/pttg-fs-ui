package steps

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import cucumber.api.Scenario
import groovyx.net.http.RESTClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

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

}
