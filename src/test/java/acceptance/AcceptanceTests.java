package acceptance;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = {"src/test/specs/financial-status/design-v6"},
    glue = {"steps"},
    tags = {"~@WIP", "~@Demo"})
    //tags = {"~@WIP", "~@Demo", "~@Slow"}) // Exclude slow tests if you want
public class AcceptanceTests {
}
