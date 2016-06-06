package acceptance;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features={"src/test/specs/financial-status/design-v1"}, glue={"steps"}/*,  tags = {"@SD63"}*/)
public class AcceptanceTests {}
